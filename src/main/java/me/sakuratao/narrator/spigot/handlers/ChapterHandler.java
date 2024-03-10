package me.sakuratao.narrator.spigot.handlers;

import lombok.Getter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.configuration.Lang;
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
    private ConcurrentHashMap<String, List<Map<ChapterData, YamlConfiguration>>> langSortedChapters = new ConcurrentHashMap<>(); // fixme langSorted 与 chapters 里存的 chapterdata 不一致
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

            narrator.getLogger().log(Level.WARNING, Lang.CHAPTERS_FOLDER_CREATED);
            narrator.getLogger().log(Level.WARNING, Lang.CHAPTERS_FOLDER_EDIT);
            narrator.getLogger().log(Level.WARNING, Lang.CHAPTERS_FOLDER_RELOAD);
            return;
        }

        for (File chapterFile : Objects.requireNonNull(path.listFiles())) {

            YamlConfiguration chapter = YamlConfiguration.loadConfiguration(chapterFile);

            if (
                    chapter.getString("chapterInfo.name") == null ||
                            chapter.getString("chapterInfo.author") == null ||
                            chapter.getString("chapterInfo.version") == null ||
                            chapter.getString("chapterInfo.ordinal") == null ||
                            chapter.getString("chapterInfo.lang") == null
            ) {
                narrator.getLogger().log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK + chapterFile.getName());
                narrator.getLogger().log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_NULL);
                return;
            }

            try {

                if (!chapters.containsKey(chapter.getString("chapterInfo.name")) || reload) {

                    String name = chapter.getString("chapterInfo.name");
                    String author = chapter.getString("chapterInfo.author");
                    double version = Double.parseDouble(Objects.requireNonNull(chapter.getString("chapterInfo.version")));
                    int ordinal = Integer.parseInt(Objects.requireNonNull(chapter.getString("chapterInfo.ordinal")));
                    String lang = chapter.getString("chapterInfo.lang").toLowerCase();

                    if (ordinal < 1) {
                        narrator.getLogger().log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_ORDINAL_OVER_ONE);
                        narrator.getLogger().log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_HELP);
                        narrator.getLogger().log(Level.SEVERE, "        Chapter Name: " + name);
                        return;
                    }

                    if (chapters.containsKey(name) && !force) {

                        ChapterData sameChapter = chapters.get(name).keySet().iterator().next();

                        if (sameChapter.getLang().equalsIgnoreCase(lang)) {
                         /*
                            版本冲突
                         */
                            if (version < sameChapter.getVersion()){
                                narrator.getLogger().log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_VERSION_HIGHER.replace("%chapterName%", name));
                                narrator.getLogger().log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_VERSION_HIGHER_WONT_LOAD);
                                narrator.getLogger().log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_VERSION_HIGHER_FORCE);
                                narrator.getLogger().log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_HELP);
                                narrator.getLogger().log(Level.SEVERE, "        Name: " + name);
                                narrator.getLogger().log(Level.SEVERE, "        Higher Version: " + sameChapter.getVersion());
                                narrator.getLogger().log(Level.SEVERE, "        Older Version: " + version);
                                continue;
                            } else if (version > sameChapter.getVersion()){
                                narrator.getLogger().log(Level.WARNING, Lang.CHAPTERS_FOLDER_CHECK_VERSION_LOWER.replace("%chapterName%", name));
                                narrator.getLogger().log(Level.WARNING, Lang.CHAPTERS_FOLDER_CHECK_VERSION_LOWER_LOADED);
                                narrator.getLogger().log(Level.WARNING, Lang.CHAPTERS_FOLDER_CHECK_HELP);
                                narrator.getLogger().log(Level.WARNING, "        Name: " + name);
                                narrator.getLogger().log(Level.WARNING, "        Higher Version: " + version);
                                narrator.getLogger().log(Level.WARNING, "        Older Version: " + sameChapter.getVersion());
                            }
                        }

                    }

                    /*
                        ordinal 索引冲突
                     */
                    chapters.values().forEach(map -> {
                        ChapterData data = map.keySet().iterator().next();
                        if (data.getLang().equalsIgnoreCase(lang) && data.getOrdinal() == ordinal && !data.getName().equalsIgnoreCase(name)){
                            narrator.getLogger().log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_ORDINAL_CONFLICT);
                            narrator.getLogger().log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_HELP);
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
                    chapterData.setLang(lang);

                    Map<ChapterData, YamlConfiguration> dataMap = new HashMap<>();
                    dataMap.put(chapterData, chapter);

                    addLangSort(dataMap, dataMap.keySet().iterator().next());
                    chapters.put(name, dataMap);

                } else {
                    ChapterData sameChapter = chapters.get(chapter.getString("chapterInfo.name")).keySet().iterator().next();
                    narrator.getLogger().log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK);
                    narrator.getLogger().log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_NAME_SAME);
                    narrator.getLogger().log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_HELP);
                    narrator.getLogger().log(Level.SEVERE, "        sameName: " + chapter.getString("chapterInfo.name"));
                    narrator.getLogger().log(Level.SEVERE, "        Ordinal: " + chapter.getString("chapterInfo.ordinal") + " | Version: " + chapter.getString("chapterInfo.version"));
                    narrator.getLogger().log(Level.SEVERE, "        Ordinal: " + sameChapter.getOrdinal() + " | Version: " + sameChapter.getVersion());
                    narrator.getLogger().log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_LOAD_STOP);
                    return;
                }
            } catch (NumberFormatException e){
                narrator.getLogger().log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK + chapterFile.getName());
                narrator.getLogger().log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_VERSION);
                narrator.getLogger().log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_ORDINAL);
                narrator.getLogger().log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_LOAD_STOP);
                return;
            }
        }

        sortLangChapter();

    }

    private void sortLangChapter(){
        for (String lang : langSortedChapters.keySet()) {

            List<Map<ChapterData, YamlConfiguration>> langChapter = langSortedChapters.get(lang);

            langSortedChapters.put(
                    lang, langChapter.stream()
                            .sorted(Comparator.comparingInt((Map<ChapterData, YamlConfiguration> chapter) -> chapter.keySet().stream().iterator().next().getOrdinal()))
                            .toList()
            );

        }
    }

    private void addLangSort(Map<ChapterData, YamlConfiguration> dataMap, ChapterData chapterData){
        List<Map<ChapterData, YamlConfiguration>> langSort = new ArrayList<>();
        if (langSortedChapters.containsKey(chapterData.getLang())) {
            langSort = new ArrayList<>(langSortedChapters.get(chapterData.getLang()));
        }
        langSort.add(dataMap);
        langSortedChapters.put(chapterData.getLang(), langSort);
    }

    public boolean isLangExists(String lang){
        return langSortedChapters.containsKey(lang.toLowerCase());
    }

    public boolean isSameDataExists(ChapterData data, String lang){
        return getSameChapterData(data, lang) != null;
    }

    public List<Map<ChapterData, YamlConfiguration>> getLangChapters(String lang){
        return langSortedChapters.get(lang.toLowerCase());
    }

    public ChapterData getSameChapterData(ChapterData data, String lang){
        for (Map<ChapterData, YamlConfiguration> map : getLangChapters(data.getLang())) {
            ChapterData data1 = map.keySet().iterator().next();
            if (data1.getName().equalsIgnoreCase(data.getName())) {
                return data1;
            }
        }
        return null;
    }

    public ChapterData jump(int ordinal){
        return null;
    }

}
