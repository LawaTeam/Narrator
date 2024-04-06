package me.sakuratao.narrator.spigot.handlers;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.configuration.Lang;
import me.sakuratao.narrator.spigot.data.player.PlayerData;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.enums.Weather;
import me.sakuratao.narrator.spigot.events.content.*;
import me.sakuratao.narrator.spigot.events.content.actionbar.ActionBarAnswerEvent;
import me.sakuratao.narrator.spigot.events.content.actionbar.ActionBarEvent;
import me.sakuratao.narrator.spigot.events.content.delay.DelayEvent;
import me.sakuratao.narrator.spigot.events.content.delay.DelaySingleEvent;
import me.sakuratao.narrator.spigot.events.content.jump.JumpTaskEvent;
import me.sakuratao.narrator.spigot.task.ContentTask;
import me.sakuratao.narrator.spigot.utils.CCUtil;
import me.sakuratao.narrator.spigot.utils.EventUtil;
import me.sakuratao.narrator.spigot.utils.LogUtil;
import me.sakuratao.narrator.spigot.utils.ToastUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.util.Arrays;
import java.util.List;
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
                    sendTitle(contentList, player);
                    return true;
                }
                /*
                    MESSAGE
                 */
                case "M":
                case "MESSAGE": {
                    sendMessage(contentList, player);
                    return true;
                }
                /*
                    ACTIONBAR
                 */
                case "AB":
                case "ACTIONBAR": {
                    return sendActionBar(contentList, player);
                }
                /*
                    ACTIONBAR_ANSWER
                 */
                case "AB_ANSWER":
                case "ACTIONBAR_ANSWER": {
                    return sendActionBarAnswer(contentList, player, playerData);
                }
                /*
                    DELAY
                 */
                case "D":
                case "DELAY": {
                    return executeDelay(contentList, player, chapterData, contentTask);
                }
                case "COMMAND": {
                    executeCommand(player, contentList, chapterData);
                    return true;
                }
                case "MC":
                case "MESSAGE_CLICK": {
                    // todo
                    return true;
                }
                case "JT":
                case "JUMP_TASK": {
                    return jumpTask(player, playerData, chapterData, Integer.parseInt(contentList.get(1)),
                            Integer.parseInt(contentList.get(2)), contentList.get(3));
                }
                case "TOAST":{
                    sendToast(player, contentList.get(2), contentList.get(3), contentList.get(1));
                    return true;
                }
                case "WEATHER": {
                    changeWeather(player, Weather.valueOf(contentList.get(1)));
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
            return false;
        }
    }

    /**
     *
     * 切换 Weather
     * @param player - 玩家
     * @param weather - 天气
     */
    private void changeWeather(Player player, Weather weather){
        WeatherChangeEvent weatherChangeEvent = new WeatherChangeEvent(player, weather);
        EventUtil.callEvent(weatherChangeEvent);
        if (!weatherChangeEvent.isCancelled()) {
            weatherChangeEvent.changeWeather();
        }
    }

    /**
     * 发送 toast
     * @param player - 玩家
     * @param material - 材质
     * @param title - 显示内容
     * @param frame - 显示形式
     */
    private void sendToast(Player player, String material, String title, String frame){
        ToastEvent toastEvent = new ToastEvent(player, Material.valueOf(material), ToastUtil.handleTitle(title), frame);
        EventUtil.callEvent(toastEvent);
        toastEvent.showToast();
    }

    /**
     * 执行命令
     * @param player - 玩家
     * @param contentList - 内容列表
     * @param chapterData - 章节数据
     */
    private void executeCommand(Player player, List<String> contentList, ChapterData chapterData){
        CommandExecuteEvent commandExecuteEvent = new CommandExecuteEvent(narrator, player, chapterData, contentList.get(1));
        EventUtil.callEvent(commandExecuteEvent);
        if (!commandExecuteEvent.isCancelled()){
            commandExecuteEvent.executeCommand();
        }
    }

    /**
     * 执行 delay
     * @param contentList - 内容列表
     * @param player - 玩家
     * @param chapterData - 章节数据
     * @param contentTask - contentTask
     * @return true - 执行完毕
     */
    private boolean executeDelay(List<String> contentList, Player player, ChapterData chapterData, ContentTask contentTask){
        if (contentList.size() > 2){
            DelaySingleEvent delaySingleEvent = new DelaySingleEvent(narrator, player, chapterData, contentTask, contentList);
            EventUtil.callEvent(delaySingleEvent);
            if (!delaySingleEvent.isCancelled()) {
                delaySingleEvent.delay();
            }
            return true;
        }

        if (narrator.getCacheData().isCurrentDelayEventExist(player)) {
            return narrator.getCacheData().isCurrentDelayEventDelayed(player);
        }
        DelayEvent delayEvent = new DelayEvent(Long.parseLong(contentList.get(1)));
        EventUtil.callEvent(delayEvent);
        if (!delayEvent.isCancelled()) {
            delayEvent.delay();
            narrator.getCacheData().putCurrentDelayEvent(player, delayEvent);
        }
        return delayEvent.isDelayed();
    }

    /**
     * 给玩家发送 actionbar Answer
     * @param contentList - 内容列表
     * @param player - 玩家
     * @param playerData - 玩家数据
     * @return true - 已作出决定， 否则反之
     */
    private boolean sendActionBarAnswer(List<String> contentList, Player player, PlayerData playerData){
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

    /**
     * 给玩家发 actionbar
     * @param contentList - 内容列表
     * @param player - 玩家
     * @return true 表示成功完毕 false 表示发送失败/未完毕
     */
    private boolean sendActionBar(List<String> contentList, Player player){
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

    /**
     * 给玩家发 title
     * @param contentList - 内容列表
     * @param player - 玩家
     */
    private void sendTitle(List<String> contentList, Player player){
        TitleEvent titleEvent = new TitleEvent(contentList, player);
        EventUtil.callEvent(titleEvent);
        if (!titleEvent.isCancelled()) {
            titleEvent.showTitle();
        }
    }

    /**
     * 给玩家发 message
     * @param contentList - 内容列表
     * @param player - 玩家
     */
    private void sendMessage(List<String> contentList, Player player){
        MessageEvent messageEvent = new MessageEvent(player, CCUtil.translate(contentList.get(1)));
        EventUtil.callEvent(messageEvent);
        if (!messageEvent.isCancelled()) {
            messageEvent.sendMessage();
        }
    }

    /**
     * 转跳章节
     * @param player - 玩家
     * @param playerData - 玩家数据
     * @param chapterData - 章节数据
     * @param taskOrdinal - 任务序数
     * @param contentIndex - 内容索引数
     * @param jumpContent - 关于转跳的content
     * @return 转跳完成
     */
    private boolean jumpTask(Player player, PlayerData playerData, ChapterData chapterData, int taskOrdinal, int contentIndex, String jumpContent) {
        JumpTaskEvent jumpTaskEvent = new JumpTaskEvent(narrator, player, playerData, chapterData, taskOrdinal, contentIndex, jumpContent);
        EventUtil.callEvent(jumpTaskEvent);
        return jumpTaskEvent.jumpTask();
    }

}

