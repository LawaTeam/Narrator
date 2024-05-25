package me.sakuratao.narrator.spigot.handlers;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.NarratorSpigot;
import me.sakuratao.narrator.spigot.configuration.Lang;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.data.player.PlayerData;
import me.sakuratao.narrator.spigot.utils.LogUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

@PieComponent
public class ChapterHandler {

    @Wire
    private Narrator narrator;

    private final ConcurrentHashMap<String, List<Map<ChapterData, YamlConfiguration>>> langChapters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<String>> totalChapters = new ConcurrentHashMap<>();


    public void load(boolean force) {

        File path = new File(narrator.getWorkFolder().getPath() + "/chapters");

        if (isValidChapterFolder(path)) {
            return;
        }

        for (File chapterFile : path.listFiles()) {
            YamlConfiguration chapter = YamlConfiguration.loadConfiguration(chapterFile);
            if (isSectionsNull(chapterFile, chapter)) {
                return;
            }

            try {

                String name = chapter.getString("chapterInfo.name");
                String author = chapter.getString("chapterInfo.author");
                double version = Double.parseDouble(Objects.requireNonNull(chapter.getString("chapterInfo.version")));
                int ordinal = Integer.parseInt(Objects.requireNonNull(chapter.getString("chapterInfo.ordinal")));
                String lang = chapter.getString("chapterInfo.lang").toLowerCase();

                if (isOrdinalLowerThanOne(ordinal, name)) {
                    return;
                }

                ChapterData chapterData = createData(name, author, version, ordinal, lang.toLowerCase());

                Map<ChapterData, YamlConfiguration> dataMap = new HashMap<>();
                dataMap.put(chapterData, chapter);

                if (getTotal(lang) == null || getLangChapterMaps(lang) == null) {
                    putLang(dataMap, getDataByMap(dataMap));
                } else {

                    // 判断同名版本
                    if (getTotal(lang).contains(name)) {
                        ChapterData equalData = getDataByLang(name, lang);
                        if (equalData != null) {
                            // 是否存在版本冲突
                            if (version < equalData.getVersion()) {
                                if (force) {
                                    putLang(dataMap, getDataByMap(dataMap));
                                    continue;
                                }
                                logVersionHigher(name, equalData.getVersion(), version);
                                continue;
                            } else if (version > equalData.getVersion()) {
                                logVersionLower(name, version, equalData.getVersion());
                                continue;
                            } else {
                                if (force) {
                                    putLang(dataMap, getDataByMap(dataMap));
                                    continue;
                                }
                                logVersionEqual(name, ordinal, equalData.getOrdinal(), version, equalData.getVersion());
                                continue;
                            }
                        }
                    }

                    if (isOrdinalConflicted(lang, name, ordinal)){
                        return;
                    }

                    putLang(dataMap, getDataByMap(dataMap));

                }
            } catch (NumberFormatException e){
                logNumberFormat(chapterFile);
                LogUtil.log(Level.SEVERE, "Details(For Developments): " + e.getMessage());
                return;
            }
        }

        sortLang();

    }

    /**
     * 检查是否存在 ordinal 冲突
     * @param lang - 语言
     * @param name - 章节名
     * @param ordinal - 序数
     * @return 是否存在冲突
     */
    private boolean isOrdinalConflicted(String lang, String name, int ordinal){
        AtomicBoolean conflicted = new AtomicBoolean(false);
        getLangChapterData(lang).forEach(data -> {
            if (data.getOrdinal() == ordinal && !data.getName().equalsIgnoreCase(name)) {
                logOrdinalConflicted(name, data, ordinal);
                conflicted.set(true);
            }
        });
        return conflicted.get();
    }

