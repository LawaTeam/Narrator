package me.sakuratao.narrator.spigot.command.base;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.configuration.lang.LangPlugin;
import me.sakuratao.narrator.spigot.utils.server.CCUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface SubCommand {

     void execute(Narrator narrator, CommandSender sender, Command command, String[] args);

}
