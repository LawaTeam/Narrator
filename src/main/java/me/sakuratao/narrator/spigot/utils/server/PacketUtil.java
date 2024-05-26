package me.sakuratao.narrator.spigot.utils.server;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.EventManager;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientAnimation;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public class PacketUtil {

    private final EventManager em = PacketEvents.getAPI().getEventManager();

    public void sendPacket(Player player, PacketWrapper<?> packet){
        PacketEvents.getAPI().getProtocolManager().sendPacket(player, packet);
    }

}
