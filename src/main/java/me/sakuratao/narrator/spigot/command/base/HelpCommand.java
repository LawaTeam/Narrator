package me.sakuratao.narrator.spigot.command.base;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.configuration.Lang;
import me.sakuratao.narrator.spigot.configuration.Permission;
import me.sakuratao.narrator.spigot.utils.server.CCUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@CommandInfo(name = "help", description = "查看帮助列表", permission = Permission.HELP, syntax = "/%command% help <Pages>", canConsoleUse = true)
public class HelpCommand implements SubCommand {

    @Override
    public void execute(Narrator narrator, CommandSender sender, Command command, String[] args) {
        // 检查参数长度
        if (args.length > 2) {
            sender.sendMessage(CCUtil.translate(Lang.COMMAND_HELP_PAGE));
            return;
        }

        // 若发送者是玩家，发送空消息作为分隔
        if (sender instanceof Player) {
            sendEmptyMessages(sender, 20);
        }

        List<List<CommandInfo>> commandPages = paginateCommandInfos(narrator.getCommandManager().getCommandInfos());

        if (args.length == 2) {
            if (!isValidPageInput(args[1])) {
                sender.sendMessage(CCUtil.translate(Lang.COMMAND_NOT_NUMBER));
                return;
            }

            int pageNumber = Integer.parseInt(args[1]);
            if (pageNumber > commandPages.size() || pageNumber <= 0) {
                handleInvalidPageNumber(sender, pageNumber, commandPages.size());
                return;
            }

            sendHelpPage(sender, commandPages, pageNumber, command);
            return;
        }

        sendHelpPage(sender, commandPages, 1, command);
    }

    private void sendHelpPage(CommandSender sender, List<List<CommandInfo>> commandPages, int pageNumber, Command command) {
        for (String s : Lang.COMMAND_HELP) {
            if (s.equalsIgnoreCase("%commands%")) {
                List<CommandInfo> pageCommands = commandPages.get(pageNumber - 1);
                for (CommandInfo ci : pageCommands) {
                    if (sender.hasPermission(ci.permission())) {
                        sender.sendMessage(CCUtil.translate(" &f◆ &8| &b" + ci.syntax().replace("%command%", command.getName()) + " &8- &b" + ci.description()));
                    }
                }
            }
            sender.sendMessage(CCUtil.translate(
                    s.replace("%pages%", String.valueOf(pageNumber)
                            .replace("%maxPages%", String.valueOf(commandPages.size()))
                            .replace("%commands%", "")))
            );
        }
    }

    private List<List<CommandInfo>> paginateCommandInfos(List<CommandInfo> commandInfos) {
        return IntStream.range(0, commandInfos.size())
                .boxed()
                .collect(Collectors.groupingBy(i -> i / 5))
                .values()
                .stream()
                .map(indices -> indices
                        .stream()
                        .map(commandInfos::get)
                        .toList()
                )
                .toList();
    }

    private void handleInvalidPageNumber(CommandSender sender, int pageNumber, int maxPages) {
        if (pageNumber <= 0) {
            sender.sendMessage(CCUtil.translate(Lang.COMMAND_LESS_THAN_MAX_PAGES));
        } else {
            sender.sendMessage(CCUtil.translate(Lang.COMMAND_MORE_THAN_MAX_PAGES));
        }
    }

    private boolean isValidPageInput(String input) {
        if (input.isEmpty()) {
            return false;
        }
        for (int i = input.length(); --i >= 0;) {
            if (!Character.isDigit(input.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private void sendEmptyMessages(CommandSender sender, int count) {
        for (int i = 0; i < count; i++) {
            sender.sendMessage("");
        }
    }

}
