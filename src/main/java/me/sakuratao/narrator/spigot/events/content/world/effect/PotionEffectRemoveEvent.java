package me.sakuratao.narrator.spigot.events.content.world.effect;

import lombok.Getter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectRemoveEvent extends NarratorEvent implements Cancellable {

    private final Narrator narrator;

    @Getter private final Player player;
    @Getter private final String effect;

    public PotionEffectRemoveEvent(Narrator narrator, Player player, String effect) {
        super(true);
        this.narrator = narrator;
        this.player = player;
        this.effect = effect;
    }

    public void removePotionEffect(){
        PotionEffectType type = PotionEffectType.getByName(effect);
        if (type != null) {
            player.removePotionEffect(type);
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
