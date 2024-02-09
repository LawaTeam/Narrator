package me.sakuratao.narrator.spigot.data.chapter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TaskData {

    private String name;
    private int ordinal;
    private List<String> content;

}
