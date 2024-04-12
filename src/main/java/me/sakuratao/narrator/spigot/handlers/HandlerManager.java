package me.sakuratao.narrator.spigot.handlers;

import lombok.Getter;
import me.sakuratao.narrator.spigot.handlers.ChapterHandler;
import me.sakuratao.narrator.spigot.handlers.ConditionHandler;
import me.sakuratao.narrator.spigot.handlers.ContentHandler;
import me.sakuratao.narrator.spigot.handlers.TaskHandler;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

@Getter
@PieComponent
public class HandlerManager {

    @Wire
    private ChapterHandler chapterHandler;
    @Wire
    private ConditionHandler conditionHandler;
    @Wire
    private ContentHandler contentHandler;
    @Wire
    private TaskHandler taskHandler;
    @Wire
    private HologramHandler hologramHandler;

}
