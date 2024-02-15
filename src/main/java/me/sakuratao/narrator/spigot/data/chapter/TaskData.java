package me.sakuratao.narrator.spigot.data.chapter;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
public class TaskData {

    private String name;
    private int ordinal = 0;
    private List<String> content = new ArrayList<>();

}
