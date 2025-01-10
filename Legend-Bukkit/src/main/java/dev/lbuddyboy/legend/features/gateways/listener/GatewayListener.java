package dev.lbuddyboy.legend.features.gateways.listener;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.gateways.model.Gateway;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class GatewayListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo(), from = event.getFrom();

        if (to == null || (to.getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY() && to.getBlockZ() == from.getBlockZ()))
            return;
        if (to.getBlock().getType() != Material.END_GATEWAY) return;

        for (Gateway gateway : LegendBukkit.getInstance().getGatewayHandler().getGateways().values()) {
            if (gateway.getEntranceRegion() != null && gateway.getExitLocation() != null && gateway.getEntranceRegion().contains(to)) {
                event.setTo(gateway.getExitLocation());
                return;
            }

            if (gateway.getExitRegion() != null && gateway.getEntranceLocation() != null && gateway.getExitRegion().contains(to)) {
                event.setTo(gateway.getEntranceLocation());
                return;
            }
        }

        event.setCancelled(true);
    }

}
