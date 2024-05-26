package me.sakuratao.narrator.spigot.listener.packets;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.EventManager;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import me.sakuratao.narrator.common.Narrator;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.sql.Time;

@PieComponent
public class PacketsHandler {

    @Wire
    private Narrator narrator;

    @Wire
    private OptionListener optionListener;

    public void register(){
        EventManager em = PacketEvents.getAPI().getEventManager();
        em.registerListener(optionListener, PacketListenerPriority.NORMAL);
    }

}
