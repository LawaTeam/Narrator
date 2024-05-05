package me.sakuratao.narrator.spigot.handlers;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.data.player.PlayerData;
import me.sakuratao.narrator.spigot.enums.Weather;
import me.sakuratao.narrator.spigot.events.content.*;
import me.sakuratao.narrator.spigot.events.content.actionbar.ActionBarAnswerEvent;
import me.sakuratao.narrator.spigot.events.content.actionbar.ActionBarEvent;
import me.sakuratao.narrator.spigot.events.content.delay.DelayEvent;
import me.sakuratao.narrator.spigot.events.content.delay.DelaySingleEvent;
import me.sakuratao.narrator.spigot.events.content.jump.JumpTaskEvent;
import me.sakuratao.narrator.spigot.events.content.player.MessageEvent;
import me.sakuratao.narrator.spigot.events.content.player.TitleEvent;
import me.sakuratao.narrator.spigot.events.content.player.ToastEvent;
import me.sakuratao.narrator.spigot.events.content.world.*;
import me.sakuratao.narrator.spigot.events.content.world.effect.PotionEffectGiveEvent;
import me.sakuratao.narrator.spigot.events.content.world.effect.PotionEffectRemoveEvent;
import me.sakuratao.narrator.spigot.events.content.world.sound.PlaySoundEvent;
import me.sakuratao.narrator.spigot.events.content.world.sound.StopSoundEvent;
import me.sakuratao.narrator.spigot.manager.ManagerHandler;
import me.sakuratao.narrator.spigot.task.ContentTask;
import me.sakuratao.narrator.spigot.utils.LogUtil;
import me.sakuratao.narrator.spigot.utils.server.*;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

@PieComponent
public class ContentHandler {

    @Wire private Narrator narrator;
    @Wire private ManagerHandler managerHandler;
    @Wire private ConditionHandler conditionHandler;
    /**
     * 执行对 content 的解析
     * @param player - 玩家
     * @param chapterData - 章节数据
     * @param content - 内容
     * @param contentTask - contentTask
     * @return 执行完毕
     */
    public boolean execute(Player player, ChapterData chapterData, String content, ContentTask contentTask) {

        List<String> contentList = Arrays.stream(content.split("\\|"))
                .map(m -> PapiUtil.getString(player, m))
                .collect(Collectors.toList());

        PlayerData playerData = managerHandler.getPlayerManager().getByPlayer(player);



        /*
            TODO: 物品栏文字调用，生成剧情对话背包，

            TODO: MESSAGE_CLICK、MESSAGE_DROP、INV_ANSWER、CONDITION、BOOM_AROUND、SPAWN_ENTITY、NPC

            TODO: 玩家自定义字幕速度以及停留时间，并提供 " 上一条 " 的功能

         */


        /*
         * 这里会解析 content 的内容
         * contentList.get(0) 即是所对应的功能
         * 其余的则是所对应功能的参数，详细请翻阅 ChapterExample.yml
         */
        try {
            return switch (contentList.get(0)) {
                /*
                    更改时间
                 */
                case "TIME" -> {
                    TaskUtil.task(() -> {
                        if (contentList.size() == 2) {
                            changeTime(player, Long.parseLong(contentList.get(1)), false, 0);
                        } else {
                            changeTime(
                                    player,
                                    Long.parseLong(contentList.get(1)),
                                    Boolean.parseBoolean(contentList.get(2)),
                                    Long.parseLong(contentList.get(3))
                            );
                        }
                    });
                    yield true;
                }
                /*
                    传送作用
                 */
                case "TP" -> {
                    handleTeleport(contentList.get(1), player);
                    yield true;
                }
                /*
                  send title to player
                 */
                case "T", "TITLE" -> {
                    handleTitleAction(contentList, player);
                    yield true;
                }
                /*
                    send message to player
                 */
                case "M", "MESSAGE" -> {
                    handleMessageAction(contentList, player);
                    yield true;
                }
                /*
                    send actionbar to player
                 */
                case "AB", "ACTIONBAR" -> handleActionBarAction(contentList, player);
                /*
                    let player use actionbar to answer
                 */
                case "AB_ANSWER", "ACTIONBAR_ANSWER" -> handleActionBarAnswerAction(contentList, player, playerData);
                /*
                    execute delay
                 */
                case "D", "DELAY" -> handleDelay(contentList, player, chapterData, contentTask);
                /*
                    execute command
                 */
                case "COMMAND" -> {
                    executeCommand(player, contentList, chapterData);
                    yield true;
                }
                /*

                 */
                case "MC", "MESSAGE_CLICK" ->
                    // todo
                        true;
                /*
                    send toast to player
                 */
                case "TOAST" -> {
                    handleToastAction(player, contentList.get(2), contentList.get(3), contentList.get(1));
                    yield true;
                }
                /*
                    change weather for player privately
                 */
                case "WEATHER" -> {
                    handleChangeWeather(player, Weather.valueOf(contentList.get(1)));
                    yield true;
                }
                /*
                    Play Sound
                 */
                case "PLAYSOUND" -> {
                    playSound(contentList, player);
                    yield true;
                }
                /*
                    Stop Sound
                 */
                case "STOPSOUND" -> {
                    stopSound(contentList, player);
                    yield true;
                }
                /*
                    Effect
                 */
                case "EFFECT" -> {
                    handleEffectAction(contentList, player);
                    yield true;
                }
                /*
                    Summon
                 */
                case "SUMMON" -> {
                    // todo
                    yield true;
                }
                case "C", "CONDITION" -> {
                    if (content.substring(0, 3).toUpperCase().startsWith("C|")){
                        conditionHandler.handle(player, PapiUtil.getString(player, content
                                .replace("C|", "")));
                    }
                    if (content.substring(0, 3).toUpperCase().startsWith("CONDITION|")){
                        conditionHandler.handle(player, PapiUtil.getString(player, content.
                                replace("CONDITION|", "")));
                    }
                    yield true;
                }
                /*
                    Jump Task
                 */
                case "JT", "JUMP_TASK" ->
                        handleJumpTask(player, playerData, chapterData, Integer.parseInt(contentList.get(1)),
                                Integer.parseInt(contentList.get(2)), content);
                case "JC", "JUMP_CHAPTER" ->
                    // todo
                        true;
                default -> true;
            };
        } catch (IndexOutOfBoundsException e) {
            LogUtil.log(Level.SEVERE, "Throw IndexOutOfBoundsException!");
            LogUtil.log(Level.SEVERE, "Please check your contents!");
            LogUtil.log(Level.SEVERE, "Here are some information may help you:");
            LogUtil.log(Level.SEVERE, "        Chapter Name: " + chapterData.getName());
            LogUtil.log(Level.SEVERE, "        Content: " + content);
            LogUtil.log(Level.SEVERE, "Details(For Developments): " + e.getMessage());
            return false;
        }
    }

