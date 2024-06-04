package me.sakuratao.narrator.spigot.command.sub.developer;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.command.base.CommandInfo;
import me.sakuratao.narrator.spigot.command.base.SubCommand;
import me.sakuratao.narrator.spigot.configuration.lang.LangPlugin;
import me.sakuratao.narrator.spigot.configuration.Permission;
import me.sakuratao.narrator.spigot.utils.server.CCUtil;
import me.sakuratao.narrator.spigot.utils.server.PlayerUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

@CommandInfo(name = "reload", description = "重载章节", permission = Permission.COMMAND_ADMIN_RELOAD, syntax = "/%command% reload", canConsoleUse = true)
public class Reload implements SubCommand {

    @Override
    public void execute(Narrator narrator, CommandSender sender, Command command, String[] args) {
        if (args.length != 1) {
            if (args[1].equals("force")) {
                if (sender instanceof Player p) PlayerUtil.sendMessage(p, CCUtil.translate(LangPlugin.CHAPTERS_LOAD_FORCE));
                narrator.getLogger().log(Level.WARNING, LangPlugin.CHAPTERS_LOAD_FORCE);
                narrator.reloadChapter(true);
                return;
            }
        }
        narrator.reloadChapter(false);
    }

}
