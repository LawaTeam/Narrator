package me.sakuratao.narrator.spigot.command.sub;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.command.base.CommandInfo;
import me.sakuratao.narrator.spigot.command.base.SubCommand;
import me.sakuratao.narrator.spigot.configuration.Lang;
import me.sakuratao.narrator.spigot.configuration.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;

@CommandInfo(name = "reload", description = "重载章节", permission = Permission.COMMAND_ADMIN_RELOAD, syntax = "/%command% reload", canConsoleUse = true)
public class Reload implements SubCommand {

    @Override
    public void execute(Narrator narrator, CommandSender sender, Command command, String[] args) {
        if (args.length != 0) {
            if (args[1].equals("force")) {
                narrator.getLogger().log(Level.WARNING, Lang.CHAPTERS_LOAD_FORCE);
                narrator.reloadChapter(true);
                return;
            }
        }
        narrator.reloadChapter(false);
    }

}
