package me.sakuratao.narrator.common.api;

import lombok.Getter;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

@Getter
@PieComponent
public class NarratorAPIProvider {

    public NarratorAPI narratorAPI = null;

    public void setNarratorAPI(NarratorAPI narratorAPI) {
        if (this.narratorAPI != null) return;
        // 防止重复注入
        this.narratorAPI = narratorAPI;
    }

}
