package me.sakuratao.narrator.spigot.utils;

import lombok.experimental.UtilityClass;
import me.sakuratao.narrator.spigot.configuration.Lang;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

@UtilityClass
public class LogUtil {

    public void log(Level level, String log){
        Bukkit.getLogger().log(level, log);
    }

    public void log(Level level, List<String> logs){
        logs.forEach(log -> Bukkit.getLogger().log(level, log));
    }

}
