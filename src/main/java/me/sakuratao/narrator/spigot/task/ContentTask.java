package me.sakuratao.narrator.spigot.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.data.Player.PlayerData;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.data.chapter.TaskData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

@Getter
@Setter
@Builder
public class ContentTask implements Runnable{

    private final Narrator narrator;
    private final PlayerData data;

    /*
        These are work for actionbar print model
     */
    private boolean printing = false;
    private boolean printKeeping = false;
    private boolean printed = false;
    private BukkitTask printTask = null;

    /*
        These are work for 消息选择 model
     */
    private int messageIndex = -1;
    private int messageSize = 0;
    private boolean messageDeciding = false;
    private boolean messageDecided = false;
    private BukkitTask messageTask = null;

    @Override
    public void run() {

        if (narrator.getHandlerManager().getContentHandler().execute(
                data.getPlayer(),
                data.getPlayingChapter(),
                data.getExecutingContent(),
                this
        )){

            printing = false;
            printKeeping = false;
            printed = false;
            printTask = null;

            messageIndex = -1;
            messageSize = 0;
            messageDeciding = false;
            messageDecided = false;
            messageTask = null;

            List<String> content = data.getPlayingTask().getContent();

            data.setExecutingContentIndex(data.getExecutingContentIndex() + 1);

            if (data.getExecutingContentIndex() >= content.size()) {
                narrator.getManagerHandler().getTaskManager().finish(data.getPlayerName());
                return;
            }
            data.setExecutingContent(content.get(data.getExecutingContentIndex()));

        }

    }

}
