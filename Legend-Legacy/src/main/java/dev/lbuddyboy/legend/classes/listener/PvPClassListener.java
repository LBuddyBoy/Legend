package dev.lbuddyboy.legend.classes.listener;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.classes.ClassHandler;
import dev.lbuddyboy.legend.classes.PvPClass;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PvPClassListener implements Listener {

    private final ClassHandler classHandler = LegendBukkit.getInstance().getClassHandler();

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!this.classHandler.isClassApplied(player)) return;

        this.classHandler.getClassApplied(player).remove(player);
    }

}
