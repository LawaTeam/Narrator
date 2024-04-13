package me.sakuratao.narrator.spigot.data.player;

import lombok.Data;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.data.chapter.TaskData;
import me.sakuratao.narrator.spigot.task.ContentTask;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@Data
public class PlayerData {

    private final Narrator narrator;

    private final String playerName; // lowercase
    private final Player player;

    private String lang; // todo 玩家语言检测

    /*
        these decided what will be set in TaskManager
     */
    private World playingWorld; // TODO: 独立于主世界
    private int playingChapterOrdinal = 1; // TODO: 这俩playing到时候要存库
    private int playingTaskOrdinal = 1;
    private int contentIndex = 0;

    private ContentTask contentTask = null;

    private Inventory chatHandle; // TODO 提供过剧情时 聊天框的控制

    public PlayerData(Narrator narrator, Player player){
        this.narrator = narrator;
        this.playerName = player.getName().toLowerCase();
        this.player = player;
    }


    /**
     * 获取此玩家正在游玩的章节数据
     * @return chapterData
     */
    public ChapterData getPlayingChapterData(){
        return narrator.getHandlerManager().getChapterHandler().getDataByPlayerData(this);
    }

    /**
     * 获取此玩家正在游玩的任务
     * @return taskData
     */
    public TaskData getPlayingTaskData(){
        return narrator.getHandlerManager().getTaskHandler().getTaskByOrdinal(getPlayingChapterData(), playingTaskOrdinal);
    }

    /**
     * 获取该玩家当前执行的 content
     * 此 content 会在 contentTask 的 execute() 返回 true 后刷新
     * @return content
     */
    public String getCurrentContent(){
        return getPlayingTaskData().getContent().get(contentIndex);
    }


}