    /**
     * 处理给定玩家的药水效果。根据输入列表的内容，要么清除玩家的某个药水效果，要么给予玩家一个新的药水效果。
     *
     * @param contentList 包含指令内容的列表。如果是清除操作，第一个元素是"clear"，接下来是效果名称；
     *                    如果是给予操作，第一个元素是效果名称，接下来是持续时间、放大器等级、是否隐藏粒子效果。
     * @param player 要应用药水效果的玩家。
     */
    private void handleEffectAction(List<String> contentList, Player player) {

        // 检查是否要清除玩家的药水效果
        if (contentList.get(0).equalsIgnoreCase("clear")) {
            String effect = contentList.get(1);
            PotionEffectRemoveEvent potionEffectRemoveEvent = new PotionEffectRemoveEvent(narrator, player, effect);
            EventUtil.callEvent(potionEffectRemoveEvent); // 触发药水效果移除事件
            if (!potionEffectRemoveEvent.isCancelled()) { // 如果事件未被取消，则移除药水效果
                potionEffectRemoveEvent.removePotionEffect();
            }
            return; // 结束方法
        }

        PotionEffectGiveEvent potionEffectGiveEvent = getPotionEffectGiveEvent(contentList, player);
        EventUtil.callEvent(potionEffectGiveEvent); // 触发药水效果给予事件
        if (!potionEffectGiveEvent.isCancelled()) { // 如果事件未被取消，则给予玩家药水效果
            potionEffectGiveEvent.givePotionEffect();
        }
        return;
    }

