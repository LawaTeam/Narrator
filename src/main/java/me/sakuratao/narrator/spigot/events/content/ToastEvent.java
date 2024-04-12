package me.sakuratao.narrator.spigot.events.content;

import lombok.Getter;
import me.sakuratao.narrator.spigot.events.NarratorEvent;
import me.sakuratao.narrator.spigot.utils.ToastUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ToastEvent extends NarratorEvent {

    @Getter private final Player player;
    @Getter private final Material material;
    @Getter private final String title;
    @Getter private final String frame;

    public ToastEvent(Player player, Material material, String title, String frame){
        this.player = player;
        this.material = material;
        this.title = title;
        this.frame = frame;
    }

    public void showToast(){
        ToastUtil.showToast(player, material, title, frame);
    }

}
