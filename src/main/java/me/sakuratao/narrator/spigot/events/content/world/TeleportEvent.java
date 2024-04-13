package me.sakuratao.narrator.spigot.events.content.world;

import lombok.Getter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.api.NarratorSpigotAPIProvider;
import me.sakuratao.narrator.spigot.enums.TeleportType;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import me.sakuratao.narrator.spigot.handlers.FunctionHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TeleportEvent extends NarratorEvent implements Cancellable {

    private final Narrator narrator;

    @Getter private final TeleportType tpType;
    @Getter private final String target;
    @Getter private final Player player;

    public TeleportEvent(Narrator narrator, String typeName, String target, Player player) {
        super(false);
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
            case NPC -> {
                // todo teleport
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

    private static final HandlerList handlers = new HandlerList();

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
