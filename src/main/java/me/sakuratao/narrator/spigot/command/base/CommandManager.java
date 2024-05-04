package me.sakuratao.narrator.spigot.command.base;

import lombok.Getter;
import lombok.SneakyThrows;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.command.sub.Reload;
import me.sakuratao.narrator.spigot.command.sub.Test;
import me.sakuratao.narrator.spigot.configuration.Lang;
import me.sakuratao.narrator.spigot.configuration.Permission;
import me.sakuratao.narrator.spigot.utils.server.CCUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@PieComponent
public class CommandManager implements TabExecutor {

    @Wire private Narrator narrator;

    @Getter private final List<SubCommand> subCommands = new ArrayList<>();
    @Getter private final List<CommandInfo> commandInfos = new ArrayList<>();
    @Getter private final ConcurrentHashMap<String, SubCommand> commandMap = new ConcurrentHashMap<>();

    @SneakyThrows
    public CommandManager() {
        subCommands.add(new HelpCommand());
        subCommands.add(new Reload());
        subCommands.add(new Test());
        neaten();
    }

    /**
     * 根据SubCommand列表中的元素，筛选并整理Command信息。
     * 遍历所有subCommands，对于每个带有CommandInfo注解的SubCommand，将其commandInfo添加到COMMAND_INFO列表中，
     * 并将其本身添加到COMMAND_MAP映射中，以commandInfo的name属性为键。
     */
    private void neaten() {
        // 遍历所有子命令
        for (SubCommand subCommand : subCommands) {
            // 如果当前子命令的类没有CommandInfo注解，则跳过当前循环迭代
            if (!subCommand.getClass().isAnnotationPresent(CommandInfo.class)) {
                continue;
            }

            CommandInfo commandInfo = subCommand.getClass().getAnnotation(CommandInfo.class);
            commandInfos.add(commandInfo);
            commandMap.put(commandInfo.name().toLowerCase(), subCommand);
        }
    }

    /**
     * 处理命令执行的请求。
     *
     * @param sender 命令发送者，可能是玩家或服务器控制台。
     * @param command 被执行的命令对象。
     * @param s 命令标识符，通常为命令的名称。
     * @param args 命令参数数组。
     * @return 始终返回true，代表该命令已由本方法完全处理。
     */
    @Override
    public boolean onCommand(
            CommandSender sender,
            @NotNull Command command,
            @NotNull String s,
            String[] args
    ) {
        // 检查发送者是否有管理员权限
        if (!sender.hasPermission(Permission.ADMIN)){
            sender.sendMessage(Lang.NO_PERMISSION);
            return true;
        }

        // 如果没有提供命令参数，检查发送者是否有帮助页面访问权限
        if (args == null || args.length == 0){
            if (!sender.hasPermission(Permission.HELP)){
                sender.sendMessage("Unknown command. Type \"/help\" for help.");
                return true;
            }
            sender.sendMessage(CCUtil.translate(Lang.USE_HELP_PLEASE));
            return true;
        }

        // 使用优化后的命令查找逻辑
        String firstArg = args[0].toLowerCase();
        SubCommand subCommand = commandMap.get(firstArg);

        if (subCommand != null) {
            executeSubCommand(sender, command, subCommand, args);
        } else {
            // 如果没有找到匹配的子命令，发送帮助信息
            sender.sendMessage(CCUtil.translate(Lang.USE_HELP_PLEASE));
        }
        return true;

    }

    private void executeSubCommand(CommandSender sender, Command command, SubCommand subCommand, String[] args) {
        CommandInfo commandInfo = subCommand.getClass().getAnnotation(CommandInfo.class);
        if (commandInfo == null) {
            // 应该避免这种情况发生，如果发生则视为错误
            sender.sendMessage(CCUtil.translate("&cNarrator 指令错误."));
            return;
        }

        // 检查非玩家是否具有使用该命令的权限
        if (!(sender instanceof Player) && !commandInfo.canConsoleUse()) {
            sender.sendMessage(CCUtil.translate(Lang.COMMAND_PLAYER_ONLY));
            return;
        }

        // 检查发送者是否具有命令所需的权限
        if (!sender.hasPermission(commandInfo.permission())) {
            sender.sendMessage(CCUtil.translate(Lang.NO_PERMISSION));
            return;
        }

        // 执行找到的子命令
        subCommand.execute(narrator, sender, command, args);
        return;
    }

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String alias,
            String[] args
    ) {

        List<String> arrayList = new ArrayList<>();
        if (args.length == 1){
            for (String scName : commandMap.keySet()) {
                if (scName.toLowerCase().startsWith(args[0])){
                    arrayList.add(scName);
                };
            }
            return arrayList;
        } else {
            return getPlayers(args);
        }

    }

    @NotNull
    public static List<String> getPlayers(@NotNull String[] args) {
        List<String> player = new ArrayList<>();

        for (Player p : Bukkit.getOnlinePlayers()){
            if (p.getName().toLowerCase().startsWith(args[args.length-1].toLowerCase(Locale.ROOT))){
                player.add(p.getName());
            }
        }
        return player;
    }

}
