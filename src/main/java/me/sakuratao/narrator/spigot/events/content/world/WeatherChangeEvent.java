package me.sakuratao.narrator.spigot.events.content.world;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;
import me.sakuratao.narrator.spigot.enums.Weather;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import me.sakuratao.narrator.spigot.utils.PacketUtil;
import me.sakuratao.narrator.spigot.utils.TaskUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicReference;

public class WeatherChangeEvent extends NarratorEvent implements Cancellable {

    @Getter private final Player player;
    @Getter private final Weather weather;

    private BukkitTask fade;

    public WeatherChangeEvent(Player player, Weather weather){
        this.player = player;
        this.weather = weather;
    }

    public void changeWeather(){
        if (weather.equals(Weather.CLEAR)) {
            player.resetPlayerWeather();
            return;
        }

        PacketContainer weatherPacket = PacketUtil.createPacket(PacketType.Play.Server.GAME_STATE_CHANGE);
        if (weather.equals(Weather.SUNSHINE)) {
            player.resetPlayerWeather();
            return;
        }
        if (weather.equals(Weather.THUNDER)) {
            AtomicReference<Float> fadein = new AtomicReference<>(0F);
            this.fade = TaskUtil.taskTimerAsync(() -> {
                weatherPacket.getGameStateIDs().write(0, 7);
                if (fadein.get() < 1F) {
                    fadein.set(fadein.get() + 0.01f);
                    weatherPacket.getModifier().write(1, fadein.get());
                    PacketUtil.sendPacket(player, weatherPacket);
                } else {
                    fade.cancel();
                }
            },0, 1);
            return;
        }
        if (weather.equals(Weather.RAINING)) {
            weatherPacket.getGameStateIDs().write(0, 2);
            weatherPacket.getModifier().write(1, 0.5F);
            PacketUtil.sendPacket(player, weatherPacket);
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
        if (fade != null) fade.cancel();
    }

}
