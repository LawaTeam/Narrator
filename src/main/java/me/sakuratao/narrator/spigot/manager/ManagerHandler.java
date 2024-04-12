package me.sakuratao.narrator.spigot.manager;

import lombok.Getter;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

@Getter
@PieComponent
public class ManagerHandler {

    @Wire
    private PlayerManager playerManager;
    @Wire
    private TaskManager taskManager;

}
