package me.sakuratao.narrator.spigot.command;

import me.sakuratao.narrator.common.Narrator;
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
            if (item.getArgs()[0].equalsIgnoreCase("force")) {
                narrator.getLogger().log(Level.WARNING, "Confirm execute force reloading...");
                narrator.reloadChapter(true, true);
                return;
            }
        }
        narrator.reloadChapter(true, false);

    }


}
