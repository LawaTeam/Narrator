package me.sakuratao.narrator.spigot.utils.server;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class ItemUtil {

    public ItemStack create(Material material){
        return new ItemStack(material);
    }

}
