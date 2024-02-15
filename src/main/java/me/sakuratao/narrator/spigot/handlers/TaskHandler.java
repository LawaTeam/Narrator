package me.sakuratao.narrator.spigot.handlers;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.data.chapter.TaskData;
import org.bukkit.configuration.file.YamlConfiguration;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

@PieComponent
public class TaskHandler {

    @Wire
    private Narrator narrator;

    @Wire
    private HandlerManager handlerManager;

    public void load(){
        for (Map<ChapterData,  YamlConfiguration> chapterDataMap : handlerManager.getChapterHandler().getChapters().values()) {
            for (YamlConfiguration contentConfig : chapterDataMap.values()) {
                try {
                    ChapterData data = chapterDataMap.keySet().iterator().next();

                    for (String task : contentConfig.getConfigurationSection("chapterTasks").getKeys(false)) {

                        if (
                                contentConfig.getString("chapterTasks." + task + ".name") == null ||
                                        contentConfig.getString("chapterTasks." + task + ".ordinal") == null
                        ) {
                            narrator.getLogger().log(Level.SEVERE, "Please check your chapter file " + contentConfig.getName());
                            narrator.getLogger().log(Level.SEVERE, "Check your options in chapterTasks about Name and Ordinal, they can't be null.");
                            return;
                        }

                        String name = contentConfig.getString("chapterTasks." + task + ".name");
                        int ordinal = Integer.parseInt(contentConfig.getString("chapterTasks." + task + ".ordinal")); // throw NumberFormatException
                        List<String> content = contentConfig.getStringList("chapterTasks." + task + ".content");

                        if (data.getTasks().stream().anyMatch(t -> {

                            boolean result = false;
                            /*
                                同名
                             */
                            if (t.getName().equalsIgnoreCase(name)){
                                narrator.getLogger().log(Level.SEVERE, "It is about tasks! There can't be same name!");
                                result = true;
                            }
                            /*
                                同序号
                             */
                            if (t.getOrdinal() == ordinal && !t.getName().equalsIgnoreCase(name)){
                                narrator.getLogger().log(Level.SEVERE, "It is about tasks! There can't be same ordinal!");
                                result = true;
                            }

                            if (result) {
                                narrator.getLogger().log(Level.SEVERE, "Please check your chapter " + data.getName() + " about Tasks");
                                narrator.getLogger().log(Level.SEVERE, "Here are some information may help you:");
                                narrator.getLogger().log(Level.SEVERE, "        Name: " + name);
                                narrator.getLogger().log(Level.SEVERE, "        Name: " + t.getName());
                                narrator.getLogger().log(Level.SEVERE, "        sameOrdinal: " + ordinal);
                                narrator.getLogger().log(Level.SEVERE, "Load stopped!");
                            }

                            return result;

                        })) return;

                        TaskData taskData = new TaskData();
                        taskData.setName(name);
                        taskData.setOrdinal(ordinal);
                        taskData.setContent(content);
                        data.getTasks().add(taskData);

                    }

                    data.getTasks().sort(Comparator.comparingInt(TaskData::getOrdinal));

                } catch (NumberFormatException e) {
                    narrator.getLogger().log(Level.SEVERE, "Please check your chapter file " + contentConfig.getName() + " about Task's ordinal");
                    narrator.getLogger().log(Level.SEVERE, "The Ordinal must be int");
                    narrator.getLogger().log(Level.SEVERE, "Load stopped!");
                    return;
                }
            }
        }
    }

    public TaskData jump(int ordinal){
        return null;
    }

}
