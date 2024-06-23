package me.sakuratao.narrator.spigot;

import lombok.Getter;
import me.sakuratao.narrator.common.Narrator;
import org.bukkit.plugin.java.JavaPlugin;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Platform;
import top.jingwenmc.spigotpie.common.instance.Wire;
import top.jingwenmc.spigotpie.spigot.SpigotPieSpigot;

@PieComponent(platform = Platform.SPIGOT)
public class NarratorSpigot extends JavaPlugin {

    @Wire
    private Narrator narrator;

    @Getter
    private static JavaPlugin pluginInstance;

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        this.getLogger().info("Injecting SpigotPie...");
        pluginInstance = this;
        SpigotPieSpigot.inject(this,"META-INF", "org", "com", "dev", "net", "org","me.sakuratao.narrator.bungee");
        narrator.init(getLogger(), getDataFolder());
    }

    @Override
    public void onDisable() {
        narrator.close();
    }
}
