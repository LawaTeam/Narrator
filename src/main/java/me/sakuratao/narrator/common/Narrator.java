package me.sakuratao.narrator.common;

import lombok.Getter;
import me.sakuratao.narrator.common.handlers.HandlerManager;
import me.sakuratao.narrator.spigot.data.cache.CacheData;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.md_5.bungee.api.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import top.jingwenmc.spigotpie.bungee.SpigotPieBungee;
import top.jingwenmc.spigotpie.common.instance.ObjectManager;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;
import top.jingwenmc.spigotpie.spigot.SpigotPieSpigot;

import java.io.File;
import java.util.logging.Logger;

@Getter
@PieComponent
public class Narrator {

    private final String version = "1.0";

    private JavaPlugin spigot = null;
    private Plugin bungee = null;
    private boolean isBungee = false;

    private Logger logger;
    private File workFolder = new File("");

    @Getter
    BukkitAudiences adventure;
    @Wire
    private HandlerManager handlerManager;
    @Wire
    private CacheData cacheData;

    /**
     * 初始化
     * 用哪个端填哪个，不然就填 null
     * @param spigot - spigot
     * @param bungee - bungee
     */
    public void init(JavaPlugin spigot, Plugin bungee) {

        // 判断 Spigot or Bungee
        if (spigot != null) {
            this.spigot = spigot;
            workFolder = spigot.getDataFolder().getParentFile();
            logger = spigot.getLogger();
            adventure = BukkitAudiences.create(spigot);
        } else if (bungee != null) {
            this.bungee = bungee;
            isBungee = true;
            workFolder = bungee.getDataFolder().getParentFile();
            logger = bungee.getLogger();
        } else {
            throw new RuntimeException("Plugin can not been init!");
        }

        logger.info("                                              ");
        logger.info("    _   __                      __            ");
        logger.info("   / | / /___ _______________ _/ /_____  _____");
        logger.info("  /  |/ / __ `/ ___/ ___/ __ `/ __/ __ \\/ ___/");
        logger.info(" / /|  / /_/ / /  / /  / /_/ / /_/ /_/ / /    ");
        logger.info("/_/ |_/\\__,_/_/  /_/   \\__,_/\\__/\\____/_/    ");
        logger.info("                                              ");
        logger.info("Platform: " + (spigot != null ? "Spigot" : "Bungee") + " | " + "Ver: " + version);

        handlerManager.getChapterHandler().load();
        handlerManager.getTaskHandler().load();



    }

    public void close() {

    }

}
