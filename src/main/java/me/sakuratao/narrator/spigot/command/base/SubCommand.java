package me.sakuratao.narrator.spigot.command.base;

import me.sakuratao.narrator.common.Narrator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface SubCommand {

     void execute(Narrator narrator, CommandSender sender, Command command, String[] args);

}
