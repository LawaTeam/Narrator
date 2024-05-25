package me.sakuratao.narrator.spigot.handlers;

import lombok.Getter;
import me.sakuratao.narrator.spigot.data.cache.CacheData;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.data.chapter.TaskData;
import me.sakuratao.narrator.spigot.data.player.PlayerData;
import me.sakuratao.narrator.spigot.manager.PlayerManager;
import me.sakuratao.narrator.spigot.utils.server.CCUtil;
import org.bukkit.entity.Player;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@PieComponent
public class DebugHandler {
    
    @Wire private PlayerManager playerManager;

    @Getter public final ConcurrentHashMap<Player, String> debugListeners = new ConcurrentHashMap<>();
    
    public void debug(
            Player player,
            ChapterData chapterData,
            TaskData taskData,
            String contentType,
            List<String> contentDetails,
            boolean isCancelled
    ){
        PlayerData playerData = playerManager.getByPlayer(player);
        List<Player> listeners = debugListeners.keySet().stream().toList();
        for (Player listener : listeners) {

            String[] listenDetail = debugListeners.get(listener).split(";");
            String listenedChapter = listenDetail[0];
            String listenedTask = listenDetail[1];
            String listenedPlayer = listenDetail[2];
            String listenedContentType = listenDetail[3];
            DecimalFormat df = new DecimalFormat("0.00");

            String chapter = chapterData.getName();
            double chapterVersion = chapterData.getVersion();
            String language = chapterData.getLang();
            String taskName = taskData.getName();
            String taskWorld = taskData.getWorld() == null ? "null" : taskData.getWorld().getName();

            sendDebugMessage(
                    player,
                    chapter,
                    chapterVersion,
                    language,
                    taskName,
                    taskWorld,
                    playerData.getContentIndex() + "",
                    contentType,
                    contentDetails,
                    isCancelled,
                    listener,
                    listenDetail,
                    listenedChapter,
                    listenedTask,
                    listenedPlayer,
                    listenedContentType,
                    df
            );

        }
    }

    public void debug(
            Player player,
            ChapterData chapterData,
            TaskData taskData,
            String contentType,
            String contentDetail,
            boolean isCancelled
    ){
        PlayerData playerData = playerManager.getByPlayer(player);
        List<Player> listeners = debugListeners.keySet().stream().toList();
        for (Player listener : listeners) {

            String[] listenDetail = debugListeners.get(listener).split(";");
            String listenedChapter = listenDetail[0];
            String listenedTask = listenDetail[1];
            String listenedPlayer = listenDetail[2];
            String listenedContentType = listenDetail[3];
            DecimalFormat df = new DecimalFormat("0.00");

            String chapter = chapterData.getName();
            double chapterVersion = chapterData.getVersion();
            String language = chapterData.getLang();
            String taskName = taskData.getName();
            String taskWorld = taskData.getWorld() == null ? "null" : taskData.getWorld().getName();

            sendDebugMessage(
                    player,
                    chapter,
                    chapterVersion,
                    language,
                    taskName,
                    taskWorld,
                    playerData.getContentIndex() + "",
                    contentType,
                    Collections.singletonList(contentDetail),
                    isCancelled,
                    listener,
                    listenDetail,
                    listenedChapter,
                    listenedTask,
                    listenedPlayer,
                    listenedContentType,
                    df
            );

        }

    }

    public void debug(
            Player player,
            String chapter,
            double chapterVersion,
            String language,
            String taskName,
            String taskWorld,
            String contentType,
            String contentDetail,
            boolean isCancelled
    ){
        PlayerData playerData = playerManager.getByPlayer(player);
        List<Player> listeners = debugListeners.keySet().stream().toList();
        for (Player listener : listeners) {

            String[] listenDetail = debugListeners.get(listener).split(";");
            String listenedChapter = listenDetail[0];
            String listenedTask = listenDetail[1];
            String listenedPlayer = listenDetail[2];
            String listenedContentType = listenDetail[3];
            DecimalFormat df = new DecimalFormat("0.00");

            sendDebugMessage(
                    player,
                    chapter,
                    chapterVersion,
                    language,
                    taskName,
                    taskWorld,
                    playerData.getContentIndex() + "",
                    contentType,
                    Collections.singletonList(contentDetail),
                    isCancelled,
                    listener,
                    listenDetail,
                    listenedChapter,
                    listenedTask,
                    listenedPlayer,
                    listenedContentType,
                    df
            );
        }

    }

    private void sendDebugMessage(
            Player player,
            String chapter,
            double chapterVersion,
            String language,
            String taskName,
            String taskWorld,
            String contentIndex,
            String contentType,
            List<String> contentDetails,
            boolean isCancelled,
            Player listener,
            String[] listenDetail,
            String listenedChapter,
            String listenedTask,
            String listenedPlayer,
            String listenedContentType,
            DecimalFormat df
    ) {
        if (listenDetail[0].equalsIgnoreCase("all")) {
            listenedChapter = chapter;
        }
        if (listenDetail[1].equalsIgnoreCase("all")) {
            listenedTask = taskName;
        }
        if (listenDetail[2].equalsIgnoreCase("all")) {
            listenedPlayer = player.getName();
        }
        if (listenDetail[3].equalsIgnoreCase("all")) {
            listenedContentType = contentType;
        }

        if (
                chapter.equalsIgnoreCase(listenedChapter)
                        && taskName.equalsIgnoreCase(listenedTask)
                        && player.getName().equalsIgnoreCase(listenedPlayer)
                        && contentType.contains(listenedContentType)
        ) {
            listener.sendMessage(CCUtil.translate("&8&o--- &7Narrator &8[Debug] &7| &f" + chapter + "&7-&f" + chapterVersion + "&7-&f" + language));
            listener.sendMessage(CCUtil.translate("  &8|- &7Task: &f" + taskName + " &7| contentIndex: &f" + contentIndex + " &7| World: &f" + taskWorld));
            listener.sendMessage(CCUtil.translate("  &8|- &7Player: &f" + player.getName() + " &7| XYZ: &f" + df.format(player.getLocation().getX()) + " " + df.format(player.getLocation().getY()) + " " + df.format(player.getLocation().getZ())));
            listener.sendMessage(CCUtil.translate("  &8|- &7contentType: &f" + contentType));
            listener.sendMessage(CCUtil.translate("  &8|- &7contentDetails: &f"));
            for (String contentDetail : contentDetails) {
                listener.sendMessage(CCUtil.translate("  &8    |- &7&f" + contentDetail));
            }
            listener.sendMessage(CCUtil.translate("  &8|- &7isCancelled: " + (isCancelled ? "&a" + "true" : "&c" + "false")));
        }
    }

}
