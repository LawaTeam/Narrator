package me.sakuratao.narrator.spigot.handlers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import eu.endercentral.crazy_advancements.JSONMessage;
import eu.endercentral.crazy_advancements.advancement.Advancement;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.NarratorSpigot;
import me.sakuratao.narrator.spigot.configuration.Lang;
import me.sakuratao.narrator.spigot.data.Player.PlayerData;
import me.sakuratao.narrator.spigot.data.cache.CacheData;
import me.sakuratao.narrator.spigot.enums.DelayStatus;
import me.sakuratao.narrator.spigot.enums.OptionStatus;
import me.sakuratao.narrator.spigot.enums.PrintStatus;
import me.sakuratao.narrator.spigot.task.ContentTask;
import me.sakuratao.narrator.spigot.utils.CCUtil;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.utils.LogUtil;
import me.sakuratao.narrator.spigot.utils.PacketUtil;
import me.sakuratao.narrator.spigot.utils.ToastUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

@PieComponent
public class ContentHandler {

    @Wire
    private Narrator narrator;

    /**
     * 执行对 content 的解析
     * @param player - 玩家
     * @param chapterData - 章节数据
     * @param content - 内容
     * @param contentTask - contentTask
     * @return 执行完毕
     */
    public boolean execute(Player player, ChapterData chapterData, String content, ContentTask contentTask) {

        List<String> contentList = Arrays.asList(content.split("\\|"));

        PlayerData playerData = narrator.getManagerHandler().getPlayerManager().getByPlayer(player);



        /*
            TODO: 物品栏文字调用，生成剧情对话背包，

            TODO: MESSAGE_CLICK、MESSAGE_DROP、INV_ANSWER、CONDITION、SOUND(播放声音)、BOOM_AROUND、SPAWN_ENTITY

            TODO: 玩家自定义字幕速度以及停留时间，并提供 " 上一条 " 的功能

         */


        /*
         * 这里会解析 content 的内容
         * contentList.get(0) 即是所对应的功能
         * 其余的则是所对应功能的参数，详细请翻阅 ChapterExample.yml
         */
        try {
            switch (contentList.get(0)) {
                case "T":
                case "TITLE": {
                    showTitle(contentList, player);
                    return true;
                }
                case "M":
                case "MESSAGE": {
                    player.sendMessage(CCUtil.translate(contentList.get(1)));
                    return true;
                }
                case "AB":
                case "ACTIONBAR": {
                    return showActionbar(contentList, player, contentTask);
                }
                case "AB_ANSWER":
                case "ACTIONBAR_ANSWER": {
                    return answerByActionbar(contentList, narrator, player, playerData, contentTask);
                }
                case "D":
                case "DELAY": {
                    if (contentList.size() > 2){
                        delaySingle(contentList, player, chapterData, contentTask);
                        return true;
                    }
                    return delayOverall(contentList, contentTask);
                }
                case "COMMAND": {
                    narrator.getLogger().log(Level.WARNING, "Executed command: " + contentList.get(1) + " | Chapter: " + chapterData.getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), contentList.get(1));
                    return true;
                }
                case "MC":
                case "MESSAGE_CLICK": {
                    // todo
                    return true;
                }
                case "JT":
                case "JUMP_TASK": {
                    return jumpTask(contentList, playerData, chapterData, content);
                }
                case "TOAST":{
                    showToast(contentList, player);
                    return true;
                }
                case "WEATHER": {
                    changeWeather(contentList.get(1), player);
                    return true;
                }
                case "C":
                case "CONDITION": {
                    // todo
                    return true;
                }
                case "JC":
                case "JUMP_CHAPTER": {
                    // todo
                    return true;
                }
                default: {
                    return true;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            narrator.getLogger().log(Level.SEVERE, "Throw IndexOutOfBoundsException!");
            narrator.getLogger().log(Level.SEVERE, "Please check your contents!");
            narrator.getLogger().log(Level.SEVERE, "Here are some information may help you:");
            narrator.getLogger().log(Level.SEVERE, "        Chapter Name: " + chapterData.getName());
            narrator.getLogger().log(Level.SEVERE, "        Content: " + content);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更改玩家的天气
     * @param weather - 天气
     * @param player - 玩家
     */
    private void changeWeather(String weather, Player player){
        if (weather.equalsIgnoreCase("CLEAR")) {
            player.resetPlayerWeather();
            return;
        }

        PacketContainer weatherPacket = PacketUtil.createPacket(PacketType.Play.Server.GAME_STATE_CHANGE);
        if (weather.equalsIgnoreCase("SUNSHINE")) {
            player.resetPlayerWeather();
        }
        if (weather.equalsIgnoreCase("THUNDER")) {

            //weatherPacket.getGameStateIDs().write(0, 2);
            //PacketUtil.sendPacket(player, weatherPacket);

            weatherPacket.getGameStateIDs().write(0, 7);
            weatherPacket.getModifier().write(1, 1);
            PacketUtil.sendPacket(player, weatherPacket);
        }
        if (weather.equalsIgnoreCase("RAINING")) {
            weatherPacket.getGameStateIDs().write(0, 2);
            weatherPacket.getModifier().write(1, 0.5F);
        }
        PacketUtil.sendPacket(player, weatherPacket);
    }

    /**
     * 向玩家发送 toast 形式的信息
     * @param contentList - 切割的content
     * @param player - 玩家
     */
    private void showToast(List<String> contentList, Player player){

        String[] t = contentList.get(3).split("<br>");
        StringBuilder sb = new StringBuilder();

        /*
            这段屎山，没事别碰，因为实在是太乱了
            主要是让单句文字不串行
         */
        for (int i = 0; i < t.length; i ++) {
            String t1 = t[i];
            if (i > 2 && i == t.length - 1) {
                if (t[i-1].getBytes().length >= 21) {
                    sb.append(" ").append(t1);
                } else {
                    sb.append(t1);
                }
                break;
            }
            for (int k = 0; k < t1.length(); k++) {
                if (i > 2) {
                    char ch = t1.charAt(k);
                    if (ch == ' ') {
                        sb.append(" ");
                    }
                }
            }
            sb.append(t1);
            if (t1.getBytes().length <= 26) {
                sb.append(" ".repeat((26 - t1.getBytes().length)));
            }
        }

        ToastUtil.showToast(player, Material.valueOf(contentList.get(2)), sb.toString(), contentList.get(1));

    }

    /**
     * 给玩家发送 title
     * @param contentList - 总content
     * @param player - 玩家
     */
    private void showTitle(List<String> contentList, Player player) {
        player.sendTitle(
                CCUtil.translate(contentList.get(4)), CCUtil.translate(contentList.get(5)),
                Integer.parseInt(contentList.get(1)), Integer.parseInt(contentList.get(2)), Integer.parseInt(contentList.get(3))
        );
    }

    /**
     * 给玩家发送 actionbar
     *
     * actionbar 发送过程中会影响整体进程
     *
     * @param contentList - 总content
     * @param player - 玩家
     * @param contentTask - contentTask
     * @return 发送成功
     */
    private boolean showActionbar(List<String> contentList, Player player, ContentTask contentTask) {
        Audience audience = narrator.getAdventure().player(player);
        if (!Boolean.parseBoolean(contentList.get(1))) {
            audience.sendActionBar(Component.text(CCUtil.translate(contentList.get(2))));
            return true;
        }

                    /*
                      这里会对 text 处理成一个打字机的效果
                      text将会在 delay 限定的时间内完成逐字打印
                     */
        String text = contentList.get(4);

        if (contentTask.getPrintStatus().equals(PrintStatus.NONE)) {
            contentTask.setPrintStatus(PrintStatus.PRINTING);

            long delayTime = Long.parseLong(contentList.get(2));
            AtomicInteger textLength = new AtomicInteger(0);

            contentTask.setPrintTask(
                    Bukkit.getScheduler().runTaskTimerAsynchronously(NarratorSpigot.getPluginInstance(), () -> {
                        if (textLength.get() >= text.length()) {
                            contentTask.setPrintStatus(PrintStatus.KEEPING);
                            return;
                        }

                        textLength.set(textLength.get() + 1);
                        String outputText = text.substring(0, textLength.get());

                        if (textLength.get() % 2 == 0 && textLength.get() != text.length()) {
                            audience.sendActionBar(Component.text(CCUtil.translate(outputText + "&kA&r_")));
                            return;
                        }
                        audience.sendActionBar(Component.text(CCUtil.translate(outputText)));
                    }, 0, delayTime)
            );
        }

        if (contentTask.getPrintStatus().equals(PrintStatus.KEEPING)) {

            contentTask.setPrintStatus(PrintStatus.KEPT);
            contentTask.getPrintTask().cancel();

            long keep = (Long.parseLong(contentList.get(3))/20 * 1000) + System.currentTimeMillis();
            contentTask.setPrintTask(Bukkit.getScheduler().runTaskTimerAsynchronously(NarratorSpigot.getPluginInstance(), () -> {
                if (System.currentTimeMillis() >= keep) {
                    contentTask.setPrintStatus(PrintStatus.PRINTED);
                    return;
                }
                audience.sendActionBar(Component.text(CCUtil.translate(text)));
            }, 0, 20));
        }

        if (contentTask.getPrintStatus().equals(PrintStatus.PRINTED)) {
            contentTask.getPrintTask().cancel();
            return true;
        }

        return false;
    }

    /**
     * 让玩家通过 actionbar 进行回答选择
     * @param contentList - 总content
     * @param narrator - narrator 实例
     * @param player - 玩家
     * @param playerData - 玩家数据
     * @param contentTask - contentTask
     * @return 是否做出选择
     */
    private boolean answerByActionbar(List<String> contentList, Narrator narrator, Player player, PlayerData playerData, ContentTask contentTask){
        Audience audience = narrator.getAdventure().player(player);
        List<String> messages = new ArrayList<>();

        for (int i = 1; i < contentList.size(); i++) {
            messages.add(contentList.get(i));
        }

        if (contentTask.getOptionStatus().equals(OptionStatus.NONE)) {

            contentTask.setOptionSize(messages.size());
            contentTask.setOptionIndex(0);
            contentTask.setOptionStatus(OptionStatus.DECIDING);

            contentTask.setOptionTask(Bukkit.getScheduler().runTaskTimerAsynchronously(NarratorSpigot.getPluginInstance(), () -> {

                if (contentTask.getOptionStatus().equals(OptionStatus.DECIDED)) {
                    playerData.setMessageOption(messages.get(contentTask.getOptionIndex()));
                    return;
                }

                StringBuilder message = new StringBuilder();
                for (String m : messages) {
                    if (contentTask.getOptionIndex() == messages.indexOf(m)) {
                        message.append("&a&l").append(m);
                    } else {
                        message.append(m);
                    }
                    if (messages.indexOf(m) < messages.size() - 1) {
                        message.append(" &7| &r");
                    }
                }
                audience.sendActionBar(Component.text(CCUtil.translate(message.toString())));

            }, 0, 5));

        }

        if (contentTask.getOptionStatus().equals(OptionStatus.DECIDED)) {
            contentTask.getOptionTask().cancel();
            return true;
        }
        return false;
    }

    /**
     * 整体延迟
     * @param contentList - 总content
     * @param contentTask - contentTask
     * @return 是否延迟结束
     */
    private boolean delayOverall(List<String> contentList, ContentTask contentTask){

        long delay = Integer.parseInt(contentList.get(1));

        if (contentTask.getDelayStatus().equals(DelayStatus.NONE)) {
            contentTask.setDelayStatus(DelayStatus.DELAYING);

            Bukkit.getScheduler().runTaskLaterAsynchronously(NarratorSpigot.getPluginInstance(), () -> {
                contentTask.setDelayStatus(DelayStatus.DELAYED);
            }, delay);
        }

        return contentTask.getDelayStatus().equals(DelayStatus.DELAYED);
    }

    /**
     * 单条延迟
     * fixme 存在延迟计算的问题，需要优化
     *
     * @param contentList - 总content
     * @param player - 玩家
     * @param chapterData - 章节数据
     * @param contentTask - contentTask
     */
    private void delaySingle(List<String> contentList, Player player, ChapterData chapterData, ContentTask contentTask){
        long delay = Integer.parseInt(contentList.get(1));

        StringBuilder sb = new StringBuilder();
        for (int i = 2; i <= contentList.size() - 1; i++){
            sb.append(contentList.get(i));
            if (i != contentList.size() - 1) {
                sb.append("|");
            }
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(NarratorSpigot.getPluginInstance(), () -> {
            execute(player, chapterData, sb.toString(), contentTask);
        }, delay);
    }

    /**
     * 进行任务转跳
     * @param contentList - 总content
     * @param playerData - 玩家数据
     * @param chapterData - 章节数据
     * @param content - 当前content
     * @return 是否转跳完毕
     */
    private boolean jumpTask(List<String> contentList, PlayerData playerData, ChapterData chapterData, String content) {
        int taskOrdinal = Integer.parseInt(contentList.get(1));
        int contentIndex = Integer.parseInt(contentList.get(2));

        if (taskOrdinal < 1 || contentIndex < 0) {
            LogUtil.log(Level.SEVERE, Lang.CHAPTERS_EXECUTE_NUMBER_FORMAT);
            LogUtil.log(Level.SEVERE, Lang.CHAPTERS_CONSOLE_HELP);
            narrator.getLogger().log(Level.SEVERE, "        Chapter Name: " + chapterData.getName());
            narrator.getLogger().log(Level.SEVERE, "        Content: " + content);
            return false;
        }
        narrator.getHandlerManager().getTaskHandler().jump(
                playerData,
                chapterData,
                taskOrdinal,
                contentIndex
        );
        return true;
    }

}

