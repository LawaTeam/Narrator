package me.sakuratao.narrator.spigot.data.cache;

import lombok.Getter;
import me.sakuratao.narrator.spigot.events.content.actionbar.ActionBarAnswerEvent;
import me.sakuratao.narrator.spigot.events.content.actionbar.ActionBarEvent;
import me.sakuratao.narrator.spigot.events.content.delay.DelayEvent;
import me.sakuratao.narrator.spigot.events.content.delay.DelaySingleEvent;
import me.sakuratao.narrator.spigot.events.content.world.TimeChangeEvent;
import org.bukkit.entity.Player;
import top.jingwenmc.spigotpie.common.instance.PieComponent;

import java.awt.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@PieComponent
public class CacheData {

    private final ConcurrentHashMap<String, ActionBarEvent> currentActionBarEvent = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ActionBarAnswerEvent> currentActionBarAnswerEvent = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, DelayEvent> currentDelayEvent = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<DelaySingleEvent> delaySingleEventList = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<TimeChangeEvent> timeChangeEventList = new CopyOnWriteArrayList<>();

    public void putTimeChangeEvent(TimeChangeEvent e){
        timeChangeEventList.add(e);
    }

    public void cancelAllTimeChangeEvent() {
        timeChangeEventList.forEach(e -> e.setCancelled(true));
        timeChangeEventList.clear();
    }

    /**
     * 添加 delaySingle
     * @param e - DelaySingleEvent
     */
    public void putDelaySingleEvent(DelaySingleEvent e){
        delaySingleEventList.add(e);
    }

    /**
     * 暂停所有 delaySingle 并清空缓存
     */
    public void cancelAllDelaySingleEvent() {
        delaySingleEventList.forEach(e -> e.setCancelled(true));
        delaySingleEventList.clear();
    }

    /**
     * 获取当前的 delay 事件
     * @param player - 玩家
     * @return DelayEvent
     */
    public DelayEvent getCurrentDelayEvent(Player player){
        return currentDelayEvent.get(player.getName().toLowerCase());
    }

    /**
     * 添加当前的 delay 事件
     * @param player - 玩家
     * @param event - 事件
     */
    public void putCurrentDelayEvent(Player player, DelayEvent event){
        currentDelayEvent.put(player.getName().toLowerCase(), event);
    }

    /**
     * 检查关于该玩家的 delay 事件是否存在
     * @param player - 玩家
     * @return boolean
     */
    public boolean isCurrentDelayEventExist(Player player){
        return currentDelayEvent.containsKey(player.getName().toLowerCase());
    }

    /**
     * 检查关于该玩家的 delay 事件是否完成延迟
     * 如果已完成延迟一并移出
     * @param player - 玩家
     * @return boolean
     */
    public boolean isCurrentDelayEventDelayed(Player player){
        DelayEvent delayEvent = getCurrentDelayEvent(player);
        if (delayEvent.isDelayed()) {
            currentDelayEvent.remove(player.getName().toLowerCase());
        }
        return delayEvent.isDelayed();
    }

    /**
     * 获取当前通过 ActionBar 进行 Answer 的事件
     * @param player - 玩家
     * @return ActionBarAnswerEvent
     */
    public ActionBarAnswerEvent getCurrentActionBarAnswerEvent(Player player){
        return currentActionBarAnswerEvent.get(player.getName().toLowerCase());
    }

    /**
     * 添加当前通过 ActionBar 进行 Answer 的事件
     * @param player - 玩家
     * @param event - ActionBarAnswerEvent
     */
    public void putCurrentActionBarAnswerEvent(Player player, ActionBarAnswerEvent event){
        currentActionBarAnswerEvent.put(player.getName().toLowerCase(), event);
    }

    /**
     * 检查当前通过 ActionBar 进行 Answer 的事件是否存在
     * @param player - 玩家
     * @return true - 存在, false - 不存在
     */
    public boolean isCurrentActionBarAnswerEventExist(Player player){
        return currentActionBarAnswerEvent.containsKey(player.getName().toLowerCase());
    }

    /**
     * 检查当前通过 ActionBar 进行 Answer 的事件是否已决定
     * @param player - 玩家
     * @return true - 已决定, false - 未决定
     */
    public boolean isCurrentActionBarAnswerEventDecided(Player player){
        ActionBarAnswerEvent actionBarAnswerEvent = currentActionBarAnswerEvent.get(player.getName().toLowerCase());
        if (actionBarAnswerEvent.isDecided()) {
            currentActionBarAnswerEvent.remove(player.getName().toLowerCase());
        }
        return actionBarAnswerEvent.isDecided();
    }

    /**
     * 获取当前正在显示的 ActionBar 事件
     * @param player - 玩家
     * @return ActionBarEvent
     */
    public ActionBarEvent getCurrentActionBarEvent(Player player){
        return currentActionBarEvent.get(player.getName().toLowerCase());
    }

    /**
     * 添加当前正在显示的 ActionBar 事件
     * @param player - 玩家
     * @param actionBarEvent - 事件
     */
    public void putCurrentActionBarEvent(Player player, ActionBarEvent actionBarEvent){
        currentActionBarEvent.put(player.getName().toLowerCase(), actionBarEvent);
    }

    /**
     * 检查当前正在显示的 ActionBar 事件是否已结束，
     * 如果已经结束一并删除其在 map 中的数据
     * @param player - 玩家
     * @return true - 已结束，false - 未结束
     */
    public boolean isCurrentActionbarEnded(Player player){
        ActionBarEvent actionBarEvent = currentActionBarEvent.get(player.getName().toLowerCase());
        if (actionBarEvent.isEnded()) {
            currentActionBarEvent.remove(player.getName().toLowerCase());
        }
        return actionBarEvent.isEnded();
    }

    /**
     * 检查当前正在显示的 ActionBar 事件是否存在
     * @param player - 玩家
     * @return true - 存在，false - 不存在
     */
    public boolean isCurrentActionBarEventExist(Player player){
        return currentActionBarEvent.containsKey(player.getName().toLowerCase());
    }

    /**
     * 清除玩家相关的缓存
     * @param player - 玩家
     */
    public void clear(Player player){

        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }

        // 使用增强for循环遍历，提高代码的可读性
        for (DelaySingleEvent event : delaySingleEventList) {
            if (event.getPlayer().equals(player)) {
                event.setCancelled(true);
            }
        }
        // 这里不需要手动删除，CopyOnWriteArrayList在迭代时自动处理并发修改

        for (TimeChangeEvent event : timeChangeEventList) {
            if (event.getPlayer().equals(player)) {
                event.setCancelled(true);
            }
        }

        // currentActionBarEvent.get(player.getName().toLowerCase()).getPrintTask().cancel();
        currentActionBarEvent.remove(player.getName().toLowerCase());

        // currentActionBarAnswerEvent.get(player.getName().toLowerCase()).getOptionTask().cancel();
        currentActionBarAnswerEvent.remove(player.getName().toLowerCase());

        // currentDelayEvent.get(player.getName().toLowerCase()).setCancelled(true);
        currentDelayEvent.remove(player.getName().toLowerCase());
    }

}
