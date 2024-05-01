package me.sakuratao.narrator.spigot.events.content.world.effect;

import lombok.Getter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectGiveEvent extends NarratorEvent implements Cancellable {

    private final Narrator narrator;

    @Getter private final Player player;
    @Getter private final String effect;
    @Getter private final int duration;
    @Getter private final int amplifier;
    @Getter private final boolean hideParticles;


    public PotionEffectGiveEvent(
            Narrator narrator,
            Player player,
            String effect,
            int duration,
            int amplifier,
            boolean hideParticles
    ) {
        super(true);
        this.narrator = narrator;
        this.player = player;
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
        this.hideParticles = hideParticles;
    }

    public void givePotionEffect(){
        PotionEffectType type = PotionEffectType.getByName(effect);
        if (type != null) {
            PotionEffect pe = new PotionEffect(type, duration, amplifier, hideParticles);
            player.addPotionEffect(pe);
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
