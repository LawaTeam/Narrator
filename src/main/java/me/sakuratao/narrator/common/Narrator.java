package me.sakuratao.narrator.common;

import com.comphenix.protocol.ProtocolLibrary;
import lombok.Getter;
import me.sakuratao.narrator.spigot.NarratorSpigot;
import me.sakuratao.narrator.spigot.command.base.CommandManager;
import me.sakuratao.narrator.spigot.data.cache.CacheData;
import me.sakuratao.narrator.spigot.handlers.HandlerManager;
import me.sakuratao.narrator.spigot.listener.packets.PacketsHandler;
import me.sakuratao.narrator.spigot.manager.ManagerHandler;
import me.sakuratao.narrator.spigot.papi.PapiExpansion;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.io.File;
import java.util.logging.Logger;

@Getter
@PieComponent
public class Narrator {

    private final String version = "1.0";

    private Logger logger;
    private File workFolder = new File("");

    @Getter private BukkitAudiences adventure;
    @Wire private PapiExpansion papiExpansion;

    @Wire
    private HandlerManager handlerManager;
    @Wire
    private ManagerHandler managerHandler;
    @Wire
    private CacheData cacheData;
    @Wire
    private PacketsHandler packetsHandler;
    @Wire
    private CommandManager commandManager;

    /**
     * 初始化
     */
    public void init(Logger logger, File workFolder) {

        adventure = BukkitAudiences.create(NarratorSpigot.getPluginInstance());
        papiExpansion.register();
        packetsHandler.register();

        Bukkit.getPluginCommand("narrator").setExecutor(commandManager);

        this.logger = logger;
        this.workFolder = workFolder;

        logger.info("                                              ");
        logger.info("    _   __                      __            ");
        logger.info("   / | / /___ _______________ _/ /_____  _____");
        logger.info("  /  |/ / __ `/ ___/ ___/ __ `/ __/ __ \\/ ___/");
        logger.info(" / /|  / /_/ / /  / /  / /_/ / /_/ /_/ / /    ");
        logger.info("/_/ |_/\\__,_/_/  /_/   \\__,_/\\__/\\____/_/    ");
        logger.info("                                              ");
        logger.info("Platform: Spigot | " + "Ver: " + version);

        handlerManager.getChapterHandler().initLoad(false);

    }

    public void close() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(NarratorSpigot.getPluginInstance());
    }

}
