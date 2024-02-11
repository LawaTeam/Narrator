package me.sakuratao.narrator.spigot.data.chapter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChapterData {

    private String name;
    private String author;
    private double version;
    private int ordinal;

    private List<TaskData> tasks;

}
