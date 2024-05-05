package me.sakuratao.narrator.spigot.handlers;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.configuration.Lang;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.data.chapter.TaskData;
import me.sakuratao.narrator.spigot.data.player.PlayerData;
import me.sakuratao.narrator.spigot.utils.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
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

    /**
     * 加载任务
     */
    public void load(){
        for (String lang : handlerManager.getChapterHandler().getTotalLang()) {
            for (Map<ChapterData, YamlConfiguration> chapterDataMap : handlerManager.getChapterHandler().getLangChapterMaps(lang)) {
                for (YamlConfiguration contentConfig : chapterDataMap.values()) {
                    try {
                        ChapterData data = handlerManager.getChapterHandler().getDataByMap(chapterDataMap);
                        for (String section : contentConfig.getConfigurationSection("chapterTasks").getKeys(false)) {

                            if (isOptionsNull(contentConfig, section, data)) return;

                            String name = contentConfig.getString("chapterTasks." + section + ".name");
                            String worldName = contentConfig.getString( "chapterTasks." + section + ".world");
                            World world = Bukkit.getWorld(worldName);
                            int ordinal = Integer.parseInt(contentConfig.getString("chapterTasks." + section + ".ordinal"));
                            List<String> content = contentConfig.getStringList("chapterTasks." + section + ".content");

                            if (isWorldNull(data, name, world, worldName)) return;
                            if (isOrdinalLowerThanOne(ordinal, data, section)) return;
                            if (isNameOrOrdinalEquals(data, section, name, ordinal)) return;

                            TaskData taskData = new TaskData();
                            taskData.setSection(section);
                            taskData.setName(name);
                            taskData.setWorld(world);
                            taskData.setOrdinal(ordinal);
                            taskData.setContent(content);
                            data.getTasks().add(taskData);
                        }
                        data.getTasks().sort(Comparator.comparingInt(TaskData::getOrdinal));

                    } catch (NumberFormatException e) {
                        LogUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_NUMBER_FORMAT);
                        LogUtil.log(Level.SEVERE, Lang.CHAPTERS_CONSOLE_HELP);
                        LogUtil.log(Level.SEVERE, "        chapterFile: " + contentConfig.getName());
                        LogUtil.log(Level.SEVERE, "Details(For Developments): " + e.getMessage());
                        return;
                    }
                }

            }
        }

    }

    private boolean isWorldNull(ChapterData data, String taskName, World world, String worldName) {
        if (world == null) {
            LogUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_TASK_WORLD_NULL);
            LogUtil.log(Level.SEVERE, Lang.CHAPTERS_CONSOLE_HELP);
            LogUtil.log(Level.SEVERE, "        chapterName: " + data.getName());
            LogUtil.log(Level.SEVERE, "        taskName: " + taskName);
            LogUtil.log(Level.SEVERE, "        worldName: " + worldName);
            return true;
        }
        return false;
    }

    /**
     * 检查是否存在 名称 或 序数 的冲突
     * @param data - 章节数据
     * @param section - section
     * @param name - 章节名
     * @param ordinal - 序数
     * @return 是否存在冲突
     */
    private boolean isNameOrOrdinalEquals(ChapterData data, String section, String name, int ordinal){
        return data.getTasks().stream().anyMatch(t -> {

            /*
                同名
             */
            if (!t.getSection().equalsIgnoreCase(section)) {

                if (t.getName().equalsIgnoreCase(name)) {
                    LogUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_TASK_NAME_SAME);
                    LogUtil.log(Level.SEVERE, Lang.CHAPTERS_CONSOLE_HELP);
                    LogUtil.log(Level.SEVERE, "        chapterName: " + data.getName());
                    LogUtil.log(Level.SEVERE, "        taskName: " + t.getName());
                    return true;
                }

                /*
                    同序号
                 */
                if (t.getOrdinal() == ordinal) {
                    LogUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_ORDINAL_CONFLICT);
                    LogUtil.log(Level.SEVERE, Lang.CHAPTERS_CONSOLE_HELP);
                    LogUtil.log(Level.SEVERE, "        chapterName: " + data.getName());
                    LogUtil.log(Level.SEVERE, "        taskName: " + section);
                    LogUtil.log(Level.SEVERE, "        taskName: " + t.getName());
                    LogUtil.log(Level.SEVERE, "        ordinal: " + t.getOrdinal());
                    return true;
                }
            }
            return false;
        });
    }

    /**
     * 检查 ordinal 是否小于 1
     * @param ordinal - 序数
     * @param data - 章节数据
     * @param section - section
     * @return - 是否小于
     */
    private boolean isOrdinalLowerThanOne(int ordinal, ChapterData data, String section){
        if (ordinal < 1) {
            LogUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_NUMBER_FORMAT);
            LogUtil.log(Level.SEVERE, Lang.CHAPTERS_CONSOLE_HELP);
            LogUtil.log(Level.SEVERE, "        chapterName: " + data.getName());
            LogUtil.log(Level.SEVERE, "        taskName: " + section);
            return true;
        }
        return false;
    }

    /**
     * 检查 name 、world 以及 ordinal 是否为 null
     * @param contentConfig - 相关的 yml
     * @param section - section
     * @param data - 章节数据
     * @return 是否为 null
     */
    private boolean isOptionsNull(YamlConfiguration contentConfig, String section, ChapterData data){
        if (
                contentConfig.getString("chapterTasks." + section + ".name") == null ||
                        contentConfig.getString( "chapterTasks." + section + ".world") == null ||
                        contentConfig.getString("chapterTasks." + section + ".ordinal") == null
        ) {
            LogUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_TASK_NULL);
            LogUtil.log(Level.SEVERE, Lang.CHAPTERS_CONSOLE_HELP);
            LogUtil.log(Level.SEVERE, "        chapterName: " + data.getName());
            LogUtil.log(Level.SEVERE, "        taskName: " + section);
            return true;
        }
        return false;
    }

    /**
     * 通过 ordinal 获取 task
     * @param data - 章节数据
     * @param ordinal - 需要的 ordinal
     * @return 目标任务数据
     */
    public TaskData getTaskByOrdinal(ChapterData data, int ordinal){
        return data.getTasks().stream().filter(taskData -> taskData.getOrdinal() == ordinal).iterator().next();
    }


    /**
     * 转跳任务
     * @param playerData - 玩家数据
     * @param chapterData - 章节数据
     * @param taskOrdinal - 任务ordinal
     * @param contentIndex - content索引
     */
    public void jump(PlayerData playerData, ChapterData chapterData, int taskOrdinal, int contentIndex){
        if (chapterData.getTasks().stream().anyMatch(taskData -> taskData.getOrdinal() == taskOrdinal)) {
            playerData.setPlayingTaskOrdinal(taskOrdinal);
            playerData.setContentIndex(contentIndex - 1); // 此处 -1 是为了抵消 ContentTask 的 +1，因为执行 jump 后 contentTask 会进行 +1
        } else {
            LogUtil.log(Level.SEVERE, Lang.CHAPTERS_EXECUTE_JT_NOT_EXIST);
            LogUtil.log(Level.SEVERE, Lang.CHAPTERS_CONSOLE_HELP);
            LogUtil.log(Level.SEVERE, "        chapterName: " + chapterData.getName());
            LogUtil.log(Level.SEVERE, "        content: " + playerData.getCurrentContent());
        }
    }

}
