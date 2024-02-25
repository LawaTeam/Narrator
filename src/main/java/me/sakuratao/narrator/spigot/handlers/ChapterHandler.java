package me.sakuratao.narrator.spigot.handlers;

import lombok.Getter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.NarratorSpigot;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import org.bukkit.configuration.file.YamlConfiguration;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

@PieComponent
public class ChapterHandler {

    @Wire
    private Narrator narrator;

    @Getter
    private List<Map<ChapterData, YamlConfiguration>> sortedChapters;
    @Getter
    private final ConcurrentHashMap<String, Map<ChapterData, YamlConfiguration>> chapters = new ConcurrentHashMap<>();


    public void load(boolean reload, boolean force) {

        File path = new File(narrator.getWorkFolder().getPath() + "/chapters");

        if (!path.exists()) {
            path.mkdirs();

            try {
                InputStreamReader inputStreamReader = new InputStreamReader(NarratorSpigot.getPluginInstance().getResource("ChapterExample.yml"));
                YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(inputStreamReader);
                yamlConfiguration.save(new File(path.getPath() + "/ChapterExample.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            narrator.getLogger().log(Level.WARNING, "Chapter folder created");
            narrator.getLogger().log(Level.WARNING, "We put an example chapter in there, please edit it as your first chapter!");
            narrator.getLogger().log(Level.WARNING, "Or put already edited chapter files in the folder and use /nr to reload");
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
                narrator.getLogger().log(Level.SEVERE, "Check your options in chapterInfo about Name, Author, Version and Ordinal, they can't be null.");
                return;
            }

            try {

                if (!chapters.containsKey(chapter.getString("chapterInfo.name")) || reload) {

                    String name = chapter.getString("chapterInfo.name");
                    String author = chapter.getString("chapterInfo.author");
                    double version = Double.parseDouble(Objects.requireNonNull(chapter.getString("chapterInfo.version")));
                    int ordinal = Integer.parseInt(Objects.requireNonNull(chapter.getString("chapterInfo.ordinal")));

                    if (ordinal < 1) {
                        narrator.getLogger().log(Level.SEVERE, "All ordinal must be over 1!");
                        narrator.getLogger().log(Level.SEVERE, "Here are some information may help you:");
                        narrator.getLogger().log(Level.SEVERE, "        Chapter Name: " + name);
                        return;
                    }

                    if (chapters.containsKey(name) && !force) {

                        ChapterData sameChapter = chapters.get(name).keySet().iterator().next();

                        /*
                            版本冲突
                         */
                        if (version < sameChapter.getVersion()){
                            narrator.getLogger().log(Level.SEVERE, "There is already a chapter with the name " + name + ", and it is higher version");
                            narrator.getLogger().log(Level.SEVERE, "We won't load the lower version");
                            narrator.getLogger().log(Level.SEVERE, "If you want to reload this chapter, please use /nr force");
                            narrator.getLogger().log(Level.SEVERE, "Here are some information may help you:");
                            narrator.getLogger().log(Level.SEVERE, "        Name: " + name);
                            narrator.getLogger().log(Level.SEVERE, "        Higher Version: " + sameChapter.getVersion());
                            narrator.getLogger().log(Level.SEVERE, "        Older Version: " + version);
                            continue;
                        } else if (version > sameChapter.getVersion()){
                            narrator.getLogger().log(Level.WARNING, "There is already a chapter with the name " + name + ", and it is lower version");
                            narrator.getLogger().log(Level.WARNING, "We will load the higher version");
                            narrator.getLogger().log(Level.WARNING, "Here are some information may help you:");
                            narrator.getLogger().log(Level.WARNING, "        Name: " + name);
                            narrator.getLogger().log(Level.WARNING, "        Higher Version: " + version);
                            narrator.getLogger().log(Level.WARNING, "        Older Version: " + sameChapter.getVersion());
                        }

                    }

                    /*
                        ordinal 索引冲突
                     */
                    chapters.values().forEach(map -> {
                        ChapterData data = map.keySet().iterator().next();
                        if (data.getOrdinal() == ordinal && !data.getName().equalsIgnoreCase(name)){
                            narrator.getLogger().log(Level.SEVERE, "There is an ordinal conflict about chapters");
                            narrator.getLogger().log(Level.SEVERE, "Here are some information may help you:");
                            narrator.getLogger().log(Level.SEVERE, "        Name: " + name);
                            narrator.getLogger().log(Level.SEVERE, "        Name: " + data.getName());
                            narrator.getLogger().log(Level.SEVERE, "        Ordinal: " + ordinal);
                        }
                    });

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

                    narrator.getLogger().log(Level.SEVERE, "There have same chapter!");
                    narrator.getLogger().log(Level.SEVERE, "Please check your chapter folder all files with chapterInfo name");
                    narrator.getLogger().log(Level.SEVERE, "Here are some information may help you:");
                    narrator.getLogger().log(Level.SEVERE, "        sameName: " + chapter.getString("chapterInfo.name"));
                    narrator.getLogger().log(Level.SEVERE, "        Ordinal: " + chapter.getString("chapterInfo.ordinal") + " | Version: " + chapter.getString("chapterInfo.version"));
                    narrator.getLogger().log(Level.SEVERE, "        Ordinal: " + sameChapter.getOrdinal() + " | Version: " + sameChapter.getVersion());
                    narrator.getLogger().log(Level.SEVERE, "Load stopped!");
                    return;
                }

            } catch (NumberFormatException e){
                narrator.getLogger().log(Level.SEVERE, "Please check your chapter file " + chapterFile.getName());
                narrator.getLogger().log(Level.SEVERE, "The Version must be double and the Ordinal must be int");
                narrator.getLogger().log(Level.SEVERE, "Load stopped!");
                return;
            }

        }

        sortedChapters = chapters.values().stream()
                .sorted(Comparator.comparingInt((Map<ChapterData, YamlConfiguration> chapter) -> chapter.keySet().stream().iterator().next().getOrdinal()))
                .collect(Collectors.toList());

    }

    public ChapterData jump(int ordinal){
        return null;
    }

}
