package dev.lbuddyboy.legend.timer;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.model.LegendUser;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.UUID;

public abstract class PlayerTimer implements Listener {

    public abstract String getId();

    public String getView() {
        return LegendBukkit.getInstance().getTimerHandler().getConfig().getString(getId() + ".view");
    }

    public String getDisplayName() {
        return LegendBukkit.getInstance().getTimerHandler().getConfig().getString(getId() + ".display-name");
    }

    public String getPrimaryColor() {
        return LegendBukkit.getInstance().getTimerHandler().getConfig().getString(getId() + ".primary-color");
    }

    public String getSecondaryColor() {
        return LegendBukkit.getInstance().getTimerHandler().getConfig().getString(getId() + ".secondary-color");
    }

    public int getDuration() {
        return LegendBukkit.getInstance().getTimerHandler().getConfig().getInt(getId() + ".duration");
    }
    
    public String getColoredName() {
        return getPrimaryColor() + getDisplayName();
    }

    public void remove(UUID playerUUID) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(playerUUID);

        user.removeTimer(getId());
    }

    public void pause(UUID playerUUID) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(playerUUID);

        user.pauseTimer(getId());
    }

    public void resume(UUID playerUUID) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(playerUUID);

        user.resumeTimer(getId());
    }

    public void apply(UUID playerUUID) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(playerUUID);

        user.applyTimer(getId(), getDuration() * 1000L);
    }

    public void apply(Player player) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        user.applyTimer(getId(), getDuration(player) * 1000L);
    }

    public String getRemainingSeconds(UUID playerUUID) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(playerUUID);

        return user.getRemainingSeconds(getId());
    }
    
    public String getRemaining(UUID playerUUID) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(playerUUID);

        return user.getRemaining(getId());
    }
    
    public boolean isActive(UUID playerUUID) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(playerUUID);
        
        return user.isTimerActive(getId());
    }
    
    public int getDuration(Player player) {
        return getDuration();
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

    public String format(UUID playerUUID, String string) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(playerUUID);

        return string
                .replaceAll("%timer-colored%", getColoredName())
                .replaceAll("%timer-display%", getDisplayName())
                .replaceAll("%timer-primary%", getPrimaryColor())
                .replaceAll("%timer-secondary%", getSecondaryColor())
                .replaceAll("%timer-id%", getId())
                .replaceAll("%timer-remaining-seconds%", getRemainingSeconds(playerUUID))
                .replaceAll("%timer-remaining-mmss%", getRemaining(playerUUID))
                ;
    }
    
}
