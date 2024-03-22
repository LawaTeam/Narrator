package me.sakuratao.narrator.spigot.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.logging.Level;

@UtilityClass
public class ServerUtil {

    public void log(Level level, String log){
        Bukkit.getLogger().log(level, log);
    }

    public void log(Level level, List<String> logs){
        logs.forEach(log -> Bukkit.getLogger().log(level, log));
    }

}
