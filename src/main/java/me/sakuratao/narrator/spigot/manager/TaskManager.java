package me.sakuratao.narrator.spigot.manager;

import lombok.Getter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.NarratorSpigot;
import me.sakuratao.narrator.spigot.data.Player.PlayerData;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
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
            tasks.get(data.getPlayerName()).cancel();
        }

        ChapterData playingChapter = narrator.getHandlerManager().getChapterHandler().getLangSortedChapters().get(data.getLang())
                .stream()
                .filter(
                        k -> k.keySet().iterator().next().getOrdinal() == data.getPlayingChapterOrdinal()
                ).iterator().next()
                .keySet().iterator().next();

        data.setPlayingChapter(playingChapter);
        data.setPlayingTask(playingChapter.getTasks()
                .stream()
                .filter(taskData -> taskData.getOrdinal() == data.getPlayingTaskOrdinal()).iterator().next());

        data.setContentTask(new ContentTask(narrator, data));

        tasks.put(
                data.getPlayerName().toLowerCase(),
                Bukkit.getScheduler().runTaskTimerAsynchronously(NarratorSpigot.getPluginInstance(),
                        data.getContentTask(),
                        0, 10
                )
        );
    }

    public void end(String playerName){
        tasks.get(playerName.toLowerCase()).cancel();
        tasks.remove(playerName.toLowerCase());
    }

}
