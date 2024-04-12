package me.sakuratao.narrator.spigot.events.content;

import me.sakuratao.narrator.spigot.events.NarratorEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class TeleportEvent extends NarratorEvent implements Cancellable {

    private Location locFrom;
    private Location locTo;
    private Entity entity;
    private Entity to;
    private boolean cancel;

    public TeleportEvent(Entity entity, Entity to, Location locFrom, Location locTo) {
        this.entity = entity;
        this.to = to;
        this.locFrom = locFrom;
        this.locTo = locTo;
    }

    public void teleportEntityWithLoc() {
        entity.teleport(locTo);
    }

    public void teleportEntityWithEntity() {
        entity.teleport(to);
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
