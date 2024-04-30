package me.sakuratao.narrator.spigot.data.chapter;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChapterData {

    private String name;
    private String author;
    private double version;
    private int ordinal = 1;

    private String lang;

    private List<TaskData> tasks = new ArrayList<>();

}
