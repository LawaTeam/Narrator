package me.sakuratao.narrator.spigot.data.cache;

import lombok.Getter;
import top.jingwenmc.spigotpie.common.instance.PieComponent;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@PieComponent
public class CacheData {

    private final ConcurrentHashMap<String, Integer> contentIndex = new ConcurrentHashMap<>();

}
