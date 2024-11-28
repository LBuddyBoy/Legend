package dev.lbuddyboy.legend.timer;

import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.model.LegendUser;
import dev.lbuddyboy.legend.user.model.PersistentTimer;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.UUID;

@Getter
public abstract class ServerTimer implements Listener {

    private PersistentTimer timer = null;

    public abstract String getId();

    public String getView() {
        return LegendBukkit.getInstance().getTimerHandler().getServerTimerConfig().getString(getId() + ".view");
    }

    public boolean isBoldColoredName() {
        return LegendBukkit.getInstance().getTimerHandler().getServerTimerConfig().getBoolean(getId() + ".bold-display", true);
    }

    public String getDisplayName() {
        return LegendBukkit.getInstance().getTimerHandler().getServerTimerConfig().getString(getId() + ".display-name");
    }

    public String getPrimaryColor() {
        return LegendBukkit.getInstance().getTimerHandler().getServerTimerConfig().getString(getId() + ".primary-color");
    }

    public String getSecondaryColor() {
        return LegendBukkit.getInstance().getTimerHandler().getServerTimerConfig().getString(getId() + ".secondary-color");
    }

    public boolean isActive() {
        if (this.timer == null) return false;

        return !this.timer.isExpired();
    }

    public void start(long duration) {
        this.timer = new PersistentTimer(getId(), duration);
    }

    public void pause() {
        this.timer.pause();
    }

    public void unpause() {
        this.timer.unpause();
    }

    public void end() {
        this.timer = null;
    }

    public boolean isScoreboard() {
        return getView().equalsIgnoreCase("SCOREBOARD");
    }

    public boolean isActionbar() {
        return getView().equalsIgnoreCase("ACTION_BAR");
    }

    public boolean isViewable() {
        return isScoreboard() || isActionbar();
    }

    public String getColoredName() {
        return CC.blend(getDisplayName(), getPrimaryColor(), getSecondaryColor(), isBoldColoredName() ? "&l" : "");
    }

    public String format(String string) {
        return string
                .replaceAll("%timer-colored%", getColoredName())
                .replaceAll("%timer-display%", getDisplayName())
                .replaceAll("%timer-primary%", getPrimaryColor())
                .replaceAll("%timer-secondary%", getSecondaryColor())
                .replaceAll("%timer-id%", getId())
                .replaceAll("%timer-remaining-hhmmss%", (this.timer == null ? "" : TimeUtils.formatIntoHHMMSS((int) (this.timer.getRemaining() / 1000L))))
                ;
    }

}
