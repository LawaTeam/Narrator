package me.sakuratao.narrator.spigot.listener;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.data.Player.PlayerData;
import me.sakuratao.narrator.spigot.data.cache.CacheData;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
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
        narrator.getManagerHandler().getPlayerManager().create(e.getPlayer());
    }

    @EventHandler
    public void onPlayerSwingToChoose(PlayerAnimationEvent e){

        if (cacheData.getContentIndex().containsKey(e.getPlayer().getName().toLowerCase())) {
            if (e.getAnimationType().equals(PlayerAnimationType.ARM_SWING)) {
                cacheData.getContentIndex().replace(
                        e.getPlayer().getName().toLowerCase(),
                        cacheData.getContentIndex().get(e.getPlayer().getName().toLowerCase()) + 1
                );
            }
        }

    }

    @EventHandler
    public void onPlayerDropToConfirm(PlayerDropItemEvent e){
        cacheData.getContentIndex().replace(e.getPlayer().getName().toLowerCase(), -1);
    }


}
