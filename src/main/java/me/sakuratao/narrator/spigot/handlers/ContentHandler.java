package me.sakuratao.narrator.spigot.handlers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.NarratorSpigot;
import me.sakuratao.narrator.spigot.configuration.Lang;
import me.sakuratao.narrator.spigot.data.Player.PlayerData;
import me.sakuratao.narrator.spigot.enums.DelayStatus;
import me.sakuratao.narrator.spigot.enums.Weather;
import me.sakuratao.narrator.spigot.events.content.WeatherChangeEvent;
import me.sakuratao.narrator.spigot.events.content.actionbar.ActionBarAnswerEvent;
import me.sakuratao.narrator.spigot.events.content.actionbar.ActionBarEvent;
import me.sakuratao.narrator.spigot.events.content.MessageEvent;
import me.sakuratao.narrator.spigot.events.content.TitleEvent;
import me.sakuratao.narrator.spigot.task.ContentTask;
import me.sakuratao.narrator.spigot.utils.*;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.util.*;
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
                /*
                  Title
                 */
                case "T":
                case "TITLE": {
                    TitleEvent titleEvent = new TitleEvent(contentList, player);
                    EventUtil.callEvent(titleEvent);
                    if (!titleEvent.isCancelled()) {
                        titleEvent.showTitle();
                    }
                    return true;
                }
                /*
                    MESSAGE
                 */
                case "M":
                case "MESSAGE": {
                    MessageEvent messageEvent = new MessageEvent(player, CCUtil.translate(contentList.get(1)));
                    EventUtil.callEvent(messageEvent);
                    if (!messageEvent.isCancelled()) {
                        messageEvent.sendMessage();
                    }
                    return true;
                }
                /*
                    ACTIONABR
                 */
                case "AB":
                case "ACTIONBAR": {
                    boolean isPrint = Boolean.parseBoolean(contentList.get(1));

                    ActionBarEvent actionBarEvent;
                    if (isPrint) {
                        if (narrator.getCacheData().isCurrentActionBarEventExist(player)) {
                            narrator.getCacheData().getCurrentActionBarEvent(player).showActionbar();
                            return narrator.getCacheData().isCurrentActionbarEnded(player);
                        }
                        actionBarEvent = new ActionBarEvent(
                                narrator,
                                player,
                                true,
                                Long.parseLong(contentList.get(2)),
                                Long.parseLong(contentList.get(3)),
                                CCUtil.translate(contentList.get(4))
                        );
                        narrator.getCacheData().putCurrentActionBarEvent(player, actionBarEvent);
                    } else {
                        actionBarEvent = new ActionBarEvent(
                                narrator,
                                player,
                                false,
                                0,
                                0,
                                CCUtil.translate(contentList.get(2))
                        );
                    }

                    EventUtil.callEvent(actionBarEvent);
                    actionBarEvent.showActionbar();
                    return actionBarEvent.isEnded();
                }
                /*
                    ACTIONBAR_ANSWER
                 */
                case "AB_ANSWER":
                case "ACTIONBAR_ANSWER": {

                    if (narrator.getCacheData().isCurrentActionBarAnswerEventExist(player)) {
                        narrator.getCacheData().getCurrentActionBarAnswerEvent(player).checkAnswer();
                        return narrator.getCacheData().isCurrentActionBarAnswerEventDecided(player);
                    }
                    ActionBarAnswerEvent actionBarAnswerEvent = new ActionBarAnswerEvent(
                            narrator,
                            player,
                            playerData,
                            contentList.subList(1, contentList.size())
                    );
                    narrator.getCacheData().putCurrentActionBarAnswerEvent(player, actionBarAnswerEvent);
                    return false;
                }
                /*
                    DELAY
                 */
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
                    WeatherChangeEvent weatherChangeEvent = new WeatherChangeEvent(player, Weather.valueOf(contentList.get(1)));
                    EventUtil.callEvent(weatherChangeEvent);
                    if (!weatherChangeEvent.isCancelled()) {
                        weatherChangeEvent.changeWeather();
                    }
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
     * fixme 重开后仍然存在此次delay
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

