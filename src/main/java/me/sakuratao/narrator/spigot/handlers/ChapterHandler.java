package me.sakuratao.narrator.spigot.handlers;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.configuration.Lang;
import me.sakuratao.narrator.spigot.NarratorSpigot;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.utils.ServerUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

@PieComponent
public class ChapterHandler {

    @Wire
    private Narrator narrator;

    private final ConcurrentHashMap<String, List<Map<ChapterData, YamlConfiguration>>> langChapters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<String>> totalChapters = new ConcurrentHashMap<>();


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

            ServerUtil.log(Level.WARNING, Lang.CHAPTERS_FOLDER_CREATED);
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
                ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_NULL);
                ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_HELP);
                ServerUtil.log(Level.SEVERE, "        chapterFile: " + chapterFile.getName());
                return;
            }

            try {

                String name = chapter.getString("chapterInfo.name");
                String author = chapter.getString("chapterInfo.author");
                double version = Double.parseDouble(Objects.requireNonNull(chapter.getString("chapterInfo.version")));
                int ordinal = Integer.parseInt(Objects.requireNonNull(chapter.getString("chapterInfo.ordinal")));
                String lang = chapter.getString("chapterInfo.lang").toLowerCase();

                if (ordinal < 1) {
                    ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_NUMBER_FORMAT);
                    ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_HELP);
                    ServerUtil.log(Level.SEVERE, "        chapterName: " + name);
                    return;
                }

                ChapterData chapterData = new ChapterData();
                chapterData.setName(name);
                chapterData.setAuthor(author);
                chapterData.setVersion(version);
                chapterData.setOrdinal(ordinal);
                chapterData.setLang(lang.toLowerCase());

                Map<ChapterData, YamlConfiguration> dataMap = new HashMap<>();
                dataMap.put(chapterData, chapter);

                if (getTotal(lang) == null || getLangChapterMaps(lang) == null) {

                    putLang(dataMap, getDataByMap(dataMap));

                } else {

                    if (getTotal(lang).contains(name)) {
                        ChapterData equalData = getDataByLang(name, lang);

                        if (equalData != null) {
                             /*
                                版本冲突
                             */
                            if (version < equalData.getVersion()) {
                                if (force) {
                                    putLang(dataMap, getDataByMap(dataMap));
                                    continue;
                                }
                                ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_VERSION_HIGHER);
                                ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_HELP);
                                ServerUtil.log(Level.SEVERE, "        chapterName: " + name);
                                ServerUtil.log(Level.SEVERE, "        higherVersion: " + equalData.getVersion());
                                ServerUtil.log(Level.SEVERE, "        olderVersion: " + version);
                                continue;
                            } else if (version > equalData.getVersion()) {
                                ServerUtil.log(Level.WARNING, Lang.CHAPTERS_FOLDER_CHECK_VERSION_LOWER);
                                ServerUtil.log(Level.WARNING, Lang.CHAPTERS_FOLDER_CHECK_HELP);
                                ServerUtil.log(Level.WARNING, "        chapterName: " + name);
                                ServerUtil.log(Level.WARNING, "        higherVersion: " + version);
                                ServerUtil.log(Level.WARNING, "        olderVersion: " + equalData.getVersion());
                                continue;
                            } else {
                                if (force) {
                                    putLang(dataMap, getDataByMap(dataMap));
                                    continue;
                                }
                                ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_NAME_SAME);
                                ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_HELP);
                                ServerUtil.log(Level.SEVERE, "        equalName: " + name);
                                ServerUtil.log(Level.SEVERE, "        Ordinal: " + ordinal + " | Version: " + version);
                                ServerUtil.log(Level.SEVERE, "        Ordinal: " + equalData.getOrdinal() + " | Version: " + equalData.getVersion());
                                ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_LOAD_STOP);
                                continue;
                            }

                        }
                    }

                    /*
                        检查同语言间的 ordinal 索引是否存在冲突
                     */
                    getLangChapterData(lang).forEach(data -> {
                        if (data.getOrdinal() == ordinal && !data.getName().equalsIgnoreCase(name)) {
                            ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_ORDINAL_CONFLICT);
                            ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_HELP);
                            ServerUtil.log(Level.SEVERE, "        chapterName: " + name);
                            ServerUtil.log(Level.SEVERE, "        chapterName: " + data.getName());
                            ServerUtil.log(Level.SEVERE, "        equalOrdinal: " + ordinal);
                            throw new RuntimeException("");
                        }
                    });

                    putLang(dataMap, getDataByMap(dataMap));

                }
            } catch (NumberFormatException e){
                ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_NUMBER_FORMAT);
                ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_HELP);
                ServerUtil.log(Level.SEVERE, "        chapterFile: " + chapterFile.getName());
                ServerUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_LOAD_STOP);
                return;
            }
        }

        sortLang();

    }

    /**
     * 对章节 按语言 进行分类
     */
    private void sortLang(){
        for (String lang : langChapters.keySet()) {

            List<Map<ChapterData, YamlConfiguration>> langChapter = langChapters.get(lang);

            langChapters.put(
                    lang,
                    langChapter.stream()
                            .sorted(Comparator.comparingInt((Map<ChapterData, YamlConfiguration> chapter) -> chapter.keySet().stream().iterator().next().getOrdinal()))
                            .toList()
            );

        }
    }

    /**
     * 在总章节列表中添加章节
     * @param lang - 章节所属语言
     * @param name - 章节名
     */
    private void putTotal(String lang, String name){

        List<String> chapters = new ArrayList<>();
        if (totalChapters.containsKey(lang)) {
            chapters = new ArrayList<>(totalChapters.get(lang));
        }
        chapters.add(name);
        totalChapters.put(lang, chapters);

    }

    /**
     * 在语言分类中添加章节
     * @param dataMap - 章节数据
     * @param chapterData - 章节
     */
    private void putLang(Map<ChapterData, YamlConfiguration> dataMap, ChapterData chapterData){

        List<Map<ChapterData, YamlConfiguration>> langSort = new ArrayList<>();
        if (langChapters.containsKey(chapterData.getLang())) {
            langSort = new ArrayList<>(langChapters.get(chapterData.getLang()));
        }
        langSort.add(dataMap);
        langChapters.put(chapterData.getLang(), langSort);

        putTotal(chapterData.getLang(), chapterData.getName());

    }


    /**
     * 是否存在此语言
     * @param lang - 语言名
     * @return 该语言是否存在
     */
    public boolean isLangExists(String lang){
        return langChapters.containsKey(lang.toLowerCase());
    }

    /**
     * 是否存在此章节
     * @param chapterName - 章节名
     * @param lang - 语言
     * @return 是否存在
     */
    public boolean isDataExists(String chapterName, String lang){
        return getTotal(lang).contains(chapterName);
    }

    /**
     * 是否存在相同的章节
     * @param data - 要对比的章节
     * @param lang - 语言
     * @return 是否存在相同的章节
     */
    public boolean isDataEquals(ChapterData data, String lang){
        return getDataEquals(data, lang) != null;
    }

    /**
     * 获取指定语言的所有章节数据
     * @param lang - 语言
     * @return 关于该语言的章节
     */
    public List<ChapterData> getLangChapterData(String lang){
        List<ChapterData> dataList = new ArrayList<>();
        for (Map<ChapterData, YamlConfiguration> map : getLangChapterMaps(lang)) {
            dataList.add(map.keySet().iterator().next());
        }
        return dataList;
    }

    /**
     * 获取指定语言的所有章节的 Data 与 Yaml
     * @param lang - 语言
     * @return 关于该语言的章节
     */
    public List<Map<ChapterData, YamlConfiguration>> getLangChapterMaps(String lang){
        return langChapters.get(lang.toLowerCase());
    }

    /**
     * 通过 Map 转换得到 Data
     * @param map - 需要转换的 map
     * @return 转换得到的 Data
     */
    public ChapterData getDataByMap(Map<ChapterData, YamlConfiguration> map){
        return map.keySet().iterator().next();
    }

    /**
     * 获取相同的章节
     * @param data - 要对比的章节
     * @param lang - 语言
     * @return 关于该语言的章节
     */
    public ChapterData getDataEquals(ChapterData data, String lang){
        for (ChapterData equal : getLangChapterData(lang.toLowerCase())){
            if (equal.getName().equalsIgnoreCase(data.getName())) {
                return equal;
            }
        }
        return null;
    }

    /**
     * 通过 ordinal 获取章节
     * @param ordinal - 指定的 ordinal
     * @param lang - 指定的语言
     * @return
     */
    public ChapterData getDataByOrdinal(int ordinal, String lang) {
        for (ChapterData data : getLangChapterData(lang.toLowerCase())){
            if (data.getOrdinal() == ordinal) return data;
        }
        return null;
    }

    /**
     * 通过指定语言与章节名获取章节
     * @param chapterName - 章节名
     * @param lang - 语言
     * @return 指定章节
     */
    public ChapterData getDataByLang(String chapterName, String lang) {
        for (ChapterData data : getLangChapterData(lang.toLowerCase())) {
            if (data.getName().equalsIgnoreCase(chapterName)) {
                return data;
            }
        }
        return null;
    }

    /**
     * 获取关于此语言所包含的所有章节名字
     * @param lang - 语言
     * @return 关于该语言的章节名字
     */
    public List<String> getTotal(String lang){
        return totalChapters.get(lang.toLowerCase());
    }

    /**
     * 获取所有语言
     * @return 所有语言
     */
    public List<String> getTotalLang(){
        return totalChapters.keySet().stream().toList();
    }

    /**
     * 清空 total 以及 lang
     */
    public void clear(){
        totalChapters.clear();
        langChapters.clear();
    }

    public ChapterData jump(int ordinal){
        return null;
    }

}
