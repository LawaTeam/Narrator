package me.sakuratao.narrator.spigot.data.Player;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.data.chapter.TaskData;
import org.bukkit.entity.Player;

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
    private int playingChapterOrdinal = 0;
    private int playingTaskOrdinal = 0;
    private int executingContentIndex = 0;

    private String messageOption = "";
    private boolean nextContent = true;

    public PlayerData(Player player){
        this.playerName = player.getName().toLowerCase();
        this.player = player;
    }

}
