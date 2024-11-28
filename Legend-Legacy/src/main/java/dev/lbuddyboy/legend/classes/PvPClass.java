package dev.lbuddyboy.legend.classes;

import lombok.Getter;
import org.bukkit.entity.Player;

public abstract class PvPClass {

    public abstract String getName();
    public abstract boolean hasSetOn(Player player);
    public abstract boolean isTickable();
    public abstract int getLimit();

    public void apply(Player player) {

    }

    public void remove(Player player) {

    }

    public void tick() {

    }

    public boolean shouldApply(Player player) {
        // TODO: Add limit
        return hasSetOn(player);
    }

}
