package me.sakuratao.narrator.spigot.listener.packets;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.NarratorSpigot;
import me.sakuratao.narrator.spigot.data.cache.CacheData;
import me.sakuratao.narrator.spigot.data.player.PlayerData;
import me.sakuratao.narrator.spigot.enums.OptionStatus;
import me.sakuratao.narrator.spigot.events.content.actionbar.ActionBarAnswerEvent;
import me.sakuratao.narrator.spigot.events.content.actionbar.ActionBarAnsweredEvent;
import me.sakuratao.narrator.spigot.task.ContentTask;
import me.sakuratao.narrator.spigot.utils.server.EventUtil;
import org.bukkit.entity.Player;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

/*
    监听玩家 drop 物品并提供回复选项
 */
@PieComponent
public class OptionListener implements PacketListener {

    @Wire
    private Narrator narrator;

    @Override
    public void onPacketReceive(PacketReceiveEvent packetEvent) {
        Player player = (Player) packetEvent.getPlayer();

        String playerName = player.getName().toLowerCase();
        PlayerData playerData = narrator.getManagerHandler().getPlayerManager().getByPlayer(player);
        ContentTask contentTask = playerData.getContentTask();

        ActionBarAnswerEvent actionBarAnswerEvent = narrator.getCacheData().getCurrentActionBarAnswerEvent(player);

        if (
                contentTask == null
                        || actionBarAnswerEvent == null
                        || actionBarAnswerEvent.getOptionTask() == null
                        || actionBarAnswerEvent.getOptionStatus().equals(OptionStatus.DECIDED)
        ) return;

        if (packetEvent.getPacketType().equals(PacketType.Play.Client.ANIMATION)) {
            actionBarAnswerEvent.setOptionStatus(OptionStatus.DECIDED);
            EventUtil.callEvent(new ActionBarAnsweredEvent(player, actionBarAnswerEvent.getOptions().get(actionBarAnswerEvent.getOptionIndex())));
        }
        if (packetEvent.getPacketType().equals(PacketType.Play.Client.HELD_ITEM_CHANGE)) {
            actionBarAnswerEvent.setOptionIndex(actionBarAnswerEvent.getOptionIndex() + 1);
            if (actionBarAnswerEvent.getOptionIndex() >= actionBarAnswerEvent.getOptions().size()) {
                actionBarAnswerEvent.setOptionIndex(0);
            }
        }
    }

}
