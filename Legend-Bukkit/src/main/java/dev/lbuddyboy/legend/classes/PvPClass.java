package dev.lbuddyboy.legend.classes;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.LegendConstants;
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

    public abstract String getName();
    public abstract String getDisplayName();
    public abstract boolean hasSetOn(Player player);
    public abstract boolean isTickable();

    public List<String> getScoreboardLines(Player player) {
        return Collections.emptyList();
    }

    public boolean apply(Player player) {
        ClassWarmUpTimer warmUpTimer = LegendBukkit.getInstance().getTimerHandler().getTimer(ClassWarmUpTimer.class);

        if (isWarmup() && !warmUpTimer.wasActive(player.getUniqueId())) {
            warmUpTimer.apply(player.getUniqueId(), getWarmupSeconds());
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("classes." + getName().toLowerCase() + ".warming")));
        }

        if (isWarmup() && warmUpTimer.isActive(player.getUniqueId())) {
            if (!hasSetOn(player)) {
                warmUpTimer.remove(player.getUniqueId());
                return false;
            }

            return false;
        }

        warmUpTimer.remove(player.getUniqueId());
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("classes." + getName().toLowerCase() + ".applied")));
        LegendBukkit.getInstance().getClassHandler().getActiveClasses().put(player.getUniqueId(), this);
        return true;
    }

    public void remove(Player player) {
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("classes." + getName().toLowerCase() + ".removed")));
    }

    public void tick(Player player) {

    }

    public boolean shouldApply(Player player) {
        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(player).orElse(null);

        if (team != null && getLimit() > 0) {
            int teamAmount = (int) team.getOnlinePlayers().stream().filter(member -> LegendBukkit.getInstance().getClassHandler().isClassApplied(member, getClass())).count();

            if (teamAmount >= getLimit()) {
                player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("classes.limited")));
                return false;
            }
        }

        return true;
    }

    public int getWarmupSeconds() {
        return LegendBukkit.getInstance().getSettings().getInt("classes." + getName().toLowerCase() + ".warmup");
    }

    public boolean isWarmup() {
        return getWarmupSeconds() > 0;
    }

    public int getLimit() {
        return LegendBukkit.getInstance().getSettings().getInt("classes." + getName().toLowerCase() + ".limit");
    }

}
