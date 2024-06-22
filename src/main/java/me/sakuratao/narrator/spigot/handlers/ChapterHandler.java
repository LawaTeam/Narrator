package me.sakuratao.narrator.spigot.handlers;

import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.NarratorSpigot;
import me.sakuratao.narrator.spigot.configuration.lang.LangPlugin;
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


    /**
     * 加载章节数据。
     * 根据是否强制加载的标志，决定是否重新加载章节内容。
     * 此方法首先检查是否存在有效的章节目录，如果存在则不进行任何操作。
     * 如果目录无效或不存在，则遍历章节目录下的所有文件和子目录，分别处理文件和子目录中的章节内容。
     * 最后，对加载的章节内容进行语言排序。
     *
     * @param isForce 强制加载标志，决定是否忽略现有缓存并重新加载章节。
     */
    public void load(boolean isForce) {

        // 获取 narrator 的工作目录路径。
        String basePath = narrator.getWorkFolder().getPath();
        // 构建章节主目录的文件对象。
        File mainPath = new File(basePath + "/chapters");
        // 如果章节主目录无效，则不进一步加载。
        if (!isValidChapterFolder(mainPath)) {
            return;
        }

        // 遍历章节主目录下的所有文件和子目录。
        for (File files : mainPath.listFiles()) {

            // 如果是子目录，则处理该子目录下的文件。
            if (files.isDirectory()) {
                filesInFolder(files, isForce);
                continue;
            }

            // 如果是文件，则加载该文件中的章节内容。
            loadChapter(files, isForce);
        }

        // 对加载的章节内容按语言进行排序。
        sortLang();

    }


    private void filesInFolder(File folder, boolean isForce) {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                filesInFolder(file, isForce);
                continue;
            }
            loadChapter(file, isForce);
        }
    }

    /**
     * 加载章节配置文件。
     *
     * @param chapterFile 章节配置文件，用于加载章节信息。
     * @param isForce 是否强制覆盖已存在的章节数据。
     */
    private void loadChapter(File chapterFile, boolean isForce) {
        // 如果章节文件为空，则直接返回。
        if (chapterFile == null) return;

        // 加载章节配置文件。
        YamlConfiguration chapter = YamlConfiguration.loadConfiguration(chapterFile);
        // 检查章节配置是否有效，如果无效则返回。
        if (isSectionsNull(chapterFile, chapter)) {
            return;
        }

        try {
            // 从配置中读取章节的基本信息。
            String name = chapter.getString("chapterInfo.name");
            String author = chapter.getString("chapterInfo.author");
            double version = Double.parseDouble(Objects.requireNonNull(chapter.getString("chapterInfo.version")));
            int ordinal = Integer.parseInt(Objects.requireNonNull(chapter.getString("chapterInfo.ordinal")));
            String lang = chapter.getString("chapterInfo.lang").toLowerCase();

            // 检查章节序号是否合法，如果不合法则返回。
            if (isOrdinalLowerThanOne(ordinal, name)) {
                return;
            }

            // 创建章节数据对象。
            ChapterData chapterData = createData(name, author, version, ordinal, lang.toLowerCase());

            // 创建一个章节数据和配置文件的映射。
            Map<ChapterData, YamlConfiguration> dataMap = new HashMap<>();
            dataMap.put(chapterData, chapter);

            // 根据语言是否存在和章节映射是否存在来决定如何处理当前章节数据。
            if (getTotal(lang) == null || getChapterMapsByLang(lang) == null) {
                putLang(dataMap, getDataByMap(dataMap));
            } else {
                // 检查是否有同名章节，如果有则进一步比较版本号。
                // 判断同名版本
                if (getTotal(lang).contains(name)) {
                    ChapterData equalData = getDataByLang(name, lang);
                    if (equalData != null) {
                        // 根据版本号比较结果，决定是否覆盖或记录版本冲突信息。
                        // 是否存在版本冲突
                        if (version < equalData.getVersion()) {
                            if (isForce) putLang(dataMap, getDataByMap(dataMap));
                            logVersionHigher(name, equalData.getVersion(), version);
                        } else if (version > equalData.getVersion()) {
                            logVersionLower(name, version, equalData.getVersion());
                        } else {
                            if (isForce) putLang(dataMap, getDataByMap(dataMap));
                            logVersionEqual(name, ordinal, equalData.getOrdinal(), version, equalData.getVersion());
                        }
                    }
                }

                // 检查章节序号是否冲突，如果冲突则返回。
                if (isOrdinalConflicted(lang, name, ordinal)) {
                    return;
                }

                // 将章节数据添加到对应的语言章节映射中。
                putLang(dataMap, getDataByMap(dataMap));
            }
        } catch (NumberFormatException e) {
            // 记录数字格式错误的日志。
            logNumberFormat(chapterFile);
            LogUtil.log(Level.SEVERE, "Details(For Developments): " + e.getMessage());
            return;
        }
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
        getChapterListByLang(lang).forEach(data -> {
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
        LogUtil.log(Level.SEVERE, LangPlugin.CHAPTERS_FOLDER_CHECK_NUMBER_FORMAT);
        LogUtil.log(Level.SEVERE, LangPlugin.CHAPTERS_CONSOLE_HELP);
        LogUtil.log(Level.SEVERE, "        chapterFile: " + chapterFile.getName());
        LogUtil.log(Level.SEVERE, LangPlugin.CHAPTERS_FOLDER_CHECK_LOAD_STOP);
    }

    /**
     * 输出 ordinal 冲突日志
     * @param name - 章节名
     * @param data - 章节数据
     * @param ordinal - 序数
     */
    private void logOrdinalConflicted(String name, ChapterData data, int ordinal){
        LogUtil.log(Level.SEVERE, LangPlugin.CHAPTERS_FOLDER_CHECK_ORDINAL_CONFLICT);
        LogUtil.log(Level.SEVERE, LangPlugin.CHAPTERS_CONSOLE_HELP);
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
        LogUtil.log(Level.SEVERE, LangPlugin.CHAPTERS_FOLDER_CHECK_VERSION_HIGHER);
        LogUtil.log(Level.SEVERE, LangPlugin.CHAPTERS_CONSOLE_HELP);
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
        LogUtil.log(Level.SEVERE, LangPlugin.CHAPTERS_FOLDER_CHECK_VERSION_LOWER);
        LogUtil.log(Level.SEVERE, LangPlugin.CHAPTERS_CONSOLE_HELP);
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
        LogUtil.log(Level.SEVERE, LangPlugin.CHAPTERS_FOLDER_CHECK_NAME_EQUAL);
        LogUtil.log(Level.SEVERE, LangPlugin.CHAPTERS_CONSOLE_HELP);
        LogUtil.log(Level.SEVERE, "        equalName: " + name);
        LogUtil.log(Level.SEVERE, "        Ordinal: " + ordinal1 + " | Version: " + version1);
        LogUtil.log(Level.SEVERE, "        Ordinal: " + ordinal2 + " | Version: " + version2);
        LogUtil.log(Level.SEVERE, LangPlugin.CHAPTERS_FOLDER_CHECK_LOAD_STOP);
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

            LogUtil.log(Level.WARNING, LangPlugin.CHAPTERS_FOLDER_CREATED);
            return false;
        }
        return true;
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
            LogUtil.log(Level.SEVERE, LangPlugin.CHAPTERS_FOLDER_CHECK_NULL);
            LogUtil.log(Level.SEVERE, LangPlugin.CHAPTERS_CONSOLE_HELP);
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

        LogUtil.log(Level.SEVERE, LangPlugin.CHAPTERS_FOLDER_CHECK_NUMBER_FORMAT);
        LogUtil.log(Level.SEVERE, LangPlugin.CHAPTERS_CONSOLE_HELP);
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
    public List<ChapterData> getChapterListByLang(String lang){

        if (lang.equalsIgnoreCase("all")) {
            return getAllChapterData();
        }

        List<ChapterData> dataList = new ArrayList<>();
        for (Map<ChapterData, YamlConfiguration> map : getChapterMapsByLang(lang)) {
            dataList.add(map.keySet().iterator().next());
        }
        return dataList;
    }

    /**
     * 获取指定语言的所有章节的 Data 与 Yaml
     * @param lang - 语言
     * @return 关于该语言的章节
     */
    public List<Map<ChapterData, YamlConfiguration>> getChapterMapsByLang(String lang){
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
        for (ChapterData equal : getChapterListByLang(lang.toLowerCase())){
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
        for (ChapterData data : getChapterListByLang(lang.toLowerCase())){
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
        for (ChapterData data : getChapterListByLang(lang.toLowerCase())) {
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
     * 获取所有章节数据的列表。
     * 这个方法遍历语言章节映射表中的所有章节配置，提取每个配置中的章节数据，并将其添加到一个列表中返回。
     * 这个列表包含了所有配置中的独特章节数据实例，提供了对所有章节数据的快速访问。
     *
     * @return 包含所有章节数据的列表。
     */
    public List<ChapterData> getAllChapterData(){
        // 初始化一个列表用于存储所有章节数据
        List<ChapterData> dataList = new ArrayList<>();
        // 遍历语言章节映射表中的每个章节配置列表
        for (List<Map<ChapterData, YamlConfiguration>> totalChapterMapList : langChapters.values()) {
            // 遍历每个章节配置映射
            for (Map<ChapterData, YamlConfiguration> map : totalChapterMapList) {
                // 从每个映射中获取第一个章节数据并添加到列表中
                dataList.add(map.keySet().iterator().next());
            }
        }
        // 返回包含所有章节数据的列表
        return dataList;
    }

    /**
     * 获取所有章节的Yaml配置列表。
     *
     * 该方法遍历langChapters映射中的所有值，每个值都是一个ChapterData到YamlConfiguration的映射列表。
     * 对于每个映射列表，它进一步遍历每个映射，并将每个映射的YamlConfiguration添加到结果列表中。
     * 这样，最终返回一个包含所有章节Yaml配置的列表。
     *
     * @return 所有章节的Yaml配置列表。
     */
    public List<YamlConfiguration> getAllChapterYaml(){
        // 初始化一个空的Yaml配置列表，用于存储所有章节的配置。
        List<YamlConfiguration> yamlList = new ArrayList<>();
        // 遍历langChapters映射中的每个值，每个值都是一个ChapterData到YamlConfiguration的映射列表。
        for (List<Map<ChapterData, YamlConfiguration>> totalChapterMapList : langChapters.values()) {
            // 对于每个映射列表，遍历每个映射。
            for (Map<ChapterData, YamlConfiguration> map : totalChapterMapList) {
                // 从每个映射中获取一个YamlConfiguration并添加到yamlList中。
                yamlList.add(map.values().iterator().next());
            }
        }
        // 返回包含所有章节Yaml配置的列表。
        return yamlList;
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
