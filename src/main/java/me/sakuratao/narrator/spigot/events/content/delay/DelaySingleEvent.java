package me.sakuratao.narrator.spigot.events.content.delay;

import lombok.Getter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import me.sakuratao.narrator.spigot.task.ContentTask;
import me.sakuratao.narrator.spigot.utils.TaskUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class DelaySingleEvent extends NarratorEvent implements Cancellable {

    private final Narrator narrator;

    @Getter private final long delayTime;
    @Getter private final Player player;
    @Getter private final ChapterData chapterData;
    @Getter private final ContentTask contentTask;
    @Getter private final String content;
    @Getter private boolean delayed = false;

    private BukkitTask task;

    public DelaySingleEvent(Narrator narrator, Player player, ChapterData chapterData, ContentTask contentTask, List<String> contentList){
        super(true);
        this.narrator = narrator;
        this.player = player;
        this.chapterData = chapterData;
        this.contentTask = contentTask;
        this.delayTime = Long.parseLong(contentList.get(1));
        this.content = generateContent(contentList);
    }

    public void delay(){
        task = TaskUtil.taskLaterAsync(() ->
                narrator.getHandlerManager().getContentHandler().execute(player, chapterData, content, contentTask), delayTime);
    }

    private String generateContent(List<String> contentList) {
        StringBuilder sb = new StringBuilder();
        for (int i = 2; i <= contentList.size() - 1; i++){
            sb.append(contentList.get(i));
            if (i != contentList.size() - 1) {
                sb.append("|");
            }
        }
        return sb.toString();
    }

    private boolean isCancelled = false;

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
        delayed = true;
        if (task != null) {
            task.cancel();
        }
    }

}
