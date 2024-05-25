package me.sakuratao.narrator.spigot.command.sub;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.command.base.CommandInfo;
import me.sakuratao.narrator.spigot.command.base.SubCommand;
import me.sakuratao.narrator.spigot.configuration.Lang;
import me.sakuratao.narrator.spigot.configuration.Permission;
import me.sakuratao.narrator.spigot.data.cache.CacheData;
import me.sakuratao.narrator.spigot.utils.server.CCUtil;
import me.sakuratao.narrator.spigot.utils.server.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.jingwenmc.spigotpie.common.instance.PieComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@CommandInfo(name = "debug", description = "开启 debug", permission = Permission.COMMAND_ADMIN_DEBUG, syntax = "/%command% debug <Player> <Chapter> <Task> <contentType> [contentDetails...]", canConsoleUse = true)
public class Debug implements SubCommand {

    @Override
    public void execute(Narrator narrator, CommandSender sender, Command command, String[] args) {

        if (!(sender instanceof Player listener)) {
            sender.sendMessage(CCUtil.translate(Lang.COMMAND_PLAYER_ONLY));
            return;
        }

        ConcurrentHashMap<Player, String> debugListeners = narrator.getHandlerManager().getDebugHandler().getDebugListeners();

        if (args[1].equalsIgnoreCase("clear")) {
            debugListeners.remove(listener);
            PlayerUtil.sendMessage(listener, Lang.COMMAND_DEBUG_CLEAR);
            return;
        }

        if (args.length < 5) {
            PlayerUtil.sendMessage(listener, Lang.COMMAND_SYNTAX_ERROR);
            return;
        }

        Player listenedPlayer = Bukkit.getPlayerExact(args[1]);
        if (listenedPlayer == null) {
            PlayerUtil.sendMessage(listener, Lang.COMMAND_PLAYER_NULL);
            return;
        }

        String listenedChapter = args[2];
        String listenedTask = args[3];
        String listenedContentType = args[4];
        List<String> listenedContentDetailsList = Collections.singletonList("all");
        if (args.length >= 6) {
            listenedContentDetailsList = Arrays.asList(args).subList(5, args.length);
        }

        String listenedContentDetails  = String.join("::", listenedContentDetailsList);

        debugListeners.put((Player) sender,  listenedChapter + ";" + listenedTask + ";" + listenedPlayer.getName() + ";" + listenedContentType + ";" + listenedContentDetails);
        List<String> replacedDetails = new ArrayList<>();
        for (String detail : Lang.COMMAND_DEBUG_DETAIL) {
            if (detail.contains("%contentDetails%")) {

                if (listenedContentDetails.equalsIgnoreCase("all")) {
                    replacedDetails.add(detail.replace("%contentDetails%", "all"));
                    continue;
                }

                for (String contentDetail : listenedContentDetailsList) {
                    replacedDetails.add(detail.replace("%contentDetails%", contentDetail));
                }
                continue;
            }
            replacedDetails.add(detail
                    .replace("%chapter%", listenedChapter)
                    .replace("%task%", listenedTask)
                    .replace("%player%", listenedPlayer.getName())
                    .replace("%content%", listenedContentType)
            );
        }
        PlayerUtil.sendMessage(listener, replacedDetails);
    }
}
