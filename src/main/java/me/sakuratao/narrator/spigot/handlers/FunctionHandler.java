package me.sakuratao.narrator.spigot.handlers;

import me.sakuratao.narrator.spigot.enums.FunctionType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import top.jingwenmc.spigotpie.common.instance.PieComponent;

import java.util.Arrays;
import java.util.List;

@PieComponent
public class FunctionHandler {

    /**
     * 解析相关 Function 功能, 解析出对应的 type
     * @param function - function
     * @return type
     */
    public FunctionType identifyType(String function) {

        String tag = getTag(function);

        if (Bukkit.getWorld(tag) != null) return FunctionType.LOC;
        if (tag.equalsIgnoreCase("Player")) return FunctionType.PLAYER;

        return FunctionType.valueOf(function);
    }

    /**
     * 解析 位置信息
     * @param function - function
     * @return location
     */
    public Location decodeLoc(String function){
        List<Double> coordinate = Arrays.stream(getInBrackets(function).split(",")).map(Double::parseDouble).toList();

        World world = Bukkit.getWorld(getTag(function));
        if (world != null) {
            double x = coordinate.get(0);
            double y = coordinate.get(1);
            double z = coordinate.get(2);
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
     * 获取 function 语句的 tag
     * @param function - function
     * @return - tag
     */
    private String getTag(String function){
        return function.split("<")[0];
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
