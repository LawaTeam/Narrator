package me.sakuratao.narrator.common.handlers;

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
    ChapterHandler chapterHandler;
    @Wire
    ConditionHandler conditionHandler;
    @Wire
    ContentHandler contentHandler;
    @Wire
    TaskHandler taskHandler;

}
