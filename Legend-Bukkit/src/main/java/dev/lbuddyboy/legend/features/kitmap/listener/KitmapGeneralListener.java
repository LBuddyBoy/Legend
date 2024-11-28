package dev.lbuddyboy.legend.features.kitmap.listener;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.TeamType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class KitmapGeneralListener implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (!LegendBukkit.getInstance().getSettings().getBoolean("kitmap.enabled")) return;
        if (!TeamType.SPAWN.appliesAt(player.getLocation())) return;

        event.setCancelled(true);
    }

}
