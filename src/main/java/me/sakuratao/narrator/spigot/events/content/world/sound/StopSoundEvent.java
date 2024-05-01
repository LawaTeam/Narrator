package me.sakuratao.narrator.spigot.events.content.world.sound;

import lombok.Getter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class StopSoundEvent extends NarratorEvent {

    private final Narrator narrator;

    @Getter private final Player player;
    @Getter private final String sound;
    @Getter private final SoundCategory category;

    public StopSoundEvent(Narrator narrator, Player player, String sound, SoundCategory category) {
        super(true);
        this.narrator = narrator;
        this.player = player;
        this.sound = sound;
        this.category = category;
    }

    public void stopSound() {
        player.stopSound(sound, category);
    }

}
