package me.sakuratao.narrator.spigot.events.content.world;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChangeGameState;
import lombok.Getter;
import me.sakuratao.narrator.spigot.enums.Weather;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import me.sakuratao.narrator.spigot.utils.server.PacketUtil;
import me.sakuratao.narrator.spigot.utils.server.TaskUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicReference;

public class WeatherChangeEvent extends NarratorEvent implements Cancellable {

    @Getter private final Player player;
    @Getter private final Weather weather;

    private BukkitTask fade;

    public WeatherChangeEvent(Player player, Weather weather){
        super(true);
        this.player = player;
        this.weather = weather;
    }

    public void changeWeather(){
        if (weather.equals(Weather.CLEAR)) {
            player.resetPlayerWeather();
            return;
        }

        if (weather.equals(Weather.SUNSHINE)) {
            player.resetPlayerWeather();
            return;
        }
        if (weather.equals(Weather.THUNDER)) {
            AtomicReference<Float> fadein = new AtomicReference<>(0F);
            this.fade = TaskUtil.taskTimerAsync(() -> {
                if (fadein.get() < 1F) {
                    fadein.set(fadein.get() + 0.01f);
                    PacketUtil.sendPacket(
                            player,
                            new WrapperPlayServerChangeGameState(
                                    WrapperPlayServerChangeGameState.Reason.THUNDER_LEVEL_CHANGE,
                                    fadein.get()
                            )
                    );
                } else {
                    fade.cancel();
                }
            },0, 1);
            return;
        }
        if (weather.equals(Weather.RAINING)) {
            PacketUtil.sendPacket(
                    player,
                    new WrapperPlayServerChangeGameState(
                            WrapperPlayServerChangeGameState.Reason.RAIN_LEVEL_CHANGE,
                            0.5F
                    )
            );
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
