package me.sakuratao.narrator.spigot;

import me.sakuratao.narrator.common.Narrator;
import org.bukkit.plugin.java.JavaPlugin;
import top.jingwenmc.spigotpie.bungee.SpigotPieBungee;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;
import top.jingwenmc.spigotpie.common.lang.PieLang;
import top.jingwenmc.spigotpie.spigot.SpigotPieSpigot;

@PieComponent
public class NarratorSpigot extends JavaPlugin {

    @Wire
    private Narrator narrator;

    @Override
    public void onEnable() {
        this.getLogger().info("Injecting SpigotPie...");
        SpigotPieSpigot.inject(this,"META-INF", "org", "com", "dev", "net", "org");
        narrator.init(this, null);
    }

    @Override
    public void onDisable() {

    }
}
