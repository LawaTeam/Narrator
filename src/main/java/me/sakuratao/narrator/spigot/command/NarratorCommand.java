package me.sakuratao.narrator.spigot.command;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.data.Player.PlayerData;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import top.jingwenmc.spigotpie.common.command.CommandItem;
import top.jingwenmc.spigotpie.common.command.CommandSender;
import top.jingwenmc.spigotpie.common.command.NotRequiredCommandParam;
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

    @PieCommand(value = "narrator chapter", aliases = {"nc"}, permission = "narrator.command.chapter", bungeeCord = false, spigot = true)
    public void onChapter(CommandItem item){

        CommandSender sender = item.getSender();
        String[] args = item.getArgs();

    }

    @PieCommand(value = "narrator reload", aliases = {"nr"}, permission = "narrator.command.reload", bungeeCord = false, spigot = true)
    public void onReload(CommandItem item){

        CommandSender sender = item.getSender();
        if (!item.isSingle()) {
            switch (item.getArgs()[0]){
                case "force": {
                    narrator.getLogger().log(Level.WARNING, "Confirm execute force reloading...");
                    narrator.reloadChapter(true, true);
                    return;
                }
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

            if (chapterOrdinal < 1) {
                chapterOrdinal = 1;
            }
            if (taskOrdinal < 1) {
                taskOrdinal = 1;
            }
            if (contentIndex < 0) {
                contentIndex = 0;
            }

            PlayerData playerData = narrator.getManagerHandler().getPlayerManager().getByPlayer(Bukkit.getPlayer(sender.getName()));
            playerData.setPlayingChapterOrdinal(chapterOrdinal);
            playerData.setPlayingTaskOrdinal(taskOrdinal);
            playerData.setContentIndex(contentIndex);

            narrator.getManagerHandler().getTaskManager().createTask(playerData);

        }

    }

}
