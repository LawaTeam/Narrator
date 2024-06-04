package me.sakuratao.narrator.spigot.command.sub.developer;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.command.base.CommandInfo;
import me.sakuratao.narrator.spigot.command.base.SubCommand;
import me.sakuratao.narrator.spigot.configuration.lang.LangPlugin;
import me.sakuratao.narrator.spigot.configuration.Permission;
import me.sakuratao.narrator.spigot.data.player.PlayerData;
import me.sakuratao.narrator.spigot.utils.server.CCUtil;
import me.sakuratao.narrator.spigot.utils.server.PlayerUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(name = "test", description = "测试", permission = Permission.COMMAND_TEST, syntax = "/%command% test <ChapterOrdinal> <TaskOrdinal> <ContentIndex> <Lang>", canConsoleUse = false)
public class Test implements SubCommand {

    @Override
    public void execute(Narrator narrator, CommandSender sender, Command command, String[] args) {

        Player tester = (Player) sender;

        if (args.length != 5) {
            sender.sendMessage(CCUtil.translate(LangPlugin.COMMAND_SYNTAX_ERROR));
            return;
        }

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
        
        PlayerData playerData = narrator.getManagerHandler().getPlayerManager().getByPlayer(tester);
        playerData.setPlayingChapterOrdinal(chapterOrdinal);
        playerData.setPlayingTaskOrdinal(taskOrdinal);
        playerData.setContentIndex(contentIndex);
        playerData.setLang(lang);

        narrator.getManagerHandler().getTaskManager().createTask(playerData);
        List<String> replacedDetails = getReplacedDetails(playerData);

        PlayerUtil.sendMessage(tester, replacedDetails);

    }

    private static @NotNull List<String> getReplacedDetails(PlayerData playerData) {
        List<String> replacedDetails = new ArrayList<>();

        for (String detail : LangPlugin.COMMAND_TEST_DETAIL) {
            replacedDetails.add(detail
                    .replace("%chapter%", playerData.getPlayingChapterData().getName())
                    .replace("%chapterOrdinal%", playerData.getPlayingChapterData().getOrdinal() + "")
                    .replace("%chapterLang%", playerData.getPlayingChapterData().getLang())
                    .replace("%task%", playerData.getPlayingTaskData().getName())
                    .replace("%taskOrdinal%", playerData.getPlayingTaskData().getOrdinal() + "")
                    .replace("%contentIndex%", playerData.getContentIndex() + ""));
        }
        return replacedDetails;
    }
}
