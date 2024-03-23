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
        所有玩家名都应该是 小写 (lowercase)
     */
    private final ConcurrentHashMap<String, PlayerData> playerDataMap = new ConcurrentHashMap<>();

    /**
     * 创建 playerData
     * @param player - 玩家
     */
    public void create(Player player) {
        playerDataMap.putIfAbsent(player.getName().toLowerCase(), new PlayerData(player));
    }

    /**
     * 添加 playerData
     * @param playerData - 玩家数据
     */
    public void add(PlayerData playerData){
        playerDataMap.putIfAbsent(playerData.getPlayerName().toLowerCase(), playerData);
    }

    /**
     * 替换 playerData
     * @param playerData - 玩家数据
     */
    public void replace(PlayerData playerData){
        playerDataMap.replace(playerData.getPlayerName().toLowerCase(), playerData);
    }

    /**
     * 通过 player 删除 playerData
     * @param player - 玩家
     */
    public void removeByPlayer(Player player) {
        playerDataMap.remove(player.getName().toLowerCase());
    }

    /**
     * 通过玩家名删除 playerData
     * @param name - 玩家名
     */
    public void removeByName(String name){
        playerDataMap.remove(name.toLowerCase());
    }

    /**
     * 通过 player 获取 playerData
     * @param player - 玩家
     * @return playerData
     */
    public PlayerData getByPlayer(Player player) {
        return playerDataMap.get(player.getName().toLowerCase());
    }

    /**
     * 通过玩家名获取 playerData
     * @param name - 玩家名
     * @return playerData
     */
    public PlayerData getByName(String name){
        return  playerDataMap.get(name.toLowerCase());
    }

    /**
     * 获取所有玩家数据
     * @return playerDataMap
     */
    public ConcurrentHashMap<String, PlayerData> getPlayerDataMap() {
        return playerDataMap;
    }

}
