package me.sakuratao.narrator.spigot.task;

import lombok.Getter;
import lombok.Setter;
import me.sakuratao.narrator.common.Narrator;
import me.sakuratao.narrator.spigot.data.player.PlayerData;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public final class ContentTask implements Runnable {
    private final Narrator narrator;
    private final PlayerData data;

    public ContentTask(Narrator narrator, PlayerData data) {
        this.narrator = narrator;
        this.data = data;
    }

    @Override
    public void run() {

        if (!data.getPlayer().isOnline()) return;

        if (execute()) {

            List<String> content = data.getPlayingTaskData().getContent();
            data.setContentIndex(data.getContentIndex() + 1);

            if (data.getContentIndex() >= content.size()) {
                narrator.getManagerHandler().getTaskManager().killTask(data.getPlayerName());
            }

        }

    }

    private boolean execute() {
        return narrator.getHandlerManager().getContentHandler().handleContent(
                data.getPlayer(),
                data.getPlayingChapterData(),
                data.getCurrentContent(),
                this
        );
    }

    public Narrator narrator() {
        return narrator;
    }

    public PlayerData data() {
        return data;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ContentTask) obj;
        return Objects.equals(this.narrator, that.narrator) &&
                Objects.equals(this.data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(narrator, data);
    }

    @Override
    public String toString() {
        return "ContentTask[" +
                "narrator=" + narrator + ", " +
                "data=" + data + ']';
    }


}
