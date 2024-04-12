package me.sakuratao.narrator.spigot.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.sakuratao.narrator.common.Narrator;
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
        return "na";
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
        return "";
    }
}
