package me.sakuratao.narrator.spigot.events.content.world;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import me.sakuratao.narrator.spigot.utils.PacketUtil;
import me.sakuratao.narrator.spigot.utils.TaskUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicReference;

public class TimeChangeEvent extends NarratorEvent implements Cancellable {

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
    public TimeChangeEvent(Player player, long toTimeTicks, boolean fade, long increase) {
        this.player = player;
        this.fromTimeTicks = player.getPlayerTime();
        this.toTimeTicks = toTimeTicks;
        this.fade = fade;
        this.increase = increase;
    }


    public void changeTime(){

        PacketContainer timePacket = PacketUtil.createPacket(PacketType.Play.Server.UPDATE_TIME);
        if (!fade) {
            timePacket.getModifier().write(1, toTimeTicks);
            PacketUtil.sendPacket(player, timePacket);
            return;
        }

        if (fromTimeTicks > toTimeTicks) {

            AtomicReference<Long> fade = new AtomicReference<>(fromTimeTicks);
            targetTicks = 24000L;

            this.fadeTask = TaskUtil.taskTimerAsync(() -> {
                if (fade.get() < targetTicks) {
                    fade.set(fade.get() + increase);

                    if (fade.get() >= 24000L) {
                        fade.set(0L);
                        targetTicks = fromTimeTicks;
                    }

                    timePacket.getModifier().write(1, fade.get());
                    PacketUtil.sendPacket(player, timePacket);
                } else {
                    fadeTask.cancel();
                }
            },0, 1);
            return;

        }

        if (fromTimeTicks < toTimeTicks) {
            AtomicReference<Long> fade = new AtomicReference<>(fromTimeTicks);

            this.fadeTask = TaskUtil.taskTimerAsync(() -> {
                if (fade.get() < toTimeTicks) {

                    fade.set(fade.get() + increase);

                    if (fade.get() > toTimeTicks) {
                        fade.set(toTimeTicks);
                    }

                    timePacket.getModifier().write(1, fade.get());
                    PacketUtil.sendPacket(player, timePacket);
                } else {
                    fadeTask.cancel();
                }
            },0, 1);
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
    }

}
