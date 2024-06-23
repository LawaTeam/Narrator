package me.sakuratao.narrator.spigot.data.chapter;

import lombok.Data;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

@Data
public class TaskData {

    private String section; // 与yaml有关的东西 chapterTasks.Task1 的 Task1 就是 section

    private String name;
    private int ordinal = 1;
    private World world;
    private List<String> content = new ArrayList<>();

    public String getContentByIndex(int contentIndex){
        contentIndex -= 1;
        if (contentIndex < 0 || contentIndex >= content.size()) return "Error";
        return content.get(contentIndex);
    }

}
