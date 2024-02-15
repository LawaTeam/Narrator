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

    private ChapterData playingChapter = null;
    private int playingChapterOrdinal = 0;

    private TaskData playingTask = null;
    private int playingTaskOrdinal = 0;

    private String executingContent = null;
    private int executingContentIndex = 0;

    public PlayerData(Player player){
        this.playerName = player.getName().toLowerCase();
        this.player = player;
    }

}
