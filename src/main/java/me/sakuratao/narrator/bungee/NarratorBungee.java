package me.sakuratao.narrator.bungee;

import me.sakuratao.narrator.common.Narrator;
import net.md_5.bungee.api.plugin.Plugin;
import top.jingwenmc.spigotpie.bungee.SpigotPieBungee;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

@PieComponent
public class NarratorBungee extends Plugin {

    @Wire
    private Narrator narrator;

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        this.getLogger().info("Injecting SpigotPie...");
        SpigotPieBungee.inject(this,"META-INF", "org", "com", "dev", "net", "org");
        narrator.init(null, this);
    }

    @Override
    public void onDisable() {

    }
}
