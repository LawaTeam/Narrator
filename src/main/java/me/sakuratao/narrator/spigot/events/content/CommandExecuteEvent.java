package me.sakuratao.narrator.spigot.events.content;

import lombok.Getter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.util.logging.Level;

public class CommandExecuteEvent extends NarratorEvent implements Cancellable {

    private final Narrator narrator;

    @Getter private final Player player;
    @Getter private final ChapterData chapterData;
    @Getter private final String command;

    public CommandExecuteEvent(Narrator narrator, Player player, ChapterData data, String command) {
        super(true);
        this.narrator = narrator;
        this.player = player;
        this.chapterData = data;
        this.command = command;
    }

    public void executeCommand(){
        narrator.getLogger().log(Level.WARNING, "Executed command: " + command + " | Chapter: " + chapterData.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    private boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
