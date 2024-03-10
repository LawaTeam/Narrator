package me.sakuratao.narrator.spigot.configuration;

import top.jingwenmc.spigotpie.common.configuration.Configuration;
import top.jingwenmc.spigotpie.common.configuration.ConfigurationFile;
import top.jingwenmc.spigotpie.common.instance.PieComponent;

@PieComponent
@ConfigurationFile("lang.yml")
public class Lang {

    @Configuration("chapters_loaded")
    public static String CHAPTERS_LOADED = "所有章节已完成加载!";

    @Configuration("chapters_load_force")
    public static String CHAPTERS_LOAD_FORCE = "正在执行强制重载...";

    @Configuration("chapter_folder_created")
    public static String CHAPTERS_FOLDER_CREATED = "章节文件夹已创建!";

    @Configuration("chapters_folder_edit")
    public static String CHAPTERS_FOLDER_EDIT = "我们已生成一个章节示例，请从它开始制作你的第一个章节!";

    @Configuration("chapters_folder_reload")
    public static String CHAPTERS_FOLDER_RELOAD = "确认无误，使用 /nr 进行重载操作.";

    @Configuration("chapters_folder_reload_force")
    public static String CHAPTERS_FOLDER_RELOAD_FORCE = "确认无误，使用 /nr force 进行强制重载操作.";

    @Configuration("chapter_folder_check")
    public static String CHAPTERS_FOLDER_CHECK = "请检查你的章节文件 ";

    @Configuration("chapter_folder_check_null")
    public static String CHAPTERS_FOLDER_CHECK_NULL = "留意查看 chapterInfo 中的 Name, Author, Lang, Version, Ordinal是否填写，这些不能留空.";

    @Configuration("chapter_folder_check_ordinal")
    public static String CHAPTERS_FOLDER_CHECK_ORDINAL = "Ordinal 为 整数!";

    @Configuration("chapter_folder_check_ordinal_over_one")
    public static String CHAPTERS_FOLDER_CHECK_ORDINAL_OVER_ONE = "所有的 Ordinal 必须大于等于 1!";

    @Configuration("chapter_folder_check_help")
    public static String CHAPTERS_FOLDER_CHECK_HELP = "这些信息也许可以快速帮助你排查问题:";

    @Configuration("chapter_folder_check_version")
    public static String CHAPTERS_FOLDER_CHECK_VERSION = "Version 必须为 Double!";

    @Configuration("chapter_folder_check_version_higher")
    public static String CHAPTERS_FOLDER_CHECK_VERSION_HIGHER = "关于章节 %chapterName% ， 当前已经加载了更高版本的文件.";

    @Configuration("chapter_folder_check_version_higher_wont_load")
    public static String CHAPTERS_FOLDER_CHECK_VERSION_HIGHER_WONT_LOAD = "我们不会加载此次提供的章节文件.";

    @Configuration("chapter_folder_check_version_higher_force")
    public static String CHAPTERS_FOLDER_CHECK_VERSION_HIGHER_FORCE = "如果您仍想加载此版本的章节, 使用 /nr force 进行强制重载.";

    @Configuration("chapter_folder_check_name_same")
    public static String CHAPTERS_FOLDER_CHECK_NAME_SAME = "章节名字冲突，请确保所有章节名字唯一.";

    @Configuration("chapter_folder_check_version_lower")
    public static String CHAPTERS_FOLDER_CHECK_VERSION_LOWER = "关于章节 %chapterName% ， 当前已加载的是较旧的版本.";

    @Configuration("chapter_folder_check_version_lower_loaded")
    public static String CHAPTERS_FOLDER_CHECK_VERSION_LOWER_LOADED = "我们会加载此次提供的章节文件.";

    @Configuration("chapter_folder_check_ordinal_conflict")
    public static String CHAPTERS_FOLDER_CHECK_ORDINAL_CONFLICT = "这些章节存在 Ordinal 冲突，Ordinal 必须是唯一的.";

    @Configuration("chapter_folder_check_load_stop")
    public static String CHAPTERS_FOLDER_CHECK_LOAD_STOP = "加载停止.";

}
