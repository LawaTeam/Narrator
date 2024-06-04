package me.sakuratao.narrator.spigot.configuration.lang;

import top.jingwenmc.spigotpie.common.configuration.Configuration;
import top.jingwenmc.spigotpie.common.configuration.ConfigurationFile;
import top.jingwenmc.spigotpie.common.instance.PieComponent;

@PieComponent
@ConfigurationFile("lang_player.yml")
public class LangPlayer {

    @Configuration("language_code")
    public static String LANGUAGE_CODE = "zh_CN";

    @Configuration("content_task_not_started")
    public static String CONTENT_TASK_NOT_STARTED = "&c你当前不处于剧情浏览中.";

    @Configuration("content_task_stopped")
    public static String CONTENT_TASK_STOPPED = "&8* &c已对当前剧情进行暂停 &8*";

}
