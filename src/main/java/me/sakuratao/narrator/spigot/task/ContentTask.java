package me.sakuratao.narrator.spigot.task;

import lombok.Getter;
import lombok.Setter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.data.player.PlayerData;

import java.util.List;

@Getter
@Setter
public class ContentTask implements Runnable{

    private final Narrator narrator;
    private final PlayerData data;

    public ContentTask(Narrator narrator, PlayerData data) {
        this.narrator = narrator;
        this.data = data;
    }

    @Override
    public void run() {

        if (!data.getPlayer().isOnline()) return;

        if (execute()){

            List<String> content = data.getPlayingTaskData().getContent();
            data.setContentIndex(data.getContentIndex() + 1);

            if (data.getContentIndex() >= content.size()) {
                narrator.getManagerHandler().getTaskManager().killTask(data.getPlayerName());
            }

        }

    }

    private boolean execute(){
        return narrator.getHandlerManager().getContentHandler().execute(
                data.getPlayer(),
                data.getPlayingChapterData(),
                data.getCurrentContent(),
                this
        );
    }

}
