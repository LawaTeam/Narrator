package me.sakuratao.narrator.spigot.events.content.world;

import lombok.Getter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.enums.FunctionType;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import me.sakuratao.narrator.spigot.handlers.FunctionHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TeleportEvent extends NarratorEvent implements Cancellable {

    private final Narrator narrator;

    private final FunctionHandler fh;

    @Getter private final FunctionType tpType;
    @Getter private final String target;
    @Getter private final Player player;

    public TeleportEvent(Narrator narrator, String target, Player player) {
        super(false);
        this.narrator = narrator;
        fh = narrator.getHandlerManager().getFunctionHandler();

        this.tpType = fh.getType(target);
        this.target = target;
        this.player = player;
    }

    public void teleport() {

        switch (tpType) {
            case LOC -> {
                Location location = fh.decodeLoc(target);
                if (location != null && location.getWorld() != null) {
                    player.teleport(location);
                }
            }
            case PLAYER -> {
                Player targetPlayer = fh.decodePlayer(target);
                if (targetPlayer != null) {
                    player.teleport(targetPlayer);
                }
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
