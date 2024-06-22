package me.sakuratao.narrator.spigot.utils.server;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@UtilityClass
public class ServerUtil {

    public void sendMessage(CommandSender sender, List<String> messages){
        for (String message : messages) {
            sender.sendMessage(CCUtil.translate("&8| &8*&bNarrator&8* " + message));
        }
    }

    public void sendMessage(CommandSender sender, String message){
        sender.sendMessage(CCUtil.translate("&8| &8*&bNarrator&8* " + message));
    }

    public void sendMessageNoPrefix(CommandSender sender, List<String> messages){
        for (String message : messages) {
            sender.sendMessage(CCUtil.translate(message));
        }
    }

    public void sendMessageNoPrefix(CommandSender sender, String message){
        sender.sendMessage(CCUtil.translate(message));
    }
}
