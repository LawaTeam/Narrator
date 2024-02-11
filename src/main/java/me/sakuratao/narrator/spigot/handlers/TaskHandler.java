package me.sakuratao.narrator.spigot.handlers;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.common.handlers.HandlerManager;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.data.chapter.TaskData;
import org.bukkit.configuration.file.YamlConfiguration;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@PieComponent
public class TaskHandler {

    @Wire
    private Narrator narrator;

    @Wire
    private HandlerManager handlerManager;

    public void load(){
        for (Map<ChapterData,  YamlConfiguration> chapterDataMap : handlerManager.getChapterHandler().getChapters().values()) {
            for (YamlConfiguration contentConfig : chapterDataMap.values()) {
                for (String task : Objects.requireNonNull(contentConfig.getConfigurationSection("chapterTasks")).getKeys(false)) {
                    TaskData taskData = new TaskData();
                    taskData.setName(contentConfig.getString("chapterTasks." + task + ".name"));
                    taskData.setOrdinal(contentConfig.getInt("chapterTasks." + task + ".ordinal"));
                    taskData.setContent(contentConfig.getStringList("chapterTasks." + task + ".content"));
                }
            }
        }
    }


}
