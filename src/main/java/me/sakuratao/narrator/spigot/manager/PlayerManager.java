package me.sakuratao.narrator.spigot.manager;

import lombok.Getter;
import me.sakuratao.narrator.spigot.data.Player.PlayerData;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import org.bukkit.entity.Player;
import top.jingwenmc.spigotpie.common.instance.PieComponent;

import java.util.concurrent.ConcurrentHashMap;

@PieComponent
public class PlayerManager {

    /*
        String is lowercase player name.
     */
    private final ConcurrentHashMap<String, PlayerData> playerDataMap = new ConcurrentHashMap<>();

    public void create(Player player) {
        playerDataMap.putIfAbsent(player.getName().toLowerCase(), new PlayerData(player));
    }

    public void add(PlayerData playerData){
        playerDataMap.putIfAbsent(playerData.getPlayerName().toLowerCase(), playerData);
    }

    public void replace(PlayerData playerData){
        playerDataMap.replace(playerData.getPlayerName().toLowerCase(), playerData);
    }

    public void removeByPlayer(Player player) {
        playerDataMap.remove(player.getName().toLowerCase());
    }

    public void removeByName(String name){
        playerDataMap.remove(name.toLowerCase());
    }

    public PlayerData getByPlayer(Player player) {
        return playerDataMap.get(player.getName().toLowerCase());
    }

    public PlayerData getByName(String name){
        return  playerDataMap.get(name.toLowerCase());
    }

    public ConcurrentHashMap<String, PlayerData> getPlayerDataMap() {
        return playerDataMap;
    }

}
