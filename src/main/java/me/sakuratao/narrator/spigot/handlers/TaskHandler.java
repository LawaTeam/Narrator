package me.sakuratao.narrator.spigot.handlers;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.configuration.Lang;
import me.sakuratao.narrator.spigot.data.Player.PlayerData;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.data.chapter.TaskData;
import me.sakuratao.narrator.spigot.utils.ServerUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@PieComponent
public class TaskHandler {

    @Wire
    private Narrator narrator;

    @Wire
    private HandlerManager handlerManager;

    /*
        对任务进行加载
     */
    public void load(){
        for (String lang : handlerManager.getChapterHandler().getTotalLang()) {
            for (Map<ChapterData, YamlConfiguration> chapterDataMap : handlerManager.getChapterHandler().getLangChapterMaps(lang)) {
                for (YamlConfiguration contentConfig : chapterDataMap.values()) {
                    try {
                        ChapterData data = handlerManager.getChapterHandler().getDataByMap(chapterDataMap);
                        clear(data);

                        for (String section : contentConfig.getConfigurationSection("chapterTasks").getKeys(false)) {

                            if (
                                    contentConfig.getString("chapterTasks." + section + ".name") == null ||
                                            contentConfig.getString("chapterTasks." + section + ".ordinal") == null
                            ) {
                                ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_NUMBER_FORMAT);
                                ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_HELP);
                                ServerUtil.log(Level.SEVERE, "        chapterName: " + data.getName());
                                ServerUtil.log(Level.SEVERE, "        taskName: " + section);
                                return;
                            }

                            String name = contentConfig.getString("chapterTasks." + section + ".name");
                            int ordinal = Integer.parseInt(contentConfig.getString("chapterTasks." + section + ".ordinal")); // throw NumberFormatException
                            List<String> content = contentConfig.getStringList("chapterTasks." + section + ".content");

                            if (ordinal < 1) {
                                ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_NUMBER_FORMAT);
                                ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_HELP);
                                ServerUtil.log(Level.SEVERE, "        chapterName: " + data.getName());
                                ServerUtil.log(Level.SEVERE, "        taskName: " + section);
                                return;
                            }

                            if (data.getTasks().stream().anyMatch(t -> {

                                /*
                                    同名
                                 */
                                if (!t.getSection().equalsIgnoreCase(section)) {

                                    System.out.println(name);
                                    System.out.println(ordinal);
                                    System.out.println("");
                                    System.out.println(t.getName());
                                    System.out.println(t.getOrdinal());

                                    if (t.getName().equalsIgnoreCase(name)) {
                                        ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_NAME_SAME);
                                        ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_HELP);
                                        ServerUtil.log(Level.SEVERE, "        chapterName: " + data.getName());
                                        ServerUtil.log(Level.SEVERE, "        taskName: " + t.getName());
                                        return true;
                                    }

                                    /*
                                        同序号
                                     */
                                    if (t.getOrdinal() == ordinal) {
                                        ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_ORDINAL_CONFLICT);
                                        ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_HELP);
                                        ServerUtil.log(Level.SEVERE, "        chapterName: " + data.getName());
                                        ServerUtil.log(Level.SEVERE, "        taskName: " + section);
                                        ServerUtil.log(Level.SEVERE, "        taskName: " + t.getName());
                                        ServerUtil.log(Level.SEVERE, "        ordinal: " + t.getOrdinal());
                                        return true;
                                    }
                                }

                                return false;

                            })) return;

                            TaskData taskData = new TaskData();
                            taskData.setSection(section);
                            taskData.setName(name);
                            taskData.setOrdinal(ordinal);
                            taskData.setContent(content);
                            data.getTasks().add(taskData);
                        }
                        data.getTasks().sort(Comparator.comparingInt(TaskData::getOrdinal));

                    } catch (NumberFormatException e) {
                        ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_NUMBER_FORMAT);
                        ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_HELP);
                        ServerUtil.log(Level.SEVERE, "        chapterFile: " + contentConfig.getName());
                        return;
                    }
                }

            }
        }

    }

    public TaskData getTaskByOrdinal(ChapterData data, int ordinal){
        return data.getTasks().stream().filter(taskData -> taskData.getOrdinal() == ordinal).iterator().next();
    }

    public void clear(ChapterData data){
        data.getTasks().clear();
    }

    public void jump(PlayerData playerData, ChapterData chapterData, int taskOrdinal, int contentIndex){
        playerData.setPlayingTaskOrdinal(taskOrdinal);
        playerData.setContentIndex(contentIndex - 1); // 此处 -1 是为了抵消 ContentTask 的+1
        playerData.setPlayingTask(getTaskByOrdinal(chapterData, taskOrdinal));
    }

}
