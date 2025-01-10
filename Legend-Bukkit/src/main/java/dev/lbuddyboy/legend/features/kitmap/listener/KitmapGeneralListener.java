package dev.lbuddyboy.legend.features.kitmap.listener;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.team.model.TeamType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class KitmapGeneralListener implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (!SettingsConfig.KITMAP_ENABLED.getBoolean()) return;
        if (!TeamType.SPAWN.appliesAt(player.getLocation())) return;

        event.setCancelled(true);
    }

}
