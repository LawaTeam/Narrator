package me.sakuratao.narrator.spigot.events.content;

import lombok.Getter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.enums.TeleportType;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import me.sakuratao.narrator.spigot.handlers.FunctionHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class TeleportEvent extends NarratorEvent implements Cancellable {

    private final Narrator narrator;

    @Getter private final TeleportType tpType;
    @Getter private final String target;
    @Getter private final Player player;

    public TeleportEvent(Narrator narrator, String typeName, String target, Player player) {
        this.narrator = narrator;
        this.tpType = TeleportType.valueOf(typeName.toUpperCase());
        this.target = target;
        this.player = player;
    }

    public void teleport() {
        FunctionHandler fh = narrator.getHandlerManager().getFunctionHandler();
        switch (tpType) {
            case LOC -> {
                Location location = fh.decodeLoc(target);
                if (location.getWorld() != null) {
                    player.teleport(location);
                }
            }
            case PLAYER -> {
                Player targetPlayer = fh.decodePlayer(target);
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
