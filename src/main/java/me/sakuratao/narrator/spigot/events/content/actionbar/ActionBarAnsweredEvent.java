package me.sakuratao.narrator.spigot.events.content.actionbar;

import lombok.Getter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import me.sakuratao.narrator.spigot.utils.CCUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class ActionBarAnsweredEvent extends NarratorEvent implements Cancellable {

    @Getter private final Player player;
    @Getter private final String option;

    public ActionBarAnsweredEvent(Player player, String option) {
        this.player = player;
        this.option = option;
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
