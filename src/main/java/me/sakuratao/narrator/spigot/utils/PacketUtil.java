package me.sakuratao.narrator.spigot.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public class PacketUtil {

    private final ProtocolManager pm = ProtocolLibrary.getProtocolManager();

    public PacketContainer createPacket(PacketType type) {
        return pm.createPacket(type);
    }


    public void sendPacket(Player player, PacketContainer packet){
        pm.sendServerPacket(player, packet);
    }

}
