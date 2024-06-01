package me.sakuratao.narrator.spigot.listener.packets;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
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
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        pm.addPacketListener(optionListener);
    }

}
