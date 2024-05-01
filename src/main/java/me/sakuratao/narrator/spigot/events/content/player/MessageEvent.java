package me.sakuratao.narrator.spigot.events.content.player;

import lombok.Getter;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import me.sakuratao.narrator.spigot.utils.CCUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class MessageEvent extends NarratorEvent implements Cancellable {

    private boolean isCancelled = false;

    @Getter private final Player player;
    @Getter private final String message;

    public MessageEvent(Player player, String message){
        super(true);
        this.player = player;
        this.message = message;
    }

    public void sendMessage(){
        player.sendMessage(CCUtil.translate(message));
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}
