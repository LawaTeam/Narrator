package me.sakuratao.narrator.spigot.events.content.player;

import lombok.Getter;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import me.sakuratao.narrator.spigot.utils.server.CCUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.util.List;

public class TitleEvent extends NarratorEvent implements Cancellable {

    private boolean isCancelled = false;

    @Getter private final String title;
    @Getter private final String subTitle;
    @Getter private final int fadeIn;
    @Getter private final int keep;
    @Getter private final int fadeOut;
    @Getter private final Player player;

    public TitleEvent(List<String> contentList, Player player) {
        super(true);
        this.player = player;
        this.title = CCUtil.translate(contentList.get(4));
        this.subTitle = CCUtil.translate(contentList.get(5));
        this.fadeIn = Integer.parseInt(contentList.get(1));
        this.keep = Integer.parseInt(contentList.get(2));
        this.fadeOut = Integer.parseInt(contentList.get(3));
    }

    /**
     * 给玩家发送 title
     */
    public void showTitle() {
        player.sendTitle(
                title, subTitle,
                fadeIn, keep, fadeOut
        );
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

}