    /**
     * 获取一个 PotionEffectGiveEvent 对象，用于触发药水效果给予事件。
     *
     * @param contentList 包含指令内容的列表。第一个元素是效果名称，接下来是持续时间、放大器等级、是否隐藏粒子效果。
     * @param player 要应用药水效果的玩家。
     * @return 一个 PotionEffectGiveEvent 对象。
     */
    private @NotNull PotionEffectGiveEvent getPotionEffectGiveEvent(List<String> contentList, Player player) {
        String effect = contentList.get(0);
        int duration = Integer.parseInt(contentList.get(1));
        int amplifier = Integer.parseInt(contentList.get(2));
        boolean hideParticles = Boolean.parseBoolean(contentList.get(3));
        boolean icon = Boolean.parseBoolean(contentList.get(4));
        return new PotionEffectGiveEvent(
                narrator,
                player,
                effect,
                duration,
                amplifier,
                hideParticles,
                icon
        );
    }


    /**
     * 停止指定玩家播放的声音。
     * @param contentList 包含声音相关信息的列表，至少包含声音 ID 和 声音分类 的字符串。
     * @param player 需要停止声音的玩家。
     */
    private void stopSound(List<String> contentList, Player player){
        StopSoundEvent stopSoundEvent = new StopSoundEvent(narrator, player, contentList.get(1), SoundCategory.valueOf(contentList.get(2).toUpperCase()));
        EventUtil.callEvent(stopSoundEvent);
        stopSoundEvent.stopSound();
    }

    /**
     * 播放声音
     * @param contentList - contentList
     * @param player - 玩家
     */
    private void playSound(List<String> contentList, Player player) {
        PlaySoundEvent playSoundEvent = new PlaySoundEvent(
                narrator,
                player,
                contentList.get(1), // target
                contentList.get(2), // sound
                SoundCategory.valueOf(contentList.get(3).toUpperCase()), // category
                Float.parseFloat(contentList.get(4)), // volume
                Float.parseFloat(contentList.get(5))  // pitch
        );
        EventUtil.callEvent(playSoundEvent);
        playSoundEvent.playSound();
    }

    /**
     * 为玩家更改时间
     * @param player - 玩家 id
     * @param toTimeTicks - 改到的时间
     * @param fade - 是否淡入
     * @param increase - 增长率
     */
    private void changeTime(Player player, long toTimeTicks, boolean fade, long increase){

        TimeChangeEvent timeChangeEvent = new TimeChangeEvent(narrator, player, toTimeTicks, fade, increase);
        EventUtil.callEvent(timeChangeEvent);
        if (!timeChangeEvent.isCancelled()) {
            timeChangeEvent.changeTime();
        }

    }

    /**
     * 执行 tp
     * @param target 目标(支持 function)
     * @param player 被 tp 玩家
     */
    private void handleTeleport(String target, Player player) {

        TaskUtil.task(() -> {
            TeleportEvent teleportEvent = new TeleportEvent(narrator, target, player);
            EventUtil.callEvent(teleportEvent);
            if (!teleportEvent.isCancelled()){
                teleportEvent.teleport();
            }
        });

    }

    /**
     *
     * 切换 Weather
     * @param player - 玩家
     * @param weather - 天气
     */
    private void handleChangeWeather(Player player, Weather weather){
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
    private void handleToastAction(Player player, String material, String title, String frame){
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
    private boolean handleDelay(List<String> contentList, Player player, ChapterData chapterData, ContentTask contentTask){
        if (contentList.size() > 2){
            DelaySingleEvent delaySingleEvent = new DelaySingleEvent(narrator, player, chapterData, contentTask, contentList);
            EventUtil.callEvent(delaySingleEvent);
            if (!delaySingleEvent.isCancelled()) {
                delaySingleEvent.delay();
                narrator.getCacheData().putDelaySingleEvent(delaySingleEvent);
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
    private boolean handleActionBarAnswerAction(List<String> contentList, Player player, PlayerData playerData){
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
    private boolean handleActionBarAction(List<String> contentList, Player player){
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
            EventUtil.callEvent(actionBarEvent);
            actionBarEvent.showActionbar();
            return true;
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
    private void handleTitleAction(List<String> contentList, Player player){
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
    private void handleMessageAction(List<String> contentList, Player player){
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
    private boolean handleJumpTask(Player player, PlayerData playerData, ChapterData chapterData, int taskOrdinal, int contentIndex, String jumpContent) {
        JumpTaskEvent jumpTaskEvent = new JumpTaskEvent(narrator, player, playerData, chapterData, taskOrdinal, contentIndex, jumpContent);
        EventUtil.callEvent(jumpTaskEvent);
        return jumpTaskEvent.jumpTask();
    }

}

