package me.sakuratao.narrator.spigot.events.content.delay;

import lombok.Getter;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import me.sakuratao.narrator.spigot.utils.TaskUtil;
import org.bukkit.event.Cancellable;

public class DelayEvent extends NarratorEvent implements Cancellable {

    @Getter private final long delayTime;
    @Getter private boolean delayed = false;

    public DelayEvent(long delayTime){
        this.delayTime = delayTime;
    }

    public void delay(){
        TaskUtil.taskLaterAsync(() -> delayed = true, delayTime);
    }

    private boolean isCancelled = false;

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
        delayed = true; // 如果出问题留意一下这里
    }

}
