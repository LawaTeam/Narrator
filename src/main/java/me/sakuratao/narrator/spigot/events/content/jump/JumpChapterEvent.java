package me.sakuratao.narrator.spigot.events.content.jump;

import lombok.Getter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.data.player.PlayerData;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import org.bukkit.entity.Player;

public class JumpChapterEvent extends NarratorEvent {

    private final Narrator narrator;

    @Getter private final Player player;
    @Getter private final PlayerData playerData;
    @Getter private final ChapterData currentData;
    @Getter private final int targetDataOrdinal;
    @Getter private final int taskOrdinal;
    @Getter private final int contentIndex;
    @Getter private final String jumpContent;

    public JumpChapterEvent(
            Narrator narrator,
            Player player,
            PlayerData playerData,
            ChapterData currentData,
            int targetDataOrdinal,
            int taskOrdinal,
            int contentIndex,
            String jumpContent
    ){
        super(true);
        this.narrator = narrator;
        this.player = player;
        this.playerData = playerData;
        this.currentData = currentData;
        this.targetDataOrdinal = targetDataOrdinal;
        this.taskOrdinal = taskOrdinal;
        this.contentIndex = contentIndex;
        this.jumpContent = jumpContent;
    }

    public boolean jumpChapter(){
        return narrator.getHandlerManager().getChapterHandler().jump(playerData, targetDataOrdinal, taskOrdinal, contentIndex - 1, jumpContent ); // 此处 -1 为抵消 contentTask 中的加一
    }

}
