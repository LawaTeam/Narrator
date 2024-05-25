package me.sakuratao.narrator.spigot.handlers;

import lombok.Getter;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

@Getter
@PieComponent
public class HandlerManager {

    @Wire private ChapterHandler chapterHandler;
    @Wire private ConditionHandler conditionHandler;
    @Wire private ContentHandler contentHandler;
    @Wire private TaskHandler taskHandler;
    @Wire private FunctionHandler functionHandler;
    @Wire private DebugHandler debugHandler;

}
