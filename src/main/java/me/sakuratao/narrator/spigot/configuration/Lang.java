package me.sakuratao.narrator.spigot.configuration;

import top.jingwenmc.spigotpie.common.configuration.Configuration;
import top.jingwenmc.spigotpie.common.configuration.ConfigurationFile;
import top.jingwenmc.spigotpie.common.instance.PieComponent;

import java.util.Arrays;
import java.util.List;

@PieComponent
@ConfigurationFile("lang.yml")
public class Lang {

    @Configuration("no_permission")
    public static String NO_PERMISSION = "你没有权限执行此命令!";

    @Configuration("use_help_please")
    public static String USE_HELP_PLEASE = "请使用 /narrator help 查询帮助";

    @Configuration("command_player_only")
    public static String COMMAND_PLAYER_ONLY = "这个指令只有玩家能执行";

    @Configuration("command_console_only")
    public static String COMMAND_CONSOLE_ONLY = "这个指令只有控制台能执行";

    @Configuration("command_help")
    public static List<String> COMMAND_HELP = Arrays.asList(
            "&8&m---»--*---------------&7/ &f页数: %pages%/%maxPages% &7/&8&m--------------*--«---",
            "%commands%",
            "&8&m---»--*---------------&7/ &f页数: %pages%/%maxPages% &7/&8&m--------------*--«---"
    );

    @Configuration("command_help_page")
    public static String COMMAND_HELP_PAGE = "请使用: /neko help <Pages>";


    @Configuration("command_not_number")
    public static String COMMAND_NOT_NUMBER = "你输入的不是数字";

    @Configuration("command_more_than_max_pages")
    public static String COMMAND_MORE_THAN_MAX_PAGES = "超出最大页数";

    @Configuration("command_less_than_max_pages")
    public static String COMMAND_LESS_THAN_MAX_PAGES = "小于最小页数";

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

    @Configuration("chapter_folder_check_null")
    public static String CHAPTERS_FOLDER_CHECK_NULL = "留意查看 chapterInfo 中的 Name, Author, Lang, Version, Ordinal是否填写，这些不能留空.";

    @Configuration("chapter_console_help")
    public static String CHAPTERS_CONSOLE_HELP = "这些信息也许可以快速帮助你排查问题:";

    @Configuration("chapter_folder_check_number_format")
    public static List<String> CHAPTERS_FOLDER_CHECK_NUMBER_FORMAT = Arrays.asList(
            "请检查你的章节文件中的内容, 其中存在格式问题.",
            "所有的 Ordinal 必须为 整数 且大于 1!",
            "所有的 Version 必须为 整数 或 小数!"
    );

    @Configuration("chapter_folder_check_version_higher")
    public static List<String> CHAPTERS_FOLDER_CHECK_VERSION_HIGHER = Arrays.asList(
            "有个章节已经加载了更高版本的文件.",
            "我们不会加载目前提供的章节文件.",
            "如果您仍想加载此版本的章节, 使用 /nr force 进行强制重载."
    );

    @Configuration("chapter_folder_check_name_equal")
    public static List<String> CHAPTERS_FOLDER_CHECK_NAME_EQUAL = Arrays.asList(
            "请检查你的章节文件，",
            "章节名字存在冲突，请确保所有章节名字唯一.",
            "如果您仍想加载此章节, 使用 /nr force 进行强制重载."
    );

    @Configuration("chapter_folder_check_version_lower")
    public static List<String> CHAPTERS_FOLDER_CHECK_VERSION_LOWER = Arrays.asList(
            "有个章节已经加载了较旧的版本.",
            "我们会加载目前提供的章节文件并进行覆盖."
    );

    @Configuration("chapter_folder_check_ordinal_conflict")
    public static String CHAPTERS_FOLDER_CHECK_ORDINAL_CONFLICT = "以下的 章节/任务 存在 Ordinal 冲突，章节/任务 之间的 Ordinal 必须是唯一的.";

    @Configuration("chapter_folder_check_task_name_same")
    public static List<String> CHAPTERS_FOLDER_CHECK_TASK_NAME_SAME = Arrays.asList(
            "请检查你的章节文件，",
            "任务名字存在冲突，请确保所有任务名字唯一."
    );

    @Configuration("chapter_folder_check_task_null")
    public static String CHAPTERS_FOLDER_CHECK_TASK_NULL = "留意查看 chapterTasks 中各个 task 的 Name, World, Ordinal 是否填写，这些不能留空.";

    @Configuration("chapter_folder_check_task_world_null")
    public static List<String> CHAPTERS_FOLDER_CHECK_TASK_WORLD_NULL = Arrays.asList(
            "未找到 Task 中填写的指定 World ",
            "World 必须与文件夹名称相同"
    );

    @Configuration("chapter_execute_jt_number_format")
    public static List<String> CHAPTERS_EXECUTE_JT_NUMBER_FORMAT = Arrays.asList(
            "请检查你的内容 'JT/JUMPTASK', 其中存在格式问题.",
            "所填写的 Ordinal 必须大于 1, Content 索引必须大于 0"
    );

    @Configuration("chapter_execute_jt_not_exist")
    public static List<String> CHAPTERS_EXECUTE_JT_NOT_EXIST = Arrays.asList(
            "请检查你的内容 'JT/JUMPTASK', 其中存在内容问题.",
            "所填写的 Ordinal 指定的任务不存在"
    );

    @Configuration("chapter_folder_check_load_stop")
    public static String CHAPTERS_FOLDER_CHECK_LOAD_STOP = "加载停止.";


}
