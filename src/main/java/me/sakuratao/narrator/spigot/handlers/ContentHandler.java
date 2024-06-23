package me.sakuratao.narrator.spigot.handlers;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.data.player.PlayerData;
import me.sakuratao.narrator.spigot.enums.Weather;
import me.sakuratao.narrator.spigot.events.content.CommandExecuteEvent;
import me.sakuratao.narrator.spigot.events.content.actionbar.ActionBarAnswerEvent;
import me.sakuratao.narrator.spigot.events.content.actionbar.ActionBarEvent;
import me.sakuratao.narrator.spigot.events.content.delay.DelayEvent;
import me.sakuratao.narrator.spigot.events.content.delay.DelaySingleEvent;
import me.sakuratao.narrator.spigot.events.content.jump.JumpChapterEvent;
import me.sakuratao.narrator.spigot.events.content.jump.JumpTaskEvent;
import me.sakuratao.narrator.spigot.events.content.player.MessageEvent;
import me.sakuratao.narrator.spigot.events.content.player.TitleEvent;
import me.sakuratao.narrator.spigot.events.content.player.ToastEvent;
import me.sakuratao.narrator.spigot.events.content.world.TeleportEvent;
import me.sakuratao.narrator.spigot.events.content.world.TimeChangeEvent;
import me.sakuratao.narrator.spigot.events.content.world.WeatherChangeEvent;
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
    @Wire private DebugHandler debugHandler;

    /**
     * 执行对 content 的解析
     *
     * @param player 当前操作的玩家对象
     * @param chapterData 当前章节数据
     * @param content 需要解析的内容
     * @param contentTask 关联的内容任务
     * @return 执行结果，成功返回true，失败返回false
     */
    public boolean handleContent(Player player, ChapterData chapterData, String content, ContentTask contentTask) {

        // 获取玩家数据
        PlayerData playerData = managerHandler.getPlayerManager().getByPlayer(player);

        // 将内容按"|"分割，并处理 papi
        List<String> contentList = List.of(content.split("\\|"));
        contentList = contentList.stream()
                .map(m -> PapiUtil.getString(player, m))
                .collect(Collectors.toList());

        return executeContent(player, playerData, chapterData, contentList, content, contentTask);
    }

    public boolean executeContent(
            Player player,
            PlayerData playerData,
            ChapterData chapterData,
            List<String> contentList,
            String content,
            ContentTask contentTask
    ){
        // 根据解析出的第一个参数（功能标识）执行相应的操作
        String contentType = contentList.get(0);
        try {
            return switch (contentType) {
                // 更改时间功能处理
                case "TIME" -> {
                    TaskUtil.task(() -> {
                        // 判断是否开启 fade
                        long targetTime = Long.parseLong(contentList.get(1));
                        if (contentList.size() == 2) {
                            changeTime(player, targetTime, false, 0);
                        } else {
                            boolean isFade = Boolean.parseBoolean(contentList.get(2));
                            long increase = Long.parseLong(contentList.get(3));
                            changeTime(player, targetTime, isFade, increase);
                        }
                    });
                    yield true;
                }
                // 传送功能处理
                case "TP", "TELEPORT" -> {
                    String tpTarget = contentList.get(1);
                    handleTeleport(tpTarget, player);
                    yield true;
                }
                // 标题发送功能处理
                case "T", "TITLE" -> {
                    handleTitleAction(contentList, player);
                    yield true;
                }
                // 消息发送功能处理
                case "M", "MESSAGE" -> {
                    handleMessageAction(contentList, player);
                    yield true;
                }
                // 动作栏消息发送功能处理
                case "AB", "ACTIONBAR" -> handleActionBarAction(contentList, player);
                // 动作栏回答功能处理
                case "AB_ANSWER", "ACTIONBAR_ANSWER" -> handleActionBarAnswerAction(contentList, player, playerData);
                // 延迟执行功能处理
                case "D", "DELAY" -> handleDelay(contentList, player, chapterData, contentTask);
                // 执行命令功能处理
                case "COMMAND" -> {
                    executeCommand(player, contentList, chapterData);
                    yield true;
                }
                // todo 消息点击处理（待完成）
                case "MC", "MESSAGE_CLICK" -> true;
                // 弹出Toast消息功能处理
                case "TOAST" -> {
                    handleToastAction(player, contentList.get(2), contentList.get(3), contentList.get(1));
                    yield true;
                }
                // 天气变化功能处理
                case "WEATHER" -> {
                    handleChangeWeather(player, Weather.valueOf(contentList.get(1)));
                    yield true;
                }
                // 播放声音功能处理
                case "PLAYSOUND" -> {
                    playSound(contentList, player);
                    yield true;
                }
                // 停止声音功能处理
                case "STOPSOUND" -> {
                    stopSound(contentList, player);
                    yield true;
                }
                // 触发特效功能处理
                case "EFFECT" -> {
                    handleEffectAction(contentList, player);
                    yield true;
                }
                // todo 召唤实体功能处理（待完成）
                case "SUMMON" -> true;
                // 条件判断处理
                case "C", "CONDITION" -> {
                    String head = content.substring(0, content.indexOf("|"));
                    if (Boolean.parseBoolean(contentList.get(1))) {
                        yield conditionHandler.handle(player, true, PapiUtil.getString(player, content.replace(head + "|" + contentList.get(1) + "|", "")));
                    } else {
                        conditionHandler.handle(player, false, PapiUtil.getString(player, content.replace(head, "")));
                        yield true;
                    }
                }
                // 任务跳转处理
                case "JT", "JUMP_TASK" -> handleJumpTask(player, playerData, chapterData, contentList, content);
                // 章节跳转处理（待完成）
                case "JC", "JUMP_CHAPTER" -> handleJumpChapter(player, playerData, chapterData, contentList, content);
                // 默认情况，无对应功能时执行
                default -> true;
            };
        } catch (IndexOutOfBoundsException e) {
            // 处理解析过程中可能出现的数组越界异常
            LogUtil.log(Level.SEVERE, "Throw IndexOutOfBoundsException!");
            LogUtil.log(Level.SEVERE, "Please check your contents!");
            LogUtil.log(Level.SEVERE, "Here are some information may help you:");
            LogUtil.log(Level.SEVERE, "        Chapter Name: " + chapterData.getName());
            LogUtil.log(Level.SEVERE, "        Content: " + content);
            LogUtil.log(Level.SEVERE, "Details(For Developments): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean handleJumpChapter(
            Player player,
            PlayerData playerData,
            ChapterData currentData,
            List<String> contentList,
            String jumpContent
    ){
        int targetChapterOrdinal = 1;
        int targetTaskOrdinal = 1;
        int targetContentIndex = 0;

        if (contentList.size() >= 2) {
            targetChapterOrdinal = Integer.parseInt(contentList.get(1));
        }
        if (contentList.size() >= 3) {
            targetTaskOrdinal = Integer.parseInt(contentList.get(2));
        }
        if (contentList.size() >= 4) {
            targetContentIndex = Integer.parseInt(contentList.get(3));
        }

        JumpChapterEvent jumpChapterEvent = new JumpChapterEvent(narrator, player, playerData, currentData, targetChapterOrdinal, targetTaskOrdinal, targetContentIndex,  jumpContent);
        EventUtil.callEvent(jumpChapterEvent);
        debugHandler.debug(
                player,
                playerData.getPlayingChapterData(),
                playerData.getPlayingTaskData(),
                "JumpChapter",
                Arrays.asList(
                        "lang: &f" + currentData.getLang(),
                        "currentDataOrdinal: &f" + currentData.getOrdinal(),
                        "targetDataOrdinal: &f" + targetChapterOrdinal,
                        "targetTaskOrdinal: &f" + targetTaskOrdinal,
                        "targetContentIndex: &f" + targetContentIndex
                ),
                false
        );
        return jumpChapterEvent.jumpChapter();

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
            PlayerData playerData = managerHandler.getPlayerManager().getByPlayer(player);
            debugHandler.debug(
                    player,
                    playerData.getPlayingChapterData(),
                    playerData.getPlayingTaskData(),
                    "Effect",
                    "isClear: &atrue",
                    potionEffectRemoveEvent.isCancelled()
            );
            return; // 结束方法
        }

        String effect = contentList.get(0);
        int duration = Integer.parseInt(contentList.get(1));
        int amplifier = Integer.parseInt(contentList.get(2));
        boolean hideParticles = Boolean.parseBoolean(contentList.get(3));
        boolean icon = Boolean.parseBoolean(contentList.get(4));

        PotionEffectGiveEvent potionEffectGiveEvent = getPotionEffectGiveEvent(effect, duration, amplifier, hideParticles, icon, player);
        EventUtil.callEvent(potionEffectGiveEvent); // 触发药水效果给予事件
        if (!potionEffectGiveEvent.isCancelled()) { // 如果事件未被取消，则给予玩家药水效果
            potionEffectGiveEvent.givePotionEffect();
            PlayerData playerData = managerHandler.getPlayerManager().getByPlayer(player);
            debugHandler.debug(
                    player,
                    playerData.getPlayingChapterData(),
                    playerData.getPlayingTaskData(),
                    "Effect",
                    Arrays.asList(
                            "effect: &f" + effect,
                            "duration: &f" + duration,
                            "amplifier: &f" + amplifier,
                            "hideParticles: &f" + hideParticles,
                            "icon: &f" + icon
                    ),
                    potionEffectGiveEvent.isCancelled()
            );
        }
        return;
    }

    /**
     * 获取一个 PotionEffectGiveEvent 对象，用于触发药水效果给予事件。
     *
     * @param player 要应用药水效果的玩家。
     * @return 一个 PotionEffectGiveEvent 对象。
     */
    private @NotNull PotionEffectGiveEvent getPotionEffectGiveEvent(
            String effect,
            int duration,
            int amplifier,
            boolean hideParticles,
            boolean icon,
            Player player
    ) {
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

        String sound = contentList.get(1);
        SoundCategory soundCategory = SoundCategory.valueOf(contentList.get(2).toUpperCase());

        StopSoundEvent stopSoundEvent = new StopSoundEvent(narrator, player, sound, soundCategory);
        EventUtil.callEvent(stopSoundEvent);
        stopSoundEvent.stopSound();

        PlayerData playerData = managerHandler.getPlayerManager().getByPlayer(player);
        debugHandler.debug(
                player,
                playerData.getPlayingChapterData(),
                playerData.getPlayingTaskData(),
                "playSound",
                Arrays.asList(
                        "&7target: &f" + player.getName(),
                        "&7sound: &f" + sound,
                        "&7category: &f" + soundCategory
                ),
                false
        );
    }

    /**
     * 播放声音
     * @param contentList - contentList
     * @param player - 玩家
     */
    private void playSound(List<String> contentList, Player player) {
        String target = contentList.get(1);
        String sound = contentList.get(2);
        SoundCategory soundCategory = SoundCategory.valueOf(contentList.get(3).toUpperCase());
        float volume = Float.parseFloat(contentList.get(4));
        float pitch = Float.parseFloat(contentList.get(5));

        PlaySoundEvent playSoundEvent = new PlaySoundEvent(
                narrator,
                player,
                target,
                sound,
                soundCategory,
                volume,
                pitch
        );
        EventUtil.callEvent(playSoundEvent);
        playSoundEvent.playSound();

        PlayerData playerData = managerHandler.getPlayerManager().getByPlayer(player);
        debugHandler.debug(
                player,
                playerData.getPlayingChapterData(),
                playerData.getPlayingTaskData(),
                "playSound",
                Arrays.asList(
                        "&7target: &f" + target,
                        "&7sound: &f" + sound,
                        "&7category: &f" + soundCategory,
                        "&7volume: &f" + volume,
                        "&7pitch: &f" + pitch
                ),
                false
        );
    }

    /**
     * 为玩家更改时间
     * @param player - 玩家 id
     * @param targetTime - 改到的时间
     * @param fade - 是否淡入
     * @param increase - 增长率
     */
    private void changeTime(Player player, long targetTime, boolean fade, long increase){

        TimeChangeEvent timeChangeEvent = new TimeChangeEvent(narrator, player, targetTime, fade, increase);
        EventUtil.callEvent(timeChangeEvent);
        if (!timeChangeEvent.isCancelled()) {
            timeChangeEvent.changeTime();
        }
        narrator.getCacheData().putTimeChangeEvent(timeChangeEvent);
        PlayerData playerData = managerHandler.getPlayerManager().getByPlayer(player);
        debugHandler.debug(
                player,
                playerData.getPlayingChapterData(),
                playerData.getPlayingTaskData(),
                "TIME",
                "&7target: &f" + targetTime + " &7| " + "isFade: &f" + fade + " &7| " + "increase: &f" + increase,
                timeChangeEvent.isCancelled()
        );

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

            PlayerData playerData = managerHandler.getPlayerManager().getByPlayer(player);
            debugHandler.debug(
                    player,
                    playerData.getPlayingChapterData(),
                    playerData.getPlayingTaskData(),
                    "Teleport",
                    "&7target: &f" + target,
                    teleportEvent.isCancelled()
            );

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
        PlayerData playerData = managerHandler.getPlayerManager().getByPlayer(player);
        debugHandler.debug(
                player,
                playerData.getPlayingChapterData(),
                playerData.getPlayingTaskData(),
                "Weather",
                "&7weatherType: " + weather.getWeather(),
                false
        );
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
        PlayerData playerData = managerHandler.getPlayerManager().getByPlayer(player);
        debugHandler.debug(
                player,
                playerData.getPlayingChapterData(),
                playerData.getPlayingTaskData(),
                "Toast",
                Arrays.asList(
                        "&7title: " + title,
                        "&7frame: &f" + frame,
                        "&7material: &f" + material
                ),
                false
        );
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
        PlayerData playerData = managerHandler.getPlayerManager().getByPlayer(player);
        debugHandler.debug(
                player,
                playerData.getPlayingChapterData(),
                playerData.getPlayingTaskData(),
                "Command",
                "&7Command: &f" + contentList.get(1),
                false
        );
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

        PlayerData playerData = managerHandler.getPlayerManager().getByPlayer(player);

        if (contentList.size() > 2){
            DelaySingleEvent delaySingleEvent = new DelaySingleEvent(narrator, player, chapterData, contentTask, contentList);
            EventUtil.callEvent(delaySingleEvent);
            if (!delaySingleEvent.isCancelled()) {
                delaySingleEvent.delay();
                narrator.getCacheData().putDelaySingleEvent(delaySingleEvent);
            }
            debugHandler.debug(
                    player,
                    playerData.getPlayingChapterData(),
                    playerData.getPlayingTaskData(),
                    "Delay",
                    Arrays.asList(
                            "&7delayTime: &f" + contentList.get(1),
                            "&7content: &f" + contentList.get(2) + "|" + contentList.get(3),
                            "&7isSingle: &atrue",
                            "&7isDelayed: &cfalse"
                    ),
                    delaySingleEvent.isCancelled()
            );
            return true;
        }

        if (narrator.getCacheData().isCurrentDelayEventExist(player)) {
            boolean delayed = narrator.getCacheData().isCurrentDelayEventDelayed(player);
            debugHandler.debug(
                    player,
                    playerData.getPlayingChapterData(),
                    playerData.getPlayingTaskData(),
                    "Delay",
                    Arrays.asList(
                            "&7delayTime: &f" + contentList.get(1),
                            "&7isSingle: &cfalse",
                            "&7isDelayed: " + (delayed ? "&atrue" : "&cfalse")
                    ),
                    false
            );
            return delayed;
        }
        DelayEvent delayEvent = new DelayEvent(Long.parseLong(contentList.get(1)));
        EventUtil.callEvent(delayEvent);
        if (!delayEvent.isCancelled()) {
            delayEvent.delay();
            narrator.getCacheData().putCurrentDelayEvent(player, delayEvent);
        }
        debugHandler.debug(
                player,
                playerData.getPlayingChapterData(),
                playerData.getPlayingTaskData(),
                "Delay",
                Arrays.asList(
                        "&7delayTime: &f" + contentList.get(1),
                        "&7isSingle: &cfalse",
                        "&7isDelayed: &cfalse"
                ),
                false
        );
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
            boolean isDecided = narrator.getCacheData().isCurrentActionBarAnswerEventDecided(player);
            debugHandler.debug(
                    player,
                    playerData.getPlayingChapterData(),
                    playerData.getPlayingTaskData(),
                    "ActionBarAnswer",
                    Arrays.asList(
                            "&7options: &f" + contentList.subList(1, contentList.size()),
                            "&7isDecided: " + (isDecided ? "&atrue" : "&cfalse")
                    ),
                    false
            );
            return isDecided;
        }
        ActionBarAnswerEvent actionBarAnswerEvent = new ActionBarAnswerEvent(
                narrator,
                player,
                playerData,
                contentList.subList(1, contentList.size())
        );
        debugHandler.debug(
                player,
                playerData.getPlayingChapterData(),
                playerData.getPlayingTaskData(),
                "ActionBarAnswer",
                Arrays.asList(
                        "&7options: &f" + contentList.subList(1, contentList.size()),
                        "&7isDecided: &cfalse"
                ),
                false
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
        PlayerData playerData = managerHandler.getPlayerManager().getByPlayer(player);

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
            debugHandler.debug(
                    player,
                    playerData.getPlayingChapterData(),
                    playerData.getPlayingTaskData(),
                    "ActionBar",
                    Arrays.asList(
                            "&7message: &f" + contentList.get(2),
                            "&7isPrint: &f" + false,
                            "&7printInterval: &fN/A",
                            "&7duration: &fN/A",
                            "&7isEnded: &atrue"
                    ),
                    false
            );
            return true;
        }

        EventUtil.callEvent(actionBarEvent);
        actionBarEvent.showActionbar();
        debugHandler.debug(
                player,
                playerData.getPlayingChapterData(),
                playerData.getPlayingTaskData(),
                "ActionBar",
                Arrays.asList(
                        "&7message: &f" + contentList.get(4),
                        "&7isPrint: &f" + true,
                        "&7printInterval: &f" + contentList.get(2),
                        "&7duration: &f" + contentList.get(3),
                        "&7isEnded: " + (actionBarEvent.isEnded() ? "&atrue" : "&cfalse")
                ),
                false
        );
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

        PlayerData playerData = managerHandler.getPlayerManager().getByPlayer(player);
        debugHandler.debug(
                player,
                playerData.getPlayingChapterData(),
                playerData.getPlayingTaskData(),
                "Title",
                "&7title: &f" + contentList.get(4) +
                        " &7| " + "&7subTitle: &f" + contentList.get(5) +
                        " &7| " + "&7fadeIn: &f" + contentList.get(1) + "&7keep: &f" + contentList.get(2) + "&7fadeOut: &f" + contentList.get(3),
                titleEvent.isCancelled()
        );

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

        PlayerData playerData = managerHandler.getPlayerManager().getByPlayer(player);
        debugHandler.debug(
                player,
                playerData.getPlayingChapterData(),
                playerData.getPlayingTaskData(),
                "Message",
                "&7message: &f" + contentList.get(1),
                messageEvent.isCancelled()
        );
    }

    /**
     * 转跳章节
     * @param player - 玩家
     * @param playerData - 玩家数据
     * @param chapterData - 章节数据
     * @param contentList - 内容列表
     * @param jumpContent - 关于转跳的content
     * @return 转跳完成
     */
    private boolean handleJumpTask(Player player, PlayerData playerData, ChapterData chapterData, List<String> contentList, String jumpContent) {

        int taskOrdinal = Integer.parseInt(contentList.get(1));
        int contentIndex = Integer.parseInt(contentList.get(2));

        JumpTaskEvent jumpTaskEvent = new JumpTaskEvent(narrator, player, playerData, chapterData, taskOrdinal, contentIndex, jumpContent);
        EventUtil.callEvent(jumpTaskEvent);
        debugHandler.debug(
                player,
                playerData.getPlayingChapterData(),
                playerData.getPlayingTaskData(),
                "jumpTask",
                Arrays.asList(
                        "targetChapter: &f" + chapterData.getName(),
                        "targetTaskOrdinal: &f" + taskOrdinal,
                        "targetContentIndex: &f" + contentIndex,
                        "originalContent: &f" + jumpContent
                ),
                false
        );
        return jumpTaskEvent.jumpTask();
    }

}

