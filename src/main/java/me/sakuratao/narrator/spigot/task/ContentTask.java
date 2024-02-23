package me.sakuratao.narrator.spigot.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.data.Player.PlayerData;
import me.sakuratao.narrator.spigot.data.chapter.ChapterData;
import me.sakuratao.narrator.spigot.data.chapter.TaskData;
import org.bukkit.entity.Player;

import java.util.List;

@Builder
public class ContentTask implements Runnable{

    final Narrator narrator;

    final PlayerData data;

    @Override
    public void run() {

        if (!data.isNextContent()) return;

        if (narrator.getHandlerManager().getContentHandler().execute(
                data.getPlayer(),
                data.getPlayingChapter(),
                data.getExecutingContent()
        )){

            data.setNextContent(true);
            data.setExecutingContentIndex(data.getExecutingContentIndex() + 1);

            List<String> content = data.getPlayingChapter().getTasks().get(data.getPlayingTaskOrdinal()).getContent();
            data.setExecutingContent(content.get(data.getExecutingContentIndex()));

        }

    }

}
