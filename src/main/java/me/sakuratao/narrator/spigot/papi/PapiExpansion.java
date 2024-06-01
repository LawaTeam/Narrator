package me.sakuratao.narrator.spigot.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.data.cache.CacheData;
import me.sakuratao.narrator.spigot.data.player.PlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

@PieComponent
public class PapiExpansion extends PlaceholderExpansion {

    @Wire private Narrator narrator;
    @Wire private CacheData cacheData;

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
        PlayerData playerData = narrator.getManagerHandler().getPlayerManager().getByPlayer(player);
        return switch (params) {
            case "player_chapter_name" -> playerData.getPlayingChapterData().getName();
            case "player_chapter_ordinal" -> String.valueOf(playerData.getPlayingChapterOrdinal());
            case "player_task_name" -> playerData.getPlayingTaskData().getName();
            case "player_task_ordinal" -> String.valueOf(playerData.getPlayingTaskOrdinal());
            case "player_content_index" -> String.valueOf(playerData.getContentIndex());
            case "player_last_option" -> playerData.getLastOption();
            case "player_content" -> playerData.getCurrentContent();
            case "player_lang" -> playerData.getLang();
            default -> "";
        };
    }
}
