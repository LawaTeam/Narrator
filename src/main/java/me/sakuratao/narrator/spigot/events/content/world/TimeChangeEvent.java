package me.sakuratao.narrator.spigot.events.content.world;

import lombok.Getter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import me.sakuratao.narrator.spigot.utils.server.TaskUtil;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicReference;

public class TimeChangeEvent extends NarratorEvent implements Cancellable {

    private final Narrator narrator;

    @Getter private final Player player;
    @Getter private final long fromTimeTicks;
    @Getter private final long toTimeTicks;
    @Getter private final long increase;
    @Getter private final boolean fade;

    @Getter private BukkitTask fadeTask;

    private long targetTicks;

    /**
     * 更改玩家时间
     * @param player - 玩家
     * @param toTimeTicks - 更改到时间
     * @param fade - 是否渐渐切换
     * @param increase - 每 1 ticks 增长的时间 ticks
     */
    public TimeChangeEvent(Narrator narrator, Player player, long toTimeTicks, boolean fade,  long increase) {
        super(false);
        this.narrator = narrator;
        this.player = player;
        this.fromTimeTicks = player.getWorld().getTime();
        this.toTimeTicks = toTimeTicks;
        this.fade = fade;
        this.increase = increase;
    }


    public void changeTime(){

        World world = player.getWorld();
        if (!fade) {
            world.setTime(toTimeTicks);
            return;
        }

        if (fromTimeTicks > toTimeTicks) {

            AtomicReference<Long> fade = new AtomicReference<>(fromTimeTicks);
            targetTicks = 24000L;

            this.fadeTask = TaskUtil.taskTimer(() -> {
                if (fade.get() < targetTicks) {
                    fade.set(fade.get() + increase);

                    if (fade.get() >= 24000L) {
                        fade.set(0L);
                        targetTicks = toTimeTicks;
                    }

                    world.setTime(fade.get());
                } else {
                    narrator.getCacheData().getTimeChangeEventList().remove(this);
                    fadeTask.cancel();
                    return;
                }
            }, 0, 1);
            return;

        }

        if (fromTimeTicks < toTimeTicks) {
            AtomicReference<Long> fade = new AtomicReference<>(fromTimeTicks);

            this.fadeTask = TaskUtil.taskTimer(() -> {
                if (fade.get() < toTimeTicks) {
                    fade.set(fade.get() + increase);

                    if (fade.get() > toTimeTicks) {
                        fade.set(toTimeTicks);
                    }

                    world.setTime(fade.get());
                } else {
                    narrator.getCacheData().getTimeChangeEventList().remove(this);
                    fadeTask.cancel();
                    return;
                }
            }, 0, 1);
        }


    }

    private boolean isCancelled = false;

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
        if (isCancelled) {
            if (fadeTask != null) fadeTask.cancel();
            narrator.getCacheData().getTimeChangeEventList().remove(this);
        }
    }

}
