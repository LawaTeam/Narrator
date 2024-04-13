package me.sakuratao.narrator.spigot.events;

import me.sakuratao.narrator.common.Narrator;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

public abstract class NarratorEvent extends Event {

    private static final HandlerList handlers = new HandlerList();


    public NarratorEvent(boolean isAsync){
        super(isAsync);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
