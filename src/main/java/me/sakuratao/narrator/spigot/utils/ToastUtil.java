package me.sakuratao.narrator.spigot.utils;

import eu.endercentral.crazy_advancements.JSONMessage;
import eu.endercentral.crazy_advancements.advancement.Advancement;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@UtilityClass
public class ToastUtil {

    public String handleTitle(String title){
        String[] t = title.split("<br>");
        StringBuilder sb = new StringBuilder();

        /*
            这段屎山，没事别碰，因为实在是太乱了
            主要是让单句文字不串行
         */
        for (int i = 0; i < t.length; i ++) {
            String t1 = t[i];
            if (i > 2 && i == t.length - 1) {
                if (t[i-1].getBytes().length >= 21) {
                    sb.append(" ").append(t1);
                } else {
                    sb.append(t1);
                }
                break;
            }
            for (int k = 0; k < t1.length(); k++) {
                if (i > 2) {
                    char ch = t1.charAt(k);
                    if (ch == ' ') {
                        sb.append(" ");
                    }
                }
            }
            sb.append(t1);
            if (t1.getBytes().length <= 26) {
                sb.append(" ".repeat((26 - t1.getBytes().length)));
            }
        }

        return sb.toString();
    }

    public void showToast(
            Player player,
            Material material,
            String title,
            String frame
    ){
        TextComponent titleTextComponent = new TextComponent(CCUtil.translate(title));

        AdvancementDisplay rootDisplay =  new AdvancementDisplay(
                material,
                new JSONMessage(titleTextComponent),
                new JSONMessage(new TextComponent("")),
                AdvancementDisplay.AdvancementFrame.parse(frame),
                AdvancementVisibility.ALWAYS
        );
        Advancement rootAdvancement = new Advancement(null, rootDisplay);

        rootAdvancement.displayToast(player);
    }

}
