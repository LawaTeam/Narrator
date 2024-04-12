package me.sakuratao.narrator.spigot.events.content;

import me.sakuratao.narrator.spigot.enums.TeleportType;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.util.Arrays;
import java.util.List;

public class TeleportEvent extends NarratorEvent implements Cancellable {

    private final TeleportType tpType;
    private final String target;
    private  final Player player;

    public TeleportEvent(String typeName, String target, Player player) {
        this.tpType = TeleportType.valueOf(typeName.toUpperCase());
        this.target = target;
        this.player = player;
    }

    public void teleport() {
        String[] split = target.split("<");
        switch (tpType) {
            case LOC -> {

                String worldName = split[0];
                List<Double> coordinate = Arrays.stream(split[1].split(",")).map(s -> Double.parseDouble(s.replace(">", ""))).toList();

                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    Location location = new Location(world, coordinate.get(0), coordinate.get(1), coordinate.get(2));
                    player.teleport(location);
                }
            }
            case PLAYER -> {
                Player targetPlayer = Bukkit.getPlayer(split[0]);
                if (targetPlayer != null) {
                    player.teleport(targetPlayer);
                }
            }
            case ENTITY -> {
                // todo
            }
        }
    }

    private boolean isCancelled = false;

    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

}
