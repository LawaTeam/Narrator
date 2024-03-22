package me.sakuratao.narrator.spigot.configuration;

import top.jingwenmc.spigotpie.common.configuration.Configuration;
import top.jingwenmc.spigotpie.common.configuration.ConfigurationFile;
import top.jingwenmc.spigotpie.common.instance.PieComponent;

import java.util.Arrays;
import java.util.List;

@PieComponent
@ConfigurationFile("lang.yml")
public class Lang {

    @Configuration("chapters_loaded")
    public static String CHAPTERS_LOADED = "所有章节已完成加载!";

    @Configuration("chapters_load_force")
    public static String CHAPTERS_LOAD_FORCE = "正在执行强制重载...";

    @Configuration("chapter_folder")
    public static List<String> CHAPTERS_FOLDER_CREATED = Arrays.asList(
            "章节文件夹已创建!",
            "我们已生成一个章节示例，请从它开始制作你的第一个章节!",
            "确认无误后，使用 /nr 进行重载操作."
    );

    // @Configuration("chapter_folder_check")
    // public static String CHAPTERS_FOLDER_CHECK = "请检查你的章节文件 ";

    @Configuration("chapter_folder_check_null")
    public static String CHAPTERS_FOLDER_CHECK_NULL = "留意查看 chapterInfo 中的 Name, Author, Lang, Version, Ordinal是否填写，这些不能留空.";

    @Configuration("chapter_folder_check_help")
    public static String CHAPTERS_FOLDER_CHECK_HELP = "这些信息也许可以快速帮助你排查问题:";

    @Configuration("chapter_folder_check_number_format")
    public static List<String> CHAPTERS_FOLDER_CHECK_NUMBER_FORMAT = Arrays.asList(
            "请检查你的章节文件中的内容, 其中存在格式问题.",
            "Ordinal 与 Version 都不能为空.",
            "所有的 Ordinal 必须为 整数 且大于 1!",
            "所有的 Version 必须为 整数 或 小数!"
    );

    @Configuration("chapter_folder_check_version_higher")
    public static List<String> CHAPTERS_FOLDER_CHECK_VERSION_HIGHER = Arrays.asList(
            "有个章节已经加载了更高版本的文件.",
            "我们不会加载目前提供的章节文件.",
            "如果您仍想加载此版本的章节, 使用 /nr force 进行强制重载."
    );

    @Configuration("chapter_folder_check_name_same")
    public static List<String> CHAPTERS_FOLDER_CHECK_NAME_SAME = Arrays.asList(
            "请检查你的章节文件，",
            "章节/任务 名字存在冲突，请确保所有 章节/任务 名字唯一.",
            "如果您仍想加载此章节, 使用 /nr force 进行强制重载."
    );

    @Configuration("chapter_folder_check_version_lower")
    public static List<String> CHAPTERS_FOLDER_CHECK_VERSION_LOWER = Arrays.asList(
            "有个章节已经加载了较旧的版本.",
            "我们会加载目前提供的章节文件并进行覆盖."
    );

    @Configuration("chapter_folder_check_ordinal_conflict")
    public static String CHAPTERS_FOLDER_CHECK_ORDINAL_CONFLICT = "以下的 章节/任务 存在 Ordinal 冲突，章节/任务 之间的 Ordinal 必须是唯一的.";

    @Configuration("chapter_folder_check_load_stop")
    public static String CHAPTERS_FOLDER_CHECK_LOAD_STOP = "加载停止.";


}
