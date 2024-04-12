package me.sakuratao.narrator.spigot.data.chapter;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TaskData {

    private String section; // 与yaml有关的东西 chapterTasks.Task1 的 Task1 就是 section

    private String name;
    private int ordinal = 1;
    private List<String> content = new ArrayList<>();

}
