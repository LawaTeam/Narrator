package me.sakuratao.narrator.spigot.manager;

import lombok.Getter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.NarratorSpigot;
import me.sakuratao.narrator.spigot.data.player.PlayerData;
import me.sakuratao.narrator.spigot.task.ContentTask;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.util.concurrent.ConcurrentHashMap;

@PieComponent
public class TaskManager {

    @Getter
    private final ConcurrentHashMap<String, BukkitTask> tasks = new ConcurrentHashMap<>();

    @Wire private Narrator narrator;

    /**
     *
     * 创建任务，作用于玩家游玩时解析对应 content 并执行
     *
     * @param data - PlayerData
     */
    public void createTask(@NotNull PlayerData data) {

        if (tasks.containsKey(data.getPlayerName())) {
            narrator.getCacheData().clear(data.getPlayer());
            tasks.get(data.getPlayerName()).cancel();
        }

        data.setContentTask(new ContentTask(narrator, data));
        tasks.put(
                data.getPlayerName().toLowerCase(),
                Bukkit.getScheduler().runTaskTimerAsynchronously(NarratorSpigot.getPluginInstance(),
                        data.getContentTask(),
                        0, 5
                )
        );
    }

    /**
     * 结束一个 task
     * @param playerName - 玩家名
     */
    public void killTask(String playerName){
        tasks.get(playerName.toLowerCase()).cancel();
        tasks.remove(playerName.toLowerCase());
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
