package me.sakuratao.narrator.spigot.configuration;

import top.jingwenmc.spigotpie.common.configuration.Configuration;
import top.jingwenmc.spigotpie.common.configuration.ConfigurationFile;
import top.jingwenmc.spigotpie.common.instance.PieComponent;

@PieComponent
@ConfigurationFile("config.yml")
public class Config {

    @Configuration(value = "language_type")
    public static String LANGUAGE_TYPE = "en_US";

}
