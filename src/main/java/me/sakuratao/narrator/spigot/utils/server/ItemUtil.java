package me.sakuratao.narrator.spigot.utils.server;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@UtilityClass
public class ItemUtil {

    public ItemStack create(Material material){
        return new ItemStack(material);
    }

    public ItemStack create(Material material, int amount){
        return new ItemStack(material, amount);
    }

    public ItemStack create(
            Material material,
            int amount,
            String displayName,
            List<String> lore

    ){
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(CCUtil.translate(displayName));
        if (lore != null) {
            itemMeta.setLore(lore);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public void setItemStack(Player p, List<Integer> slots, ItemStack itemStack) {

        for (int slot : slots) {
            p.getInventory().setItem(slot, itemStack);
        }

    }

}
