package me.sakuratao.narrator.spigot.handlers;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;
import top.jingwenmc.spigotpie.common.instance.PieComponent;

@PieComponent
public class HologramHandler {

    public Hologram createHologram(String message, Location loc){
        return DHAPI.createHologram(message, loc);
    }

}
