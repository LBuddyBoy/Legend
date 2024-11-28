package dev.lbuddyboy.legend.classes;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.List;

public abstract class PvPClass implements Listener {

    public abstract String getName();
    public abstract String getDisplayName();
    public abstract boolean hasSetOn(Player player);
    public abstract boolean isTickable();
    public abstract int getLimit();

    public List<String> getScoreboardLines(Player player) {
        return Collections.emptyList();
    }

    public void apply(Player player) {
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("classes." + getName().toLowerCase() + ".applied")));
    }

    public void remove(Player player) {
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("classes." + getName().toLowerCase() + ".removed")));
    }

    public void tick(Player player) {

    }

    public boolean shouldApply(Player player) {
        // TODO: Add limit
        return hasSetOn(player);
    }

}
