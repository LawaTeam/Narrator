package me.sakuratao.narrator.spigot.events.content;

import lombok.Getter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.enums.FunctionType;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import me.sakuratao.narrator.spigot.handlers.FunctionHandler;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class PlaySoundEvent extends NarratorEvent {

    private final Narrator narrator;
    private final FunctionHandler fh;

    @Getter private final Player player;
    @Getter private final FunctionType type;
    @Getter private final String target;
    @Getter private final String sound;
    @Getter private final SoundCategory category;
    @Getter private final float volume;
    @Getter private final float pitch;

    public PlaySoundEvent(
            Narrator narrator,
            Player player,
            String target,
            String sound,
            SoundCategory category,
            float volume,
            float pitch
    ) {
        super(true);
        this.narrator = narrator;
        fh = narrator.getHandlerManager().getFunctionHandler();

        this.player = player;
        this.type = fh.getType(target);
        this.target = target;
        this.sound = sound;
        this.category = category;
        this.volume = volume;
        this.pitch = pitch;
    }

    public void playSound(){

        switch (type) {
            case LOC:
                player.playSound(fh.decodeLoc(target), sound, category, volume, pitch);
                break;
            case PLAYER:
                System.out.println(("[Narrator] PlaySoundEvent: " + target + " " + sound + " " + category + " " + volume + " " + pitch));
                player.playSound(fh.decodePlayer(target).getLocation(), sound, category, volume, pitch);
                break;
            case ENTITY:
                // todo
                break;
            case NPC:
                // todo
                break;
        }

    }

}
