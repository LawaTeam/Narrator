package me.sakuratao.narrator.spigot.events.content.actionbar;

import lombok.Getter;
import lombok.Setter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.data.player.PlayerData;
import me.sakuratao.narrator.spigot.enums.OptionStatus;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import me.sakuratao.narrator.spigot.utils.CCUtil;
import me.sakuratao.narrator.spigot.utils.TaskUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class ActionBarAnswerEvent extends NarratorEvent {

    private final Narrator narrator;
    @Getter private final Player player;
    @Getter private final PlayerData playerData;

    @Getter private final List<String> options;

    @Getter @Setter private int optionIndex = -1;
    @Getter @Setter private OptionStatus optionStatus = OptionStatus.NONE;
    @Getter private BukkitTask optionTask = null;

    public ActionBarAnswerEvent(Narrator narrator, Player player, PlayerData playerData, List<String> options){
        this.narrator = narrator;
        this.player = player;
        this.playerData = playerData;
        this.options = options;
    }

    public void checkAnswer() {
        Audience audience = narrator.getAdventure().player(player);
        if (optionStatus.equals(OptionStatus.NONE)) {

            optionIndex = 0;
            optionStatus = OptionStatus.DECIDING;

            optionTask = TaskUtil.taskTimerAsync(() -> {
                if (optionStatus.equals(OptionStatus.DECIDED)) {
                    return;
                }
                StringBuilder message = new StringBuilder();
                for (String m : options) {
                    if (optionIndex == options.indexOf(m)) {
                        message.append("&a&l").append(m);
                    } else {
                        message.append(m);
                    }
                    if (options.indexOf(m) < options.size() - 1) {
                        message.append(" &7| &r");
                    }
                }
                audience.sendActionBar(Component.text(CCUtil.translate(message.toString())));

            }, 0, 5);
        }

    }

    public boolean isDecided(){
        if (optionStatus.equals(OptionStatus.DECIDED)) {
            optionTask.cancel();
            return true;
        }
        return false;
    }

}
