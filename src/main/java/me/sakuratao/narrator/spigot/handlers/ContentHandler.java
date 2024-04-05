package me.sakuratao.narrator.spigot.handlers;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.configuration.Lang;
import me.sakuratao.narrator.spigot.data.player.PlayerData;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.enums.Weather;
import me.sakuratao.narrator.spigot.events.content.*;
import me.sakuratao.narrator.spigot.events.content.actionbar.ActionBarAnswerEvent;
import me.sakuratao.narrator.spigot.events.content.actionbar.ActionBarEvent;
import me.sakuratao.narrator.spigot.task.ContentTask;
import me.sakuratao.narrator.spigot.utils.CCUtil;
import me.sakuratao.narrator.spigot.utils.EventUtil;
import me.sakuratao.narrator.spigot.utils.LogUtil;
import me.sakuratao.narrator.spigot.utils.ToastUtil;
import org.bukkit.Bukkit;
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
                    ACTIONBAR
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
                    ToastEvent toastEvent = new ToastEvent(player, Material.valueOf(contentList.get(2)), ToastUtil.handleTitle(contentList.get(3)), contentList.get(1));
                    EventUtil.callEvent(toastEvent);
                    toastEvent.showToast();
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
            return false;
        }
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

