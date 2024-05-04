package me.sakuratao.narrator.spigot.utils.server;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

@UtilityClass
public class EventUtil {

    public void callEvent(Event event){
        Bukkit.getPluginManager().callEvent(event);
    }

}
