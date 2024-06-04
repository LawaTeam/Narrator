package me.sakuratao.narrator.spigot.listener;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.data.cache.CacheData;
import me.sakuratao.narrator.spigot.data.player.PlayerData;
import me.sakuratao.narrator.spigot.utils.server.PlayerUtil;
import me.sakuratao.narrator.spigot.utils.server.TaskUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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

        TaskUtil.taskLaterAsync(() -> {
            PlayerData playerData = narrator.getManagerHandler().getPlayerManager().getByPlayer(p);
            // playerData.setLang(PlayerUtil.getLang(p));
        }, 40);

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){

        if (narrator.getManagerHandler().getTaskManager().isExists(e.getPlayer().getName())) {
            narrator.getManagerHandler().getTaskManager().killTask(e.getPlayer().getName().toLowerCase());
        }

        narrator.getManagerHandler().getPlayerManager().removeByPlayer(e.getPlayer());

    }

    @EventHandler
    public void onInvClickCancel(InventoryClickEvent e){

        Player p = (Player) e.getWhoClicked();
        PlayerData playerData = narrator.getManagerHandler().getPlayerManager().getByPlayer(p);

        if (playerData.isStopped()) {
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void onItemDropCancel(PlayerDropItemEvent e){

        Player p = e.getPlayer();
        PlayerData playerData = narrator.getManagerHandler().getPlayerManager().getByPlayer(p);

        if (playerData.isStopped()) {
            e.setCancelled(true);
        }


    }


}
