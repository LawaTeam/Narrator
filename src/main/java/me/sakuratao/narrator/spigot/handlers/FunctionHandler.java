package me.sakuratao.narrator.spigot.handlers;

import me.sakuratao.narrator.spigot.enums.FunctionType;
import me.sakuratao.narrator.spigot.utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import top.jingwenmc.spigotpie.common.instance.PieComponent;

import java.util.Arrays;
import java.util.List;

@PieComponent
public class FunctionHandler {

    /**
     * 解码函数，根据传入的函数参数获取指定位置的方块。
     *
     * @param function 一个包含方块坐标信息的字符串，坐标信息应该在括号内，以逗号分隔.
     * @return 返回在指定坐标处找到的方块对象。
     */
    public Block decodeBlock(String function) {

        // 从函数参数中提取坐标信息，并转换为列表形式
        List<String> coordinate = Arrays.stream(StringUtil.getInBrackets(function).split(",")).toList();

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
     * 解析 位置信息
     * @param function - function
     * @return location
     */
    public Location decodeLoc(String function){
        List<String> coordinate = Arrays.stream(StringUtil.getInBrackets(function).split(",")).toList();

        World world = Bukkit.getWorld(coordinate.get(0));
        if (world != null) {
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

}
