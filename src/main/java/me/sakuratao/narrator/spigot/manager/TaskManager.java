package me.sakuratao.narrator.spigot.manager;

import lombok.Getter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.NarratorSpigot;
import me.sakuratao.narrator.spigot.data.player.PlayerData;
import me.sakuratao.narrator.spigot.task.ContentTask;
import me.sakuratao.narrator.spigot.utils.server.TaskUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.util.concurrent.ConcurrentHashMap;

@PieComponent
public class TaskManager {

    @Getter
    private final ConcurrentHashMap<String, BukkitTask> tasks = new ConcurrentHashMap<>(); // String 为 lowercase

    @Wire private Narrator narrator;

    /**
     * 创建任务，作用于玩家游玩时解析对应 content 并执行
     * @param data - PlayerData
     */
    public void createTask(@NotNull PlayerData data) {
        String playerName = data.getPlayerName().toLowerCase();
        if (tasks.containsKey(playerName)) {
            narrator.getCacheData().clear(data.getPlayer());
            tasks.get(playerName).cancel();
        }
        /*      fixme 报错
                World cloneWorld = WorldCreator
                        .name(data.getPlayingTaskData().getWorld().getName() + "_clone_" + data.getPlayer().getName()) // worldName_clone_playerName
                        .copy(data.getPlayingTaskData().getWorld()).createWorld();

                data.setPlayingWorld(cloneWorld);
         */
        data.setContentTask(new ContentTask(narrator, data));
        tasks.put(
                playerName,
                TaskUtil.taskTimerAsync(data.getContentTask(), 0, 5)
        );
    }

    /**
     * 结束一个 task
     * @param playerName - 玩家名
     */
    public void killTask(String playerName){
        if (!isExists(playerName)) return;
        tasks.get(playerName.toLowerCase()).cancel();
        tasks.remove(playerName.toLowerCase());
    }

    public void cancelAll(){
        tasks.forEach((s, bukkitTask) -> bukkitTask.cancel());
        tasks.clear();
    }

    /**
     * 检查是否存在与该 playerName 相关的 task
     * @param playerName - 玩家名
     * @return 是否存在
     */
    public boolean isExists(String playerName){
        return tasks.containsKey(playerName.toLowerCase());
    }

}
