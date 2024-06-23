package me.sakuratao.narrator.spigot.command.sub.developer;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.command.base.CommandInfo;
import me.sakuratao.narrator.spigot.command.base.SubCommand;
import me.sakuratao.narrator.spigot.configuration.Permission;
import me.sakuratao.narrator.spigot.configuration.lang.LangPlugin;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.utils.StringUtil;
import me.sakuratao.narrator.spigot.utils.server.ServerUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@CommandInfo(name = "chapter", description = "与剧情相关的指令集", permission = Permission.ADMIN, syntax = "/%command% chapter", canConsoleUse = true)
public class Chapter implements SubCommand {

    @Override
    public void execute(Narrator narrator, CommandSender sender, Command command, String[] args) {

        if (args.length == 1) {
            sendChapterHelp(sender);
            return;
        }

        if (args.length > 1) {
            switch (args[1]) {
                case "list":
                    // 简化了条件判断逻辑，合并处理发送列表的调用
                    try {

                        if (args.length == 2) {
                            sendChapterLangList(narrator, sender);
                            return;
                        }

                        // 尝试解析页码，如果args长度小于4，则默认页码为0
                        int page = (args.length >= 4) ? Integer.parseInt(args[3]) : 1;
                        // 添加对page的范围验证，假设页码不能为负
                        if (page < 0) {
                            ServerUtil.sendMessage(sender, LangPlugin.COMMAND_LESS_THAN_MAX_PAGES);
                            return;
                        }

                        sendChapterList(narrator, sender, args[2], page);
                    } catch (NumberFormatException e) {
                        // 当args[3]不是有效的整数时，发送错误提示
                        ServerUtil.sendMessage(sender, LangPlugin.COMMAND_NOT_NUMBER);
                    }
                    break;
                default:
                    ServerUtil.sendMessage(sender, LangPlugin.USE_HELP_PLEASE);
                    break;
            }
        }

    }


    private void sendChapterHelp(CommandSender sender) {
        ServerUtil.sendMessage(sender, LangPlugin.COMMAND_CHAPTER_HELP);
    }

    private void sendChapterLangList(Narrator narrator, CommandSender sender) {
        ServerUtil.sendMessage(sender,  " &f◆ &8| &fChapter 指令集 &8[&7List&8]");
        ServerUtil.sendMessage(sender,  " &f◆ &8| &b请选择需要的语言: ");
        ServerUtil.sendMessage(sender,  " &f◆ &8|  &7  |- &fall");
        for (String lang : narrator.getHandlerManager().getChapterHandler().getTotalLang()) {
            ServerUtil.sendMessage(sender,  " &f◆ &8|  &7  |- &f" + lang);
        }
    }

    private void sendChapterList(Narrator narrator, CommandSender sender, String lang, int pageNumber){

        if (!lang.equalsIgnoreCase("all") && !narrator.getHandlerManager().getChapterHandler().getTotalLang().contains(lang)) {
            ServerUtil.sendMessage(sender, LangPlugin.COMMAND_CHAPTER_NON_LANG);
            return;
        }

        ServerUtil.sendMessage(sender,  " &f◆ &8| &fChapter 指令集 &8[&7List&8-&f" + lang + "&8]");
        List<String> tempList = new ArrayList<>();

        for (ChapterData data : narrator.getHandlerManager().getChapterHandler().getChapterListByLang(lang).stream().sorted(Comparator.comparing(ChapterData::getName)).toList()) {
            tempList.add(" &f◆ &8| &b" + data.getName() + " &8- &f" + data.getAuthor());
            tempList.add("          &8|- &fOrdinal: " + data.getOrdinal() + "&8- &fVer: " + data.getVersion());
        }
        List<List<String>> messageList = StringUtil.paginateStrings(tempList, 8);

        if (!handleInvalidPageNumber(sender, pageNumber, messageList.size())) {
            ServerUtil.sendMessageNoPrefix(sender, "&8&m---»--*---------------&7/ &f页数: " + pageNumber + "/" + messageList.size() + " &7/&8&m--------------*--«---");
            for (String message : messageList.get(pageNumber - 1)){
                ServerUtil.sendMessage(sender, message);
            }
            ServerUtil.sendMessageNoPrefix(sender, "&8&m---»--*---------------&7/ &f页数: " + pageNumber + "/" + messageList.size() + " &7/&8&m--------------*--«---");
        }

    }

}
