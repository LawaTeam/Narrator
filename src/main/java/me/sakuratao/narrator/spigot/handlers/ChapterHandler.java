package me.sakuratao.narrator.spigot.handlers;

import lombok.Getter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.common.handlers.HandlerManager;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

@PieComponent
public class ChapterHandler {

    @Wire
    private Narrator narrator;

    @Getter
    private final ConcurrentHashMap<String, Map<ChapterData, YamlConfiguration>> chapters = new ConcurrentHashMap<>();

    public void load() {

        File path = new File(narrator.getWorkFolder().getPath() + "/chapters");

        if (!path.exists()) {
            path.mkdirs();
            narrator.getLogger().log(Level.WARNING, "Chapter folder created");
            narrator.getLogger().log(Level.WARNING, "Please put your chapter files in the folder and use /nr to reload");
            return;
        }

        for (File chapterFile : Objects.requireNonNull(path.listFiles())) {

            YamlConfiguration chapter = YamlConfiguration.loadConfiguration(chapterFile);
            if (
                    chapter.getString("chapterInfo.name") == null ||
                            chapter.getString("chapterInfo.author") == null ||
                            chapter.getString("chapterInfo.version") == null ||
                            chapter.getString("chapterInfo.ordinal") == null ||
                            !Character.isDigit(Integer.parseInt(Objects.requireNonNull(chapter.getString("chapterInfo.ordinal"))))
            ) {
                narrator.getLogger().log(Level.SEVERE, "Please check your chapter file in " + chapterFile.getName());
                narrator.getLogger().log(Level.SEVERE, "These options can't be null and the ordinal must be a number");
                return;
            }

            if (!chapters.containsKey(chapter.getString("chapterInfo.name"))) {

                ChapterData chapterData = new ChapterData();
                chapterData.setName(chapter.getString("chapterInfo.name"));
                chapterData.setAuthor(chapter.getString("chapterInfo.author"));
                chapterData.setVersion(chapter.getString("chapterInfo.version"));
                chapterData.setOrdinal(chapter.getInt("chapterInfo.ordinal"));

                Map<ChapterData, YamlConfiguration> chapterDataMap = new HashMap<>();
                chapterDataMap.put(chapterData, chapter);
                chapters.put(Objects.requireNonNull(chapter.getString("chapterInfo.name")), chapterDataMap);

            } else {
                narrator.getLogger().log(Level.SEVERE, "Please check your chapter folder all files chapterInfo name");
                narrator.getLogger().log(Level.SEVERE, "There have same name!");
                narrator.getLogger().log(Level.SEVERE, "Here are some information may help you:");
                narrator.getLogger().log(Level.SEVERE, "        sameName:" + chapter.getString("chapterInfo.name"));
                narrator.getLogger().log(Level.SEVERE, "        ordinal:" + chapter.getString("chapterInfo.ordinal"));
                narrator.getLogger().log(Level.SEVERE, "        ordinal:" + chapters.get(chapter.getString("chapterInfo.name")));
            }

        }


    }

}
