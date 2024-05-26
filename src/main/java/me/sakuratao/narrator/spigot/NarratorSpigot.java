package me.sakuratao.narrator.spigot;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
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
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        //Are all listeners read only?
        PacketEvents.getAPI().getSettings().reEncodeByDefault(false)
                .checkForUpdates(false)
                .bStats(false);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        this.getLogger().info("Injecting SpigotPie...");
        pluginInstance = this;
        SpigotPieSpigot.inject(this,"META-INF", "org", "com", "dev", "net", "org","me.sakuratao.narrator.bungee");
        narrator.init(true, getLogger(), getDataFolder());
    }

    @Override
    public void onDisable() {
        narrator.close();
    }
}
