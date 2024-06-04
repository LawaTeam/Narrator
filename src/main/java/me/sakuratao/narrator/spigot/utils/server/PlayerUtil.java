package me.sakuratao.narrator.spigot.utils.server;

import lombok.experimental.UtilityClass;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.NarratorSpigot;
import me.sakuratao.narrator.spigot.utils.LogUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;

@UtilityClass
public class PlayerUtil {

    public String getLang(Player player) {
        try {

            String state = PlayerUtil.getIpState(player);
            return switch (state) {
                // 简体中文
                case "CN" -> "zh_CN";
                // 繁体中文
                case "HK" -> "zh_HK";
                case "TW" -> "zh_TW";
                // 英文(美式)
                case "AU", "BZ", "CA", "CB", "IE", "JM", "NZ", "PH", "ZA", "TT", "GB", "US", "ZW" -> "en_US";
                // 日语
                case "JP" -> "ja_JP";
                // 韩语
                case "KR" -> "ko_KR";
                // 默认
                default -> "en_US";
            };

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "en_US";
    }

    public String getIpState(Player player) throws IOException {
        String ip = player.getAddress().getHostString();
        HttpURLConnection urlCon = (HttpURLConnection)new URL("https://ip2c.org/" + ip).openConnection();
        urlCon.setDefaultUseCaches(false);
        urlCon.setUseCaches(false);
        urlCon.connect();
        InputStream is = urlCon.getInputStream();
        int c = 0;
        StringBuilder s = new StringBuilder();
        while((c = is.read()) != -1) s.append((char) c);
        is.close();
        switch(s.charAt(0))
        {
            case '0':
                LogUtil.log(Level.SEVERE, "Something wrong when get " + player.getName() + "'s IP State");
                break;
            case '1':
                String[] reply = s.toString().split(";");
                return reply[1];
            case '2':
                LogUtil.log(Level.SEVERE, "Can't found " + player.getName() + "'s IP in ip2c database");
                break;
        }
        return "US";
    }

    public void sendMessage(Player p, List<String> messages){
        for (String message : messages) {
            p.sendMessage(CCUtil.translate("&8| &8*&bNarrator&8* " + message));
        }
    }

    public void sendMessage(Player p, String message){
        p.sendMessage(CCUtil.translate("&8| &8*&bNarrator&8* " + message));
    }

    public void sendActionBar(Narrator narrator, Player p, String message) {
        Audience audience = narrator.getAdventure().player(p);
        audience.sendActionBar(Component.text(CCUtil.translate(message)));
    }

}
