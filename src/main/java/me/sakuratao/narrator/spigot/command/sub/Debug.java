package me.sakuratao.narrator.spigot.command.sub;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.command.base.CommandInfo;
import me.sakuratao.narrator.spigot.command.base.SubCommand;
import me.sakuratao.narrator.spigot.configuration.Permission;
import me.sakuratao.narrator.spigot.data.cache.CacheData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.jingwenmc.spigotpie.common.instance.PieComponent;

@CommandInfo(name = "debug", description = "开启 debug", permission = Permission.COMMAND_ADMIN_DEBUG, syntax = "/%command% debug <Player> <Chapter> <Task> <contentType>", canConsoleUse = true)
public class Debug implements SubCommand {

    private final Narrator narrator;

    public Debug(Narrator narrator) {
        this.narrator = narrator;
    }

    @Override
    public void execute(Narrator narrator, CommandSender sender, Command command, String[] args) {
        CacheData cacheData = narrator.getCacheData();

        if (args[1].equalsIgnoreCase("clear")) {
            cacheData.getDebugListeners().remove((Player) sender);
            return;
        }

        if (args.length != 5) return;

        Player listenedPlayer = Bukkit.getPlayerExact(args[1]);
        if (listenedPlayer == null) return;

        String listenedChapter = args[2];
        String listenedTask = args[3];
        String listenedContentType = args[4];

        cacheData.getDebugListeners().put((Player) sender,  listenedChapter + ";" + listenedTask + ";" + listenedPlayer.getName() + ";" + listenedContentType);

    }
}
