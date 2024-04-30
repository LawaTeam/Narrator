package me.sakuratao.narrator.spigot.events.content.delay;

import lombok.Getter;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import me.sakuratao.narrator.spigot.utils.TaskUtil;
import org.bukkit.event.Cancellable;
import org.bukkit.scheduler.BukkitTask;

public class DelayEvent extends NarratorEvent implements Cancellable {

    @Getter private final long delayTime;
    @Getter private boolean delayed = false;

    private BukkitTask delayTask;

    public DelayEvent(long delayTime){
        super(true);
        this.delayTime = delayTime;
    }

    public void delay(){
        delayTask = TaskUtil.taskLaterAsync(() -> delayed = true, delayTime);
    }

    private boolean isCancelled = false;

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
        delayed = isCancelled;
        if (delayed && delayTask != null) {
            delayTask.cancel();
        }
    }

}
