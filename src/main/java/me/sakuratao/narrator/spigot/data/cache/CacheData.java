package me.sakuratao.narrator.spigot.data.cache;

import lombok.Getter;
import me.sakuratao.narrator.spigot.events.content.actionbar.ActionBarAnswerEvent;
import me.sakuratao.narrator.spigot.events.content.actionbar.ActionBarEvent;
import org.bukkit.entity.Player;
import top.jingwenmc.spigotpie.common.instance.PieComponent;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@PieComponent
public class CacheData {

    private final ConcurrentHashMap<String, ActionBarEvent> currentActionbarEvent = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, ActionBarAnswerEvent> currentActionBarAnswerEvent = new ConcurrentHashMap<>();

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
        return currentActionbarEvent.get(player.getName().toLowerCase());
    }

    /**
     * 添加当前正在显示的 ActionBar 事件
     * @param player - 玩家
     * @param actionBarEvent - 事件
     */
    public void putCurrentActionBarEvent(Player player, ActionBarEvent actionBarEvent){
        currentActionbarEvent.put(player.getName().toLowerCase(), actionBarEvent);
    }

    /**
     * 检查当前正在显示的 ActionBar 事件是否已结束，
     * 如果已经结束一并删除其在 map 中的数据
     * @param player - 玩家
     * @return true - 已结束，false - 未结束
     */
    public boolean isCurrentActionbarEnded(Player player){
        ActionBarEvent actionBarEvent = currentActionbarEvent.get(player.getName().toLowerCase());
        if (actionBarEvent.isEnded()) {
            currentActionbarEvent.remove(player.getName().toLowerCase());
        }
        return actionBarEvent.isEnded();
    }

    /**
     * 检查当前正在显示的 ActionBar 事件是否存在
     * @param player - 玩家
     * @return true - 存在，false - 不存在
     */
    public boolean isCurrentActionBarEventExist(Player player){
        return currentActionbarEvent.containsKey(player.getName().toLowerCase());
    }

    /**
     * 清除玩家相关的缓存
     * @param player - 玩家
     */
    public void clear(Player player){
        if (isCurrentActionBarEventExist(player)) {
            currentActionbarEvent.remove(player.getName().toLowerCase());
        }
        if (isCurrentActionBarAnswerEventExist(player)){
            currentActionBarAnswerEvent.remove(player.getName().toLowerCase());
        }
    }

}
