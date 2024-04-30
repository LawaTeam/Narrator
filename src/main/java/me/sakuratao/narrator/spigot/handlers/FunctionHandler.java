package me.sakuratao.narrator.spigot.handlers;

import me.sakuratao.narrator.spigot.enums.FunctionType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import top.jingwenmc.spigotpie.common.instance.PieComponent;

import java.util.Arrays;
import java.util.List;

@PieComponent
public class FunctionHandler {

    /**
     * 解析 位置信息
     * @param function - function
     * @return location
     */
    public Location decodeLoc(String function){
        List<String> coordinate = Arrays.stream(getInBrackets(function).split(",")).toList();

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
        return Bukkit.getPlayerExact(getInBrackets(function));
    }

    /**
     * 获取 function 语句的 type
     * @param function - function
     * @return - functionType
     */
    public FunctionType getType(String function){
        return FunctionType.valueOf(function.split("<")[0].toUpperCase());
    }

    /**
     * 用于解析 function 语句 < > 内的内容
     * @param functionWithBracket - 带有 < > 的 function 语段
     * @return < > 内的内容
     */
    private String getInBrackets(String functionWithBracket){
        String[] split = functionWithBracket.split("<");
        return split[1].replace(">", "");
    }

}
