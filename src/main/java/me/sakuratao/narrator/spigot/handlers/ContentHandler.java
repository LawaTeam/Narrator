package me.sakuratao.narrator.spigot.handlers;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.utils.CCUtil;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

@PieComponent
public class ContentHandler {

    @Wire
    private Narrator narrator;

    public boolean execute(Player player, ChapterData chapterData, String content){

        List<String> type = Arrays.asList(content.split(":"));

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
                long delay = Long.parseLong(type.get(2));
                for (int textLength = 0; textLength <= text.length(); textLength++){
                    while ((System.currentTimeMillis() + delay/text.length() > System.currentTimeMillis())){
                        // delay打印
                    }
                    if (textLength % 2 == 0) {
                        audience.sendActionBar(Component.text(CCUtil.translate(text.substring(0, textLength) + "_")));
                    } else {
                        audience.sendActionBar(Component.text(CCUtil.translate(text.substring(0, textLength))));
                    }
                }

                long time = 0;
                long keep = Long.parseLong(type.get(3));
                while ((System.currentTimeMillis() + keep) > System.currentTimeMillis()) {
                    // 字幕停留
                    time++;
                    if (time >= 1000L) {
                        time = 0;
                        audience.sendActionBar(Component.text(CCUtil.translate(text)));
                    }
                }

                return true;
            }
            case "D":
            case "DELAY":{
                while ((System.currentTimeMillis() + Integer.parseInt(type.get(1))) > System.currentTimeMillis()) {
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
                // TODO:      # TASK
                //            # --Feature:
                //            #     jump to another task
                //            # --Form: TASK:name
                return true;
            }
            case "JC":
            case "JUMPCHAPTER":{
                return true;
            }
            default:{
                return false;
            }
        }

    }

}
