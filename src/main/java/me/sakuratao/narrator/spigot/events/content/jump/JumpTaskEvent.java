package me.sakuratao.narrator.spigot.events.content.jump;

import lombok.Getter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.configuration.Lang;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.data.player.PlayerData;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import me.sakuratao.narrator.spigot.utils.LogUtil;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Level;

public class JumpTaskEvent extends NarratorEvent {

    private final Narrator narrator;

    @Getter private final Player player;
    @Getter private final PlayerData playerData;
    @Getter private final ChapterData currentData;
    @Getter private final int taskOrdinal;
    @Getter private final int contentIndex;
    @Getter private final String jumpContent;

    public JumpTaskEvent(Narrator narrator, Player player, PlayerData playerData, ChapterData currentData, int taskOrdinal, int contentIndex, String jumpContent) {
        this.narrator = narrator;
        this.player = player;
        this.playerData = playerData;
        this.currentData = currentData;
        this.taskOrdinal = taskOrdinal;
        this.contentIndex = contentIndex;
        this.jumpContent = jumpContent;
    }

    public boolean jumpTask() {
        if (taskOrdinal < 1 || contentIndex < 0) {
            LogUtil.log(Level.SEVERE, Lang.CHAPTERS_EXECUTE_NUMBER_FORMAT);
            LogUtil.log(Level.SEVERE, Lang.CHAPTERS_CONSOLE_HELP);
            narrator.getLogger().log(Level.SEVERE, "        Chapter Name: " + currentData.getName());
            narrator.getLogger().log(Level.SEVERE, "        Content: " + jumpContent);
            return false;
        }
        narrator.getHandlerManager().getTaskHandler().jump(
                playerData,
                currentData,
                taskOrdinal,
                contentIndex
        );
        return true;
    }

}
