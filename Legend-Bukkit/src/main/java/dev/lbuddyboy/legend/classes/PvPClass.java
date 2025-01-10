package dev.lbuddyboy.legend.classes;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.LegendConstants;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.timer.impl.ClassWarmUpTimer;
import dev.lbuddyboy.legend.util.Cooldown;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.List;

@Getter
public abstract class PvPClass implements Listener {

    public final Config config;

    public PvPClass() {
        this.config = new Config(LegendBukkit.getInstance(), getName().toLowerCase(), LegendBukkit.getInstance().getClassHandler().getDirectory());

        this.loadDefaults();
        this.config.options().copyDefaults(true);
        this.config.save();

        if (this.config.getBoolean("settings.enabled")) {
            LegendBukkit.getInstance().getClassHandler().getClasses().add(this);
        }
    }

    public abstract void loadDefaults();
    public abstract String getName();
    public abstract String getDisplayName();
    public abstract boolean hasSetOn(Player player);
    public abstract boolean isTickable();

    public List<String> getScoreboardLines(Player player) {
        return Collections.emptyList();
    }

    public boolean apply(Player player) {
        if (LegendBukkit.getInstance().getMiniGamesProvider() != null && LegendBukkit.getInstance().getMiniGamesProvider().isInMiniGame(player)) return false;

        ClassWarmUpTimer warmUpTimer = LegendBukkit.getInstance().getTimerHandler().getTimer(ClassWarmUpTimer.class);

        if (isWarmup() && !warmUpTimer.wasActive(player.getUniqueId())) {
            warmUpTimer.apply(player.getUniqueId(), getWarmupSeconds());
            player.sendMessage(CC.translate(this.config.getString("lang.warming")));
        }

        if (isWarmup() && warmUpTimer.isActive(player.getUniqueId())) {
            if (!hasSetOn(player)) {
                warmUpTimer.remove(player.getUniqueId());
                return false;
            }

            return false;
        }

        warmUpTimer.remove(player.getUniqueId());
        player.sendMessage(CC.translate(this.config.getString("lang.applied")));
        LegendBukkit.getInstance().getClassHandler().getActiveClasses().put(player.getUniqueId(), this);
        return true;
    }

    public void remove(Player player) {
        player.sendMessage(CC.translate(this.config.getString("lang.removed")));
    }

    public void tick(Player player) {

    }

    public boolean shouldApply(Player player) {
        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(player).orElse(null);

        if (team != null && getLimit() > 0) {
            int teamAmount = (int) team.getOnlinePlayers().stream().filter(member -> LegendBukkit.getInstance().getClassHandler().isClassApplied(member, getClass())).count();

            if (teamAmount >= getLimit()) {
                player.sendMessage(CC.translate(this.config.getString("lang.limited")));
                return false;
            }
        }

        return true;
    }

    public int getWarmupSeconds() {
        return this.config.getInt("settings.warmup");
    }

    public boolean isWarmup() {
        return getWarmupSeconds() > 0;
    }

    public int getLimit() {
        return this.config.getInt("settings.limit");
    }

}
