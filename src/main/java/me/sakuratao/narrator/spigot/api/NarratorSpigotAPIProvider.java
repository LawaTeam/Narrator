package me.sakuratao.narrator.spigot.api;

import lombok.Getter;
import top.jingwenmc.spigotpie.common.instance.PieComponent;

@Getter
@PieComponent
public class NarratorSpigotAPIProvider {

    public NarratorSpigotAPI narratorSpigotAPI = null;

    public void setNarratorSpigotAPI(NarratorSpigotAPI narratorSpigotAPI) {
        // 防止重复注入
        if (this.narratorSpigotAPI != null) return;
        this.narratorSpigotAPI = narratorSpigotAPI;
    }

}