    /**
     * 输出 数字格式 冲突日志
     * @param chapterFile - 文件
     */
    private void logNumberFormat(File chapterFile){
        LogUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_NUMBER_FORMAT);
        LogUtil.log(Level.SEVERE, Lang.CHAPTERS_CONSOLE_HELP);
        LogUtil.log(Level.SEVERE, "        chapterFile: " + chapterFile.getName());
        LogUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_LOAD_STOP);
    }

    /**
     * 输出 ordinal 冲突日志
     * @param name - 章节名
     * @param data - 章节数据
     * @param ordinal - 序数
     */
    private void logOrdinalConflicted(String name, ChapterData data, int ordinal){
        LogUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_ORDINAL_CONFLICT);
        LogUtil.log(Level.SEVERE, Lang.CHAPTERS_CONSOLE_HELP);
        LogUtil.log(Level.SEVERE, "        chapterName: " + name);
        LogUtil.log(Level.SEVERE, "        chapterName: " + data.getName());
        LogUtil.log(Level.SEVERE, "        equalOrdinal: " + ordinal);
    }

    /**
     * 输出 高版本号 冲突日志
     * @param name - 章节名
     * @param higherVersion - 更高的版本号
     * @param lowerVersion - 更低的版本号
     */
    private void logVersionHigher(String name, double higherVersion, double lowerVersion){
        LogUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_VERSION_HIGHER);
        LogUtil.log(Level.SEVERE, Lang.CHAPTERS_CONSOLE_HELP);
        LogUtil.log(Level.SEVERE, "        chapterName: " + name);
        LogUtil.log(Level.SEVERE, "        higherVersion: " + higherVersion);
        LogUtil.log(Level.SEVERE, "        lowerVersion: " + lowerVersion);
    }

    /**
     * 输出 低版本号 冲突日志
     * @param name - 章节名
     * @param higherVersion - 更高的版本号
     * @param lowerVersion - 更低的版本号
     */
    private void logVersionLower(String name, double higherVersion, double lowerVersion){
        LogUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_VERSION_LOWER);
        LogUtil.log(Level.SEVERE, Lang.CHAPTERS_CONSOLE_HELP);
        LogUtil.log(Level.SEVERE, "        chapterName: " + name);
        LogUtil.log(Level.SEVERE, "        higherVersion: " + higherVersion);
        LogUtil.log(Level.SEVERE, "        lowerVersion: " + lowerVersion);
    }

    /**
     * 输出 同版本号 冲突日志
     * @param name - 章节名
     * @param ordinal1 - 冲突序数1
     * @param ordinal2 - 冲突序数2
     * @param version1 - 冲突版本1
     * @param version2 - 冲突版本2
     */
    private void logVersionEqual(String name, int ordinal1, int ordinal2, double version1, double version2){
        LogUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_NAME_EQUAL);
        LogUtil.log(Level.SEVERE, Lang.CHAPTERS_CONSOLE_HELP);
        LogUtil.log(Level.SEVERE, "        equalName: " + name);
        LogUtil.log(Level.SEVERE, "        Ordinal: " + ordinal1 + " | Version: " + version1);
        LogUtil.log(Level.SEVERE, "        Ordinal: " + ordinal2 + " | Version: " + version2);
        LogUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_LOAD_STOP);
    }

    /**
     * 检查章节文件夹是否存在
     * @param path - 路径
     * @return 存在与否
     */
    private boolean isValidChapterFolder(File path) {
        if (!path.exists()) {
            path.mkdirs();

            try {
                InputStreamReader inputStreamReader = new InputStreamReader(NarratorSpigot.getPluginInstance().getResource("ChapterExample.yml"));
                YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(inputStreamReader);
                yamlConfiguration.save(new File(path.getPath() + "/ChapterExample.yml"));
            } catch (IOException e) {
                LogUtil.log(Level.SEVERE, "Couldn't create chapterExample.");
                LogUtil.log(Level.SEVERE, "Details(For Developments): " + e.getMessage());
            }

            LogUtil.log(Level.WARNING, Lang.CHAPTERS_FOLDER_CREATED);
            return true;
        }
        return false;
    }

    /**
     * 对章节 按语言 进行分类
     */
    private void sortLang(){
        for (String lang : langChapters.keySet()) {

            langChapters.computeIfPresent(
                    lang, (k, langChapter) -> langChapter.stream()
                            .sorted(Comparator.comparingInt(
                                    (Map<ChapterData, YamlConfiguration> chapter) -> chapter.keySet().stream().iterator().next().getOrdinal())
                            )
                            .toList()
            );

        }
    }

    /**
     * 创建章节 data
     * @param name - 章节名
     * @param author - 作者名
     * @param version - 版本
     * @param ordinal - 序号
     * @param lang - 语言
     * @return 处理好的 data
     */
    private ChapterData createData(String name, String author, double version, int ordinal, String lang){
        ChapterData chapterData = new ChapterData();
        chapterData.setName(name);
        chapterData.setAuthor(author);
        chapterData.setVersion(version);
        chapterData.setOrdinal(ordinal);
        chapterData.setLang(lang.toLowerCase());
        return chapterData;
    }

    /**
     * 检查相关项是否有一个为空
     * @param chapter - 需要检查的yaml
     * @return 是否有一个为空
     */
    private boolean isSectionsNull(File chapterFile, YamlConfiguration chapter){

        if (chapter.getString("chapterInfo.name") == null ||
                chapter.getString("chapterInfo.author") == null ||
                chapter.getString("chapterInfo.version") == null ||
                chapter.getString("chapterInfo.ordinal") == null ||
                chapter.getString("chapterInfo.lang") == null) {
            LogUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_NULL);
            LogUtil.log(Level.SEVERE, Lang.CHAPTERS_CONSOLE_HELP);
            LogUtil.log(Level.SEVERE, "        chapterFile: " + chapterFile.getName());
            return true;
        }

        return false;
    }

    /**
     * 检查 ordinal 是否小于 1
     * @param ordinal - 序数
     * @param name - 章节名
     * @return 是否小于 1
     */
    private boolean isOrdinalLowerThanOne(int ordinal, String name){
        if (ordinal >= 1) return false;

        LogUtil.log(Level.SEVERE, Lang.CHAPTERS_FOLDER_CHECK_NUMBER_FORMAT);
        LogUtil.log(Level.SEVERE, Lang.CHAPTERS_CONSOLE_HELP);
        LogUtil.log(Level.SEVERE, "        chapterName: " + name);
        return true;
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

        if (chapters.contains(name)) return;

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

        langSort.removeIf(map -> getDataByMap(map).getName().equalsIgnoreCase(chapterData.getName()));

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
        return totalChapters.containsKey(lang.toLowerCase());
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
     * 通过 playerData 获取章节数据
     * @param data - 玩家数据
     * @return chapterData
     */
    public ChapterData getDataByPlayerData(PlayerData data){
        return getDataByOrdinal(data.getPlayingChapterOrdinal(), data.getLang());
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
     * @return 与该 ordinal 匹配的章节
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

    /**
     * 转跳章节 (玩家现有语言)
     * @param ordinal - 章节序数
     */
    public void jump(PlayerData playerData, int ordinal){
        playerData.setPlayingChapterOrdinal(ordinal);
        playerData.setPlayingTaskOrdinal(0);
        playerData.setContentIndex(0);
    }

}
