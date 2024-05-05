package me.sakuratao.narrator.spigot.utils.server;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@UtilityClass
public class ItemUtil {

    public ItemStack create(Material material){
        return new ItemStack(material);
    }

    public ItemStack create(Material material, int amount){
        return new ItemStack(material, amount);
    }

    public ItemMeta getItemMeta(ItemStack item) {
        return item.getItemMeta();
    }

}
