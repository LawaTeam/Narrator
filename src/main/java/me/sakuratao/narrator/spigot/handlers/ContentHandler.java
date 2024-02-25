package me.sakuratao.narrator.spigot.handlers;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.NarratorSpigot;
import me.sakuratao.narrator.spigot.data.Player.PlayerData;
import me.sakuratao.narrator.spigot.data.cache.CacheData;
import me.sakuratao.narrator.spigot.task.ContentTask;
import me.sakuratao.narrator.spigot.utils.CCUtil;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

@PieComponent
public class ContentHandler {

    @Wire
    private Narrator narrator;
    @Wire
    private CacheData cacheData;

    public boolean execute(Player player, ChapterData chapterData, String content, ContentTask contentTask) {

        List<String> type = Arrays.asList(content.split("\\|"));

        PlayerData playerData = narrator.getManagerHandler().getPlayerManager().getByPlayer(player);
        /*
         * 这里会解析 content 的内容
         * type.get(0) 即是所对应的功能
         * 其余的则是所对应功能的参数，详细请翻阅 ChapterExample.yml
         */
        try {
            switch (type.get(0)) {
                case "T":
                case "TITLE": {
                    player.sendTitle(
                            CCUtil.translate(type.get(4)), CCUtil.translate(type.get(5)),
                            Integer.parseInt(type.get(1)), Integer.parseInt(type.get(2)), Integer.parseInt(type.get(3))
                    );
                    return true;
                }
                case "M":
                case "MESSAGE": {
                    player.sendMessage(CCUtil.translate(type.get(1)));
                    return true;
                }
                case "MC":
                case "MESSAGE_CLICK": {
                    return true;
                }
                case "AB":
                case "ACTIONBAR": {

                    Audience audience = narrator.getAdventure().player(player);
                    if (!Boolean.parseBoolean(type.get(1))) {
                        audience.sendActionBar(Component.text(CCUtil.translate(type.get(2))));
                        return true;
                    }

                    /*
                      这里会对 text 处理成一个打字机的效果
                      text将会在 delay 限定的时间内完成逐字打印
                     */
                    String text = type.get(4);

                    if (!contentTask.isPrinting()) {

                        contentTask.setPrinted(false);
                        contentTask.setPrinting(true);
                        contentTask.setPrintKeeping(true);

                        long delayTime = Long.parseLong(type.get(2));

                        AtomicInteger textLength = new AtomicInteger(0);

                        contentTask.setPrintTask(
                                Bukkit.getScheduler().runTaskTimerAsynchronously(NarratorSpigot.getPluginInstance(), () -> {
                                    if (textLength.get() >= text.length()) {
                                        contentTask.setPrintKeeping(false);
                                        return;
                                    }

                                    textLength.set(textLength.get() + 1);
                                    String outputText = text.substring(0, textLength.get());

                                    if (textLength.get() % 2 == 0 && textLength.get() != text.length()) {
                                        audience.sendActionBar(Component.text(CCUtil.translate(outputText + "&kA&r_")));
                                        return;
                                    }
                                    audience.sendActionBar(Component.text(CCUtil.translate(outputText)));
                                }, 0, delayTime)
                        );

                        return false;
                    }

                    long keep = (Long.parseLong(type.get(3))/20 * 1000) + System.currentTimeMillis();
                    if (!contentTask.isPrintKeeping()) {

                        contentTask.getPrintTask().cancel();
                        contentTask.setPrintKeeping(true);
                        Bukkit.getScheduler().runTaskTimerAsynchronously(NarratorSpigot.getPluginInstance(), () -> {

                            if (System.currentTimeMillis() > keep) {
                                contentTask.setPrinted(true);
                                return;
                            }
                            audience.sendActionBar(Component.text(CCUtil.translate(text)));

                        }, 0, 20);
                    }

                    return contentTask.isPrinted();
                }
                case "AB_ANSWER":
                case "ACTIONBAR_ANSWER": {

                    Audience audience = narrator.getAdventure().player(player);
                    List<String> messages = new ArrayList<>();

                    for (int i = 1; i < type.size(); i++) {
                        messages.add(type.get(i));
                    }

                    if (!contentTask.isMessageDeciding()) {

                        contentTask.setMessageSize(messages.size());
                        contentTask.setMessageIndex(0);
                        contentTask.setMessageDecided(false);
                        contentTask.setMessageDeciding(true);

                        contentTask.setMessageTask(Bukkit.getScheduler().runTaskTimerAsynchronously(NarratorSpigot.getPluginInstance(), () -> {

                            if (contentTask.isMessageDecided()) {
                                playerData.setMessageOption(messages.get(contentTask.getMessageIndex()));
                                contentTask.setMessageIndex(-1);
                                return;
                            }

                            StringBuilder message = new StringBuilder();
                            for (String m : messages) {
                                if (contentTask.getMessageIndex() == messages.indexOf(m)) {
                                    message.append("&a&l").append(m);
                                } else {
                                    message.append(m);
                                }
                                if (messages.indexOf(m) < messages.size() - 1) {
                                    message.append(" &7| &r");
                                }
                            }
                            audience.sendActionBar(Component.text(CCUtil.translate(message.toString())));

                        }, 0, 5));

                    }

                    if (contentTask.isMessageDecided()) {
                        contentTask.getMessageTask().cancel();
                        return true;
                    }
                    return false;
                }
                case "D":
                case "DELAY": {
                    contentTask.setDelay(true);

                    if (!contentTask.isDelaying()) {
                        contentTask.setDelaying(true);
                        long delay = Integer.parseInt(type.get(1));
                        Bukkit.getScheduler().runTaskLater(NarratorSpigot.getPluginInstance(), () -> {
                            contentTask.setDelay(false);
                        }, delay);
                    }

                    return !contentTask.isDelay();
                }
                case "C":
                case "CONDITION": {
                    // TODO:      # C/CONDITION
                    //            # --Feature:
                    //            #     need player finish some condition then can execute next content
                    //            # --Form:
                    //            #       a: C/CONDITION:CONDITION1
                    //            #       b: C/CONDITION:CONDITION1|CONDITION2
                    //            #       c: C/CONDITION:CONDITION1||CONDITION2
                    //            #       d: C/CONDITION:CONDITION1&CONDITION2
                    //            #       e: C/CONDITION:CONDITION1&&CONDITION2
                    //            #
                    //            #   CONDITION can be:
                    //            #       NPC - touch NPC
                    //            #       MOVE - appoint move to any location
                    //            #       INTERACT - touch something
                    //            #       KILL - need to kill appoint entity
                    //            #
                    //            #   | means if one of two conditions be false then execute
                    //            #   || means two conditions need to be false
                    //            #   & means if one of two conditions be true then execute
                    //            #   && means two conditions need to be true
                    return true;
                }
                case "COMMAND": {
                    narrator.getLogger().log(Level.WARNING, "Executed command: " + type.get(1) + " | Chapter: " + chapterData.getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), type.get(1));
                    return true;
                }
                case "JT":
                case "JUMP_TASK": {

                    int taskOrdinal = Integer.parseInt(type.get(1));
                    int contentIndex = Integer.parseInt(type.get(2));

                    if (taskOrdinal < 1 || contentIndex < 1) {
                        narrator.getLogger().log(Level.SEVERE, "All ordinal must be over 1!");
                        narrator.getLogger().log(Level.SEVERE, "Please check your content about 'JT/JUMP_TASK'");
                        narrator.getLogger().log(Level.SEVERE, "Here are some information may help you:");
                        narrator.getLogger().log(Level.SEVERE, "        Chapter Name: " + chapterData.getName());
                        narrator.getLogger().log(Level.SEVERE, "        Content: " + content);
                        return false;

                    }
                    narrator.getHandlerManager().getTaskHandler().jump(
                            playerData,
                            chapterData,
                            taskOrdinal,
                            contentIndex
                    );
                    return true;
                }
                case "JC":
                case "JUMP_CHAPTER": {
                    return true;
                }
                default: {
                    return true;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            narrator.getLogger().log(Level.SEVERE, "Throw IndexOutOfBoundsException!");
            narrator.getLogger().log(Level.SEVERE, "Please check your contents!");
            narrator.getLogger().log(Level.SEVERE, "Here are some information may help you:");
            narrator.getLogger().log(Level.SEVERE, "        Chapter Name: " + chapterData.getName());
            narrator.getLogger().log(Level.SEVERE, "        Content: " + content);
            return false;
        }
    }
}

