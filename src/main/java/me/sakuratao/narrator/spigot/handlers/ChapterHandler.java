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

    public void load(boolean reload) {

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
                            chapter.getString("chapterInfo.ordinal") == null
            ) {
                narrator.getLogger().log(Level.SEVERE, "Please check your chapter file " + chapterFile.getName());
                narrator.getLogger().log(Level.SEVERE, "These options can't be null, the Version must be double and the Ordinal must be int");
                return;
            }

            try {

                if (!chapters.containsKey(chapter.getString("chapterInfo.name")) || reload) {

                    String name = chapter.getString("chapterInfo.name");
                    String author = chapter.getString("chapterInfo.author");
                    double version = Double.parseDouble(Objects.requireNonNull(chapter.getString("chapterInfo.version")));
                    int ordinal = Integer.parseInt(Objects.requireNonNull(chapter.getString("chapterInfo.ordinal")));

                    if (chapters.containsKey(name)) {

                        ChapterData sameChapter = chapters.get(name).keySet().iterator().next();

                        if (version <= sameChapter.getVersion()){
                            narrator.getLogger().log(Level.SEVERE, "There is already a chapter with the name " + name + ", and it is higher version");
                            narrator.getLogger().log(Level.SEVERE, "We won't load the lower version");
                            narrator.getLogger().log(Level.SEVERE, "Here are some information may help you:");
                            narrator.getLogger().log(Level.SEVERE, "        Name: " + name);
                            narrator.getLogger().log(Level.SEVERE, "        Higher Version: " + sameChapter.getVersion());
                            narrator.getLogger().log(Level.SEVERE, "        Older Version: " + version);
                            return;
                        } else {
                            narrator.getLogger().log(Level.SEVERE, "There is already a chapter with the name " + name + ", and it is lower version");
                            narrator.getLogger().log(Level.SEVERE, "We will load the higher version");
                            narrator.getLogger().log(Level.SEVERE, "Here are some information may help you:");
                            narrator.getLogger().log(Level.SEVERE, "        Name: " + name);
                            narrator.getLogger().log(Level.SEVERE, "        Higher Version: " + version);
                            narrator.getLogger().log(Level.SEVERE, "        Older Version: " + sameChapter.getVersion());
                        }

                    }

                    ChapterData chapterData = new ChapterData();
                    chapterData.setName(name);
                    chapterData.setAuthor(author);
                    chapterData.setVersion(version);
                    chapterData.setOrdinal(ordinal);

                    Map<ChapterData, YamlConfiguration> chapterDataMap = new HashMap<>();
                    chapterDataMap.put(chapterData, chapter);
                    chapters.put(name, chapterDataMap);

                } else {

                    ChapterData sameChapter = chapters.get(chapter.getString("chapterInfo.name")).keySet().iterator().next();

                    narrator.getLogger().log(Level.SEVERE, "There have same name!");
                    narrator.getLogger().log(Level.SEVERE, "Please check your chapter folder all files with chapterInfo name");
                    narrator.getLogger().log(Level.SEVERE, "Here are some information may help you:");
                    narrator.getLogger().log(Level.SEVERE, "        sameName: " + chapter.getString("chapterInfo.name"));
                    narrator.getLogger().log(Level.SEVERE, "        Ordinal: " + chapter.getString("chapterInfo.ordinal") + " | Version: " + chapter.getString("chapterInfo.version"));
                    narrator.getLogger().log(Level.SEVERE, "        Ordinal: " + sameChapter.getOrdinal() + " | Version: " + sameChapter.getVersion());
                    return;

                }

            } catch (NumberFormatException e){
                narrator.getLogger().log(Level.SEVERE, "Please check your chapter file " + chapterFile.getName());
                narrator.getLogger().log(Level.SEVERE, "The Version must be double and the Ordinal must be int");
            }

        }


    }

}
