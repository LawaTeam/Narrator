package me.sakuratao.narrator.spigot.manager;

import lombok.Getter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.NarratorSpigot;
import me.sakuratao.narrator.spigot.data.Player.PlayerData;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.handlers.ChapterHandler;
import me.sakuratao.narrator.spigot.handlers.TaskHandler;
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

    @Wire
    private Narrator narrator;

    /**
     *
     * 创建任务，作用于玩家游玩时可以解析对应 content 并执行
     *
     * @param data - PlayerData
     */
    public void createTask(@NotNull PlayerData data) {

        if (tasks.containsKey(data.getPlayerName())) {
            data.getContentTask().killSubTasks();
            tasks.get(data.getPlayerName()).cancel();
        }

        ChapterHandler ch = narrator.getHandlerManager().getChapterHandler();
        TaskHandler th = narrator.getHandlerManager().getTaskHandler();

        ChapterData playingChapter = ch.getDataByOrdinal(data.getPlayingChapterOrdinal(), data.getLang());

        data.setPlayingChapter(playingChapter);
        data.setPlayingTask(th.getTaskByOrdinal(playingChapter, data.getPlayingTaskOrdinal()));

        data.setContentTask(new ContentTask(narrator, data));

        tasks.put(
                data.getPlayerName().toLowerCase(),
                Bukkit.getScheduler().runTaskTimerAsynchronously(NarratorSpigot.getPluginInstance(),
                        data.getContentTask(),
                        0, 10
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

}
