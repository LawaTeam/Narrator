package me.sakuratao.narrator.spigot.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.data.player.PlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

@PieComponent
public class PapiExpansion extends PlaceholderExpansion {

    @Wire
    private Narrator narrator;

    @Override
    public @NotNull String getIdentifier() {
        return "narr";
    }

    @Override
    public @NotNull String getAuthor() {
        return "SakuraTao";
    }

    @Override
    public @NotNull String getVersion() {
        return narrator.getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        //todo papi custom
        PlayerData playerData = narrator.getManagerHandler().getPlayerManager().getByPlayer(player);
        return switch (params) {
            case "chapter_name" -> playerData.getPlayingChapterData().getName();
            case "chapter_ordinal" -> String.valueOf(playerData.getPlayingChapterOrdinal());
            case "task_name" -> playerData.getPlayingTaskData().getName();
            case "task_ordinal" -> String.valueOf(playerData.getPlayingTaskOrdinal());
            case "content_index" -> String.valueOf(playerData.getContentIndex());
            case "content" -> playerData.getCurrentContent();
            case "lang" -> playerData.getLang();
            default -> "";
        };
    }
}
