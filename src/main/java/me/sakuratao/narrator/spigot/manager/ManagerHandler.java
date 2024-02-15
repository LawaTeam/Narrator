package me.sakuratao.narrator.spigot.manager;

import lombok.Getter;
import me.sakuratao.narrator.spigot.manager.PlayerManager;
import me.sakuratao.narrator.spigot.manager.TaskManager;
import org.bukkit.entity.Player;
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
