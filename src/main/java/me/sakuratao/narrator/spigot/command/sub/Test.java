package me.sakuratao.narrator.spigot.command.sub;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.command.base.CommandInfo;
import me.sakuratao.narrator.spigot.command.base.SubCommand;
import me.sakuratao.narrator.spigot.configuration.Permission;
import me.sakuratao.narrator.spigot.data.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandInfo(name = "test", description = "测试", permission = Permission.COMMAND_ADMIN_TEST, syntax = "/%command% test <ChapterOrdinal> <TaskOrdinal> <ContentIndex> <Lang>", canConsoleUse = true)
public class Test implements SubCommand {

    @Override
    public void execute(Narrator narrator, CommandSender sender, Command command, String[] args) {

        if (!(args.length == 0)) {

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
