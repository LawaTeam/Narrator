package me.sakuratao.narrator.spigot.listener;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.data.player.PlayerData;
import me.sakuratao.narrator.spigot.data.cache.CacheData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import top.jingwenmc.spigotpie.common.event.SpigotEventListener;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

@PieComponent
@SpigotEventListener
public class PlayerListener implements Listener {

    @Wire
    private CacheData cacheData;

    @Wire
    private Narrator narrator;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        narrator.getManagerHandler().getPlayerManager().create(p);

        PlayerData playerData = narrator.getManagerHandler().getPlayerManager().getByPlayer(p);
        playerData.setLang(e.getPlayer().getLocale().toLowerCase()); // fixme 可能有问题

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){

        if (narrator.getManagerHandler().getTaskManager().isExists(e.getPlayer().getName())) {
            narrator.getManagerHandler().getTaskManager().killTask(e.getPlayer().getName().toLowerCase());
        }

        narrator.getManagerHandler().getPlayerManager().removeByPlayer(e.getPlayer());

    }

}
