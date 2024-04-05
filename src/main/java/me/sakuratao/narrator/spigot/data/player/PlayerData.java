package me.sakuratao.narrator.spigot.data.player;

import lombok.Data;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.data.chapter.TaskData;
import me.sakuratao.narrator.spigot.task.ContentTask;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@Data
public class PlayerData {

    private final String playerName; // lowercase
    private final Player player;

    private String lang; // todo

    /*
        these were been set in TaskManager
     */
    private ChapterData playingChapter = null;
    private TaskData playingTask = null;

    /*
        these decided what will be set in TaskManager
     */
    private int playingChapterOrdinal = 1; // TODO: 这俩playing倒时后要存库
    private int playingTaskOrdinal = 1;
    private int contentIndex = 0;

    /*
        This work for AB_ANSWER
     */
    private String messageOption = "";

    private ContentTask contentTask;

    private Inventory chatHandle; // TODO 提供过剧情时 聊天框的控制

    public PlayerData(Player player){
        this.playerName = player.getName().toLowerCase();
        this.player = player;
    }

}
