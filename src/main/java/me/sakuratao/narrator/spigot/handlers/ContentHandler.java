package me.sakuratao.narrator.spigot.handlers;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.data.Player.PlayerData;
import me.sakuratao.narrator.spigot.data.cache.CacheData;
import me.sakuratao.narrator.spigot.utils.CCUtil;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

@PieComponent
public class ContentHandler {

    @Wire
    private Narrator narrator;
    @Wire
    private CacheData cacheData;

    public boolean execute(Player player, ChapterData chapterData, String content){

        List<String> type = Arrays.asList(content.split(":"));

        PlayerData playerData = narrator.getManagerHandler().getPlayerManager().getByPlayer(player);
        playerData.setNextContent(false);

        /*
         * 这里会解析 content 的内容
         * type.get(0) 即是所对应的功能
         * 其余的则是所对应功能的参数，详细请翻阅 ChapterExample.yml
         */
        switch (type.get(0)) {
            case "T":
            case "TITLE":{
                player.sendTitle(
                        CCUtil.translate(type.get(4)), CCUtil.translate(type.get(5)),
                        Integer.parseInt(type.get(1)), Integer.parseInt(type.get(2)), Integer.parseInt(type.get(3))
                );
                return true;
            }
            case "M":
            case "MESSAGE":{
                player.sendMessage(type.get(1));
                return true;
            }
            case "AB":
            case "ACTIONBAR":{
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
                long delayTime = Long.parseLong(type.get(2));
                for (int textLength = 0; textLength <= text.length(); textLength++){
                    while (delayTime > System.currentTimeMillis()){
                        // delay打印
                    }
                    delayTime = System.currentTimeMillis() + Long.parseLong(type.get(2));
                    if (textLength % 2 == 0) {
                        audience.sendActionBar(Component.text(CCUtil.translate(text.substring(0, textLength) + "_")));
                    } else {
                        audience.sendActionBar(Component.text(CCUtil.translate(text.substring(0, textLength))));
                    }
                }

                long time = System.currentTimeMillis();
                long keep = System.currentTimeMillis() + Long.parseLong(type.get(3));
                while (keep > System.currentTimeMillis()) {

                    while (time >= System.currentTimeMillis()) {
                        // 字幕停留
                    }

                    audience.sendActionBar(Component.text(CCUtil.translate(text)));
                    time = System.currentTimeMillis();

                }

                return true;
            }
            case "AB_ANSWER":
            case "ACTIONBAR_ANSWER": {

                Audience audience = narrator.getAdventure().player(player);
                List<String> messages = new ArrayList<>();

                for (int i = 1; i < type.size(); i++) {
                    messages.add(type.get(i));
                }

                cacheData.getContentIndex().put(player.getName().toLowerCase(), 0);
                if (cacheData.getContentIndex().get(player.getName().toLowerCase()) > messages.size()){
                    cacheData.getContentIndex().put(player.getName().toLowerCase(), 0);
                }

                StringBuilder message = new StringBuilder();
                for (String m : messages) {
                    if (cacheData.getContentIndex().get(player.getName().toLowerCase()) == messages.indexOf(m)) {
                        message.append("&a&l").append(m);
                    } else {
                        message.append(m);
                    }
                    if (messages.indexOf(m) < messages.size()) {
                        message.append(" &7| &r");
                    }
                }

                long time = System.currentTimeMillis() + 250L;
                while (cacheData.getContentIndex().get(player.getName().toLowerCase()) == -1) {
                    // 字幕停留
                    if (System.currentTimeMillis() >= time) {
                        time = System.currentTimeMillis() + 250L;
                        audience.sendActionBar(Component.text(CCUtil.translate(message.toString())));
                    }
                }

                playerData.setMessageOption(messages.get(cacheData.getContentIndex().get(player.getName().toLowerCase())));
                player.sendMessage(playerData.getMessageOption());

                cacheData.getContentIndex().remove(player.getName().toLowerCase());

                return true;
            }
            case "D":
            case "DELAY":{
                long delay = System.currentTimeMillis() + Integer.parseInt(type.get(1));
                while (delay > System.currentTimeMillis()) {
                }
                return true;
            }
            case "C":
            case "CONDITION":{
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
            case "COMMAND":{
                narrator.getLogger().log(Level.WARNING, "Executed command: "+ type.get(1) + " | Chapter: "  + chapterData.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), type.get(1));
                return true;
            }
            case "JT":
            case "JUMPTASK":{
                narrator.getManagerHandler().getTaskManager().jump(Integer.parseInt(type.get(1)));
                return true;
            }
            case "JC":
            case "JUMPCHAPTER":{
                return true;
            }
            default:{
                return true;
            }
        }

    }

}
