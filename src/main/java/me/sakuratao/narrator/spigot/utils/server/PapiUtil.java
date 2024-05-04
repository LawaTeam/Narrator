package me.sakuratao.narrator.spigot.utils.server;

import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

@UtilityClass
public class PapiUtil {

    /**
     * 获取 papi 修改过后的字符串
     * @param player - 玩家
     * @param m - 需要修改的字符
     * @return - 修改后的语句
     */
    public String getString(Player player, String m) {
        return PlaceholderAPI.setPlaceholders(player, m);
    }

}
