package me.sakuratao.narrator.spigot.command.base;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.configuration.lang.LangPlugin;
import me.sakuratao.narrator.spigot.utils.server.CCUtil;
import me.sakuratao.narrator.spigot.utils.server.ServerUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface SubCommand {

     void execute(Narrator narrator, CommandSender sender, Command command, String[] args);


     default boolean handleInvalidPageNumber(CommandSender sender, int pageNumber, int maxPages) {
          if (pageNumber <= 0) {
               ServerUtil.sendMessage(sender, LangPlugin.COMMAND_LESS_THAN_MAX_PAGES);
               return true;
          } else if (pageNumber > maxPages){
               ServerUtil.sendMessage(sender, LangPlugin.COMMAND_MORE_THAN_MAX_PAGES);
               return true;
          }
          return false;
     }

}
