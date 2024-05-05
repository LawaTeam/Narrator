package me.sakuratao.narrator.spigot.handlers;

import me.sakuratao.narrator.spigot.enums.FunctionType;
import me.sakuratao.narrator.spigot.utils.StringUtil;
import me.sakuratao.narrator.spigot.utils.server.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import top.jingwenmc.spigotpie.common.instance.PieComponent;

import java.util.Arrays;
import java.util.List;

@PieComponent
public class FunctionHandler {

    public ItemStack decodeItemStack(String function){
        List<String> itemInfo = getInBracketsList(function);
        Material material = Material.valueOf(itemInfo.get(0));
        int amount = Integer.parseInt(itemInfo.get(1));
        return ItemUtil.create(material, amount);
    }

    /**
     * 解码函数，根据传入的函数参数获取指定位置的方块。
     *
     * @param function 一个包含方块坐标信息的字符串，坐标信息应该在括号内，以逗号分隔.
     * @return 返回在指定坐标处找到的方块对象。
     */
    public Block decodeBlock(String function) {

        // 从函数参数中提取坐标信息，并转换为列表形式
        List<String> coordinate = getInBracketsList(function);

        World world = Bukkit.getWorld(coordinate.get(0));
        if (world != null) {
            // 使用提取的坐标信息获取对应的方块
            return world.getBlockAt(
                    Integer.parseInt(coordinate.get(1)),
                    Integer.parseInt(coordinate.get(2)),
                    Integer.parseInt(coordinate.get(3))
            );
        }

        return null;
    }

    /**
     * 解码函数，根据输入的字符串来获取一个Location对象。
     * 如果输入字符串代表一个在线玩家的名称，则返回该玩家的当前位置。
     * 如果输入字符串包含坐标信息，则根据这些坐标信息和世界名称创建一个新的Location对象。
     *
     * @param function 字符串，可以是玩家名称或包含坐标信息的字符串。
     * @return Location对象，如果解析成功则返回相应的Location，否则返回null。
     */
    public Location decodeLoc(String function){

        // 尝试将输入字符串解析为玩家，并获取其位置
        Player player = Bukkit.getPlayerExact(function);
        if (player != null) {
            return player.getLocation();
        }

        // 解析输入字符串中的坐标信息
        List<String> coordinate = getInBracketsList(function);
        World world = Bukkit.getWorld(coordinate.get(0));
        if (world != null) {
            // 根据解析出的坐标和世界对象创建Location
            double x = Double.parseDouble(coordinate.get(1));
            double y = Double.parseDouble(coordinate.get(2));
            double z = Double.parseDouble(coordinate.get(3));
            return new Location(world, x, y, z);
        }
        return null;
    }

    /**
     * 解析 player
     * @param function - function
     * @return - player
     */
    public Player decodePlayer(String function){
        return Bukkit.getPlayerExact(StringUtil.getInBrackets(function));
    }

    /**
     * 获取 function 语句的 type
     * @param function - function
     * @return - functionType
     */
    public FunctionType getType(String function){
        return FunctionType.valueOf(function.split("<")[0].toUpperCase());
    }

    public Object handleType(String type){
        return switch (type) {
            case "BLOCK" -> decodeBlock(type);
            case "LOC" -> decodeLoc(type);
            case "PLAYER" -> decodePlayer(type);
            default -> null;
        };
    }

    private List<String> getInBracketsList(String function){
        return Arrays.stream(StringUtil.getInBrackets(function).split(",")).toList();
    }
}
