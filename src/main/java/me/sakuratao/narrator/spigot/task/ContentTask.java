package me.sakuratao.narrator.spigot.task;

import lombok.Getter;
import lombok.Setter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.data.Player.PlayerData;
import me.sakuratao.narrator.spigot.enums.DelayStatus;
import me.sakuratao.narrator.spigot.enums.OptionStatus;
import me.sakuratao.narrator.spigot.enums.PrintStatus;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

@Getter
@Setter
public class ContentTask implements Runnable{

    private final Narrator narrator;
    private final PlayerData data;

    public ContentTask(Narrator narrator, PlayerData data) {
        this.narrator = narrator;
        this.data = data;
    }

    /*
        These are work for actionbar print model
     */
    private PrintStatus printStatus = PrintStatus.NONE;
    private BukkitTask printTask = null;

    /*
        These are work for 消息选择 model
     */
    private int optionIndex = -1;
    private int optionSize = 0;
    private OptionStatus optionStatus = OptionStatus.NONE;
    private BukkitTask optionTask = null;

    private DelayStatus delayStatus = DelayStatus.NONE;

    @Override
    public void run() {

        if (!data.getPlayer().isOnline()) return;

        if (narrator.getHandlerManager().getContentHandler().execute(
                data.getPlayer(),
                data.getPlayingChapter(),
                data.getPlayingTask().getContent().get(data.getContentIndex()),
                this
        )){

            printTask = null;
            printStatus = PrintStatus.NONE;

            optionIndex = 0;
            optionSize = 0;
            optionTask = null;
            optionStatus = OptionStatus.NONE;

            delayStatus = DelayStatus.NONE;

            List<String> content = data.getPlayingTask().getContent();

            data.setContentIndex(data.getContentIndex() + 1);

            if (data.getContentIndex() >= content.size()) {
                narrator.getManagerHandler().getTaskManager().kill(data.getPlayerName());
                return;
            }

        }

    }

}
