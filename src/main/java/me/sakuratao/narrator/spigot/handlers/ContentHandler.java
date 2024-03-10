package me.sakuratao.narrator.spigot.handlers;

import eu.endercentral.crazy_advancements.JSONMessage;
import eu.endercentral.crazy_advancements.advancement.Advancement;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.NarratorSpigot;
import me.sakuratao.narrator.spigot.data.Player.PlayerData;
import me.sakuratao.narrator.spigot.data.cache.CacheData;
import me.sakuratao.narrator.spigot.enums.DelayStatus;
import me.sakuratao.narrator.spigot.enums.OptionStatus;
import me.sakuratao.narrator.spigot.enums.PrintStatus;
import me.sakuratao.narrator.spigot.task.ContentTask;
import me.sakuratao.narrator.spigot.utils.CCUtil;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PieComponent
public class ContentHandler {

    @Wire
    private Narrator narrator;
    @Wire
    private CacheData cacheData;

    public boolean execute(Player player, ChapterData chapterData, String content, ContentTask contentTask) {

        List<String> type = Arrays.asList(content.split("\\|"));

        PlayerData playerData = narrator.getManagerHandler().getPlayerManager().getByPlayer(player);



        /*
            TODO: 物品栏文字调用，生成剧情对话背包，

            TODO: MESSAGE_CLICK、MESSAGE_DROP、INV_ANSWER、CONDITION、SOUND(播放声音)

            TODO: 玩家自定义字幕速度以及停留时间，并提供 " 上一条 " 的功能

         */


        /*
         * 这里会解析 content 的内容
         * type.get(0) 即是所对应的功能
         * 其余的则是所对应功能的参数，详细请翻阅 ChapterExample.yml
         */
        try {
            switch (type.get(0)) {
                case "T":
                case "TITLE": {
                    return title(type, player);
                }
                case "M":
                case "MESSAGE": {
                    player.sendMessage(CCUtil.translate(type.get(1)));
                    return true;
                }
                case "AB":
                case "ACTIONBAR": {
                    return actionbar(type, player, contentTask);
                }
                case "AB_ANSWER":
                case "ACTIONBAR_ANSWER": {
                    return actionBarAnswer(type, narrator, player, playerData, contentTask);
                }
                case "D":
                case "DELAY": {
                    return delay(type, contentTask);
                }
                case "COMMAND": {
                    narrator.getLogger().log(Level.WARNING, "Executed command: " + type.get(1) + " | Chapter: " + chapterData.getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), type.get(1));
                    return true;
                }
                case "MC":
                case "MESSAGE_CLICK": {
                    // todo
                    return true;
                }
                case "JT":
                case "JUMP_TASK": {
                    return jumpTask(type, playerData, chapterData, content);
                }
                case "TOAST":{
                    toast(type, player);
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

    private void toast(List<String> type, Player player){

        String[] t = type.get(3).split("<br>");
        StringBuilder sb = new StringBuilder();

        /*
            这段屎山，没事别碰
            因为实在是太乱了
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

        TextComponent title = new TextComponent(CCUtil.translate(sb.toString()));

        AdvancementDisplay rootDisplay =  new AdvancementDisplay(
                Material.valueOf(type.get(2)),
                new JSONMessage(title),
                new JSONMessage(new TextComponent("")),
                AdvancementDisplay.AdvancementFrame.parse(type.get(1)),
                AdvancementVisibility.ALWAYS
        );
        Advancement rootAdvancement = new Advancement(null, rootDisplay);

        rootAdvancement.displayToast(player);

    }

    private boolean title(List<String> type, Player player) {
        player.sendTitle(
                CCUtil.translate(type.get(4)), CCUtil.translate(type.get(5)),
                Integer.parseInt(type.get(1)), Integer.parseInt(type.get(2)), Integer.parseInt(type.get(3))
        );
        return true;
    }

    private boolean actionbar(List<String> type, Player player, ContentTask contentTask) {
        Audience audience = narrator.getAdventure().player(player);
        if (!Boolean.parseBoolean(type.get(1))) {
            audience.sendActionBar(Component.text(CCUtil.translate(type.get(2))));
            return true;
        }

                    /*
                      这里会对 text 处理成一个打字机的效果
                      text将会在 delay 限定的时间内完成逐字打印
                     */
        String text = type.get(4);

        if (contentTask.getPrintStatus().equals(PrintStatus.NONE)) {
            contentTask.setPrintStatus(PrintStatus.PRINTING);

            long delayTime = Long.parseLong(type.get(2));
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

            long keep = (Long.parseLong(type.get(3))/20 * 1000) + System.currentTimeMillis();
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

    private boolean actionBarAnswer(List<String> type, Narrator narrator, Player player, PlayerData playerData,  ContentTask contentTask){
        Audience audience = narrator.getAdventure().player(player);
        List<String> messages = new ArrayList<>();

        for (int i = 1; i < type.size(); i++) {
            messages.add(type.get(i));
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

    private boolean delay(List<String> type, ContentTask contentTask){
        if (contentTask.getDelayStatus().equals(DelayStatus.NONE)) {
            contentTask.setDelayStatus(DelayStatus.DELAYING);

            long delay = Integer.parseInt(type.get(1));
            Bukkit.getScheduler().runTaskLater(NarratorSpigot.getPluginInstance(), () -> {
                contentTask.setDelayStatus(DelayStatus.DELAYED);
            }, delay);
        }

        return contentTask.getDelayStatus().equals(DelayStatus.DELAYED);
    }

    private boolean jumpTask(List<String> type, PlayerData playerData, ChapterData chapterData, String content) {
        int taskOrdinal = Integer.parseInt(type.get(1));
        int contentIndex = Integer.parseInt(type.get(2));

        if (taskOrdinal < 1 || contentIndex < 0) {
            narrator.getLogger().log(Level.SEVERE, "Task Ordinal must be over 1, Content Index must be over 0!");
            narrator.getLogger().log(Level.SEVERE, "Please check your content about 'JT/JUMP_TASK'");
            narrator.getLogger().log(Level.SEVERE, "Here are some information may help you:");
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

