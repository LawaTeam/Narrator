package me.sakuratao.narrator.spigot.utils.server;

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

    private static final int MIN_LENGTH_FOR_SPACE = 21;
    private static final int MAX_LENGTH_FOR_PADDING = 26;
    private static final String BREAK_LINE = "<br>";

    public String handleTitle(String title) {
        // 安全性：对输入进行基本的验证
        if (title == null || title.isEmpty()) {
            return title;
        }

        // 使用StringBuilder时指定一个初始容量，这里假设平均每个部分的长度为10
        StringBuilder sb = new StringBuilder(Math.max(title.length() / 2, 100));

        String[] parts = title.split(BREAK_LINE);
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            // 处理最后一个部分的特殊逻辑
            if (i == parts.length - 1) {
                appendLastPart(sb, part, i > 2);
            } else {
                // 优化：只在需要时计算长度
                int partLength = part.getBytes().length;
                appendWithPadding(sb, part, partLength);
            }
        }

        return sb.toString();
    }

    private void appendLastPart(StringBuilder sb, String part, boolean checkLength) {
        if (checkLength && part.getBytes().length >= MIN_LENGTH_FOR_SPACE) {
            sb.append(" ").append(part);
        } else {
            sb.append(part);
        }
    }

    private void appendWithPadding(StringBuilder sb, String part, int partLength) {
        sb.append(part);
        // 优化：避免对相同长度重复计算
        if (partLength <= MAX_LENGTH_FOR_PADDING) {
            sb.append(" ".repeat(MAX_LENGTH_FOR_PADDING - partLength));
        }
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
