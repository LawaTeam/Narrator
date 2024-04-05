package me.sakuratao.narrator.spigot.command;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.configuration.Lang;
import me.sakuratao.narrator.spigot.data.player.PlayerData;
import org.bukkit.Bukkit;
import top.jingwenmc.spigotpie.common.command.CommandItem;
import top.jingwenmc.spigotpie.common.command.CommandSender;
import top.jingwenmc.spigotpie.common.command.PieCommand;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Platform;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.util.logging.Level;

@PieComponent(platform = Platform.SPIGOT)
public class NarratorCommand {

    @Wire
    private Narrator narrator;

    @PieCommand(value = "narrator help", aliases = {"nh"}, permission = "narrator.command.narrator", bungeeCord = false, spigot = true)
    public void onHelp(CommandItem item){

        CommandSender sender = item.getSender();
        String[] args = item.getArgs();

    }

    @PieCommand(value = "narrator reload", aliases = {"nr"}, permission = "narrator.command.reload", bungeeCord = false, spigot = true)
    public void onReload(CommandItem item){

        CommandSender sender = item.getSender();
        if (!item.isSingle()) {
            if (item.getArgs()[0].equals("force")) {
                narrator.getLogger().log(Level.WARNING, Lang.CHAPTERS_LOAD_FORCE);
                narrator.reloadChapter(true, true);
                return;
            }
        }
        narrator.reloadChapter(true, false);

    }

    @PieCommand(value = "narrator test", aliases = {"nt"}, permission = "narrator.command.admin", bungeeCord = false, spigot = true)
    public void onTest(CommandItem item){

        CommandSender sender = item.getSender();
        String[] args = item.getArgs();
        if (!item.isSingle()) {

            int chapterOrdinal = Integer.parseInt(args[1]);
            int taskOrdinal = Integer.parseInt(args[2]);
            int contentIndex = Integer.parseInt(args[3]);

            String lang = args[4].toLowerCase();

            if (chapterOrdinal < 1) {
                chapterOrdinal = 1;
            }
            if (taskOrdinal < 1) {
                taskOrdinal = 1;
            }
            if (contentIndex < 0) {
                contentIndex = 0;
            }

            if (!narrator.getHandlerManager().getChapterHandler().isLangExists(lang)){
                lang = "zh_cn";
            }

            PlayerData playerData = narrator.getManagerHandler().getPlayerManager().getByPlayer(Bukkit.getPlayer(sender.getName()));
            playerData.setPlayingChapterOrdinal(chapterOrdinal);
            playerData.setPlayingTaskOrdinal(taskOrdinal);
            playerData.setContentIndex(contentIndex);
            playerData.setLang(lang);

            narrator.getManagerHandler().getTaskManager().createTask(playerData);

        }

    }

}
