package me.sakuratao.narrator.spigot.listener.packets;

import com.comphenix.protocol.ProtocolManager;
import me.sakuratao.narrator.common.Narrator;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

@PieComponent
public class PacketsHandler {

    @Wire
    private Narrator narrator;

    @Wire
    private OptionListener optionListener;

    public void register(ProtocolManager pm){
        pm.addPacketListener(optionListener);
    }

}
