package me.sakuratao.narrator.spigot.data.chapter;

import lombok.*;
import org.bukkit.configuration.file.YamlConfiguration;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChapterData {

    private String name;
    private String author;
    private double version;
    private int ordinal = 1;

    private List<TaskData> tasks = new ArrayList<>();

}
