package me.sakuratao.narrator.spigot.command.sub.player;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.command.base.CommandInfo;
import me.sakuratao.narrator.spigot.command.base.SubCommand;
import me.sakuratao.narrator.spigot.configuration.Permission;
import me.sakuratao.narrator.spigot.configuration.lang.LangPlayer;
import me.sakuratao.narrator.spigot.data.player.PlayerData;
import me.sakuratao.narrator.spigot.utils.server.ItemUtil;
import me.sakuratao.narrator.spigot.utils.server.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandInfo(name = "toggle", description = "开始/暂停剧情", permission = Permission.COMMAND_PLAYER_PLAY, syntax = "/%command% toggle", canConsoleUse = false)
public class Toggle implements SubCommand {

    @Override
    public void execute(Narrator narrator, CommandSender sender, Command command, String[] args) {

        Player player = (Player) sender;
        PlayerData playerData = narrator.getManagerHandler().getPlayerManager().getByPlayer(player);

        if (!narrator.getManagerHandler().getTaskManager().isExists(player.getName())) {
            PlayerUtil.sendMessage(player, LangPlayer.CONTENT_TASK_NOT_STARTED);
            return;
        }

        if (!playerData.isStopped()) {

            playerData.setStopped(true);

            playerData.setCachedInventory(player.getInventory().getContents());
            player.getInventory().clear();

            ItemStack barrier = ItemUtil.create(
                    Material.BARRIER, 1, LangPlayer.CONTENT_TASK_STOPPED, null
            );

            player.getInventory().setItem(4, barrier);

        } else {
            playerData.setStopped(false);
            player.getInventory().clear();
            player.getInventory().setContents(playerData.getCachedInventory());

        }

    }

}
