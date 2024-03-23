package me.sakuratao.narrator.spigot.utils;

import eu.endercentral.crazy_advancements.JSONMessage;
import eu.endercentral.crazy_advancements.advancement.Advancement;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

@UtilityClass
public class ToastUtil {

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
