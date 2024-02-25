package me.sakuratao.narrator.spigot.data.Player;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.data.chapter.TaskData;
import me.sakuratao.narrator.spigot.task.ContentTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

@Data
public class PlayerData {

    private final String playerName; // lowercase
    private final Player player;

    /*
        these were been set in TaskManager
     */
    private ChapterData playingChapter = null;
    private TaskData playingTask = null;
    private String executingContent = "";

    /*
        these decided what will be set in TaskManager
     */
    private int playingChapterOrdinal = 1;
    private int playingTaskOrdinal = 1;
    private int executingContentIndex = 1;

    /*
        This work for AB_ANSWER
     */
    private String messageOption = "";

    private ContentTask contentTask;

    public PlayerData(Player player){
        this.playerName = player.getName().toLowerCase();
        this.player = player;
    }

}
