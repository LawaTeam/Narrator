package me.sakuratao.narrator.spigot.events.content.actionbar;

import lombok.Getter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.NarratorSpigot;
import me.sakuratao.narrator.spigot.enums.PrintStatus;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import me.sakuratao.narrator.spigot.utils.CCUtil;
import me.sakuratao.narrator.spigot.utils.TaskUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicInteger;

public class ActionBarEvent extends NarratorEvent{

    private final Narrator narrator;

    @Getter private final Player player;
    @Getter private final boolean isPrint;
    @Getter private final long printInterval;
    @Getter private final long keep;
    @Getter private final String text;
    @Getter private PrintStatus printStatus = PrintStatus.NONE;

    @Getter private BukkitTask printTask = null;



    public ActionBarEvent(Narrator narrator, Player player, boolean isPrint, long printInterval, long keep, String text){
        this.narrator = narrator;
        this.player = player;
        this.isPrint = isPrint;
        this.printInterval = printInterval;
        this.keep = keep;
        this.text = text;
    }

    public void showActionbar() {
        Audience audience = narrator.getAdventure().player(player);
        if (!isPrint) {
            audience.sendActionBar(Component.text(text));
            printStatus = PrintStatus.ENDED;
            return;
        }

        /*
          这里会对 text 处理成一个打字机的效果
          text将会在 delay 限定的时间内完成逐字打印
         */
        if (printStatus.equals(PrintStatus.NONE)) {
            printStatus = PrintStatus.PRINTING;
            printTask = runActionBarPrint(audience, new AtomicInteger(0));
        }

        if (printStatus.equals(PrintStatus.KEEPING)) {
            printStatus = PrintStatus.KEPT;
            printTask.cancel();

            long keepTime = (keep/20 * 1000) + System.currentTimeMillis();
            printTask = runActionBarKeep(audience, keepTime);
        }

    }

    private BukkitTask runActionBarKeep(Audience audience, long keepTime){
        return TaskUtil.taskTimerAsync(() -> {
            if (System.currentTimeMillis() >= keepTime) {
                printStatus = PrintStatus.ENDED;
                return;
            }
            audience.sendActionBar(Component.text(CCUtil.translate(text)));
        }, 0, 20);
    }

    private BukkitTask runActionBarPrint(Audience audience, AtomicInteger textLength){
        return TaskUtil.taskTimerAsync(() -> {
            if (textLength.get() >= text.length()) {
                printStatus = (PrintStatus.KEEPING);
                return;
            }
            textLength.set(textLength.get() + 1);
            String outputText = text.substring(0, textLength.get());

            if (textLength.get() % 2 == 0 && textLength.get() != text.length()) {
                audience.sendActionBar(Component.text(CCUtil.translate(outputText + "&kA&r_")));
                return;
            }
            audience.sendActionBar(Component.text(CCUtil.translate(outputText)));
        }, 0, printInterval);
    }

    public boolean isEnded(){
        if (printStatus.equals(PrintStatus.ENDED)) {
            printTask.cancel();
            return true;
        }
        return false;
    }

}
