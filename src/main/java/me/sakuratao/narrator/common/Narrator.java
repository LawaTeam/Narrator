package me.sakuratao.narrator.common;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.sakuratao.narrator.spigot.NarratorSpigot;
import me.sakuratao.narrator.spigot.configuration.Lang;
import me.sakuratao.narrator.spigot.data.cache.CacheData;
import me.sakuratao.narrator.spigot.handlers.HandlerManager;
import me.sakuratao.narrator.spigot.listener.packets.PacketsHandler;
import me.sakuratao.narrator.spigot.manager.ManagerHandler;
import me.sakuratao.narrator.spigot.papi.PapiExpansion;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.scheduler.BukkitTask;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.io.File;
import java.util.logging.Logger;

@Getter
@PieComponent
public class Narrator {

    private final String version = "1.0";

    private boolean isBungee = false;

    private Logger logger;
    private File workFolder = new File("");

    @Getter
    private BukkitAudiences adventure;
    @Getter
    private ProtocolManager protocolManager;
    @Wire
    private PapiExpansion papiExpansion;

    @Wire
    private HandlerManager handlerManager;
    @Wire
    private ManagerHandler managerHandler;
    @Wire
    private CacheData cacheData;
    @Wire
    private PacketsHandler packetsHandler;




    /**
     * 初始化
     */
    public void init(boolean isSpigot, Logger logger, File workFolder) {

        // 判断 Spigot or Bungee
        if (isSpigot) {
            adventure = BukkitAudiences.create(NarratorSpigot.getPluginInstance());
            protocolManager = ProtocolLibrary.getProtocolManager();
            packetsHandler.register(protocolManager);
            papiExpansion.register();
        } else {
            isBungee = true;
        }

        this.logger = logger;
        this.workFolder = workFolder;

        logger.info("                                              ");
        logger.info("    _   __                      __            ");
        logger.info("   / | / /___ _______________ _/ /_____  _____");
        logger.info("  /  |/ / __ `/ ___/ ___/ __ `/ __/ __ \\/ ___/");
        logger.info(" / /|  / /_/ / /  / /  / /_/ / /_/ /_/ / /    ");
        logger.info("/_/ |_/\\__,_/_/  /_/   \\__,_/\\__/\\____/_/    ");
        logger.info("                                              ");
        logger.info("Platform: " + (isSpigot ? "Spigot" : "Bungee") + " | " + "Ver: " + version);

        reloadChapter(false);

    }

    public void close() {
    }

    public void reloadChapter(boolean force){

        managerHandler.getTaskManager().getTasks().values().forEach(BukkitTask::cancel);
        cacheData.cancelAllDelaySingleEvent();

        handlerManager.getChapterHandler().load(force);
        handlerManager.getTaskHandler().load();

        getLogger().info(Lang.CHAPTERS_LOADED);
    }

}
