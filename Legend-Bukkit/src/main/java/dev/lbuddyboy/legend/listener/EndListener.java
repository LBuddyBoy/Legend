package dev.lbuddyboy.legend.listener;

import dev.lbuddyboy.commons.util.LocationUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.PortalCreateEvent;

import java.util.List;
import java.util.stream.Collectors;

public class EndListener implements Listener {

    @EventHandler
    public void onEntityCreatePortal(PortalCreateEvent event) {
        if (event.getReason() == PortalCreateEvent.CreateReason.END_PLATFORM) {
            event.getBlocks().clear();
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMovePortal(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo(), from = event.getFrom();

        if (to == null || (to.getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY() && to.getBlockZ() == from.getBlockZ())) return;
        if (to.getBlock().getType() != Material.END_PORTAL) return;
        if (player.getWorld().getEnvironment() != World.Environment.THE_END) return;

        event.setTo(getEntranceLocation());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo(), from = event.getFrom();

        if (to == null || (to.getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY() && to.getBlockZ() == from.getBlockZ())) return;
        if (to.getBlock().getType() != Material.WATER) return;
        if (player.getWorld().getEnvironment() != World.Environment.THE_END) return;

        event.setTo(getExitLocation());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo(), from = event.getFrom();

        if (to.getWorld().getEnvironment() == World.Environment.THE_END) {
            event.setTo(getEntranceLocation());
            event.setCanCreatePortal(false);
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        World fromWorld = event.getFrom();
        if (fromWorld.getEnvironment() == World.Environment.THE_END) {

            return;
        }

    }

    public List<Location> getCreeperLocations() {
        return LegendBukkit.getInstance().getSettings().getStringList("end.creepers").stream().map(LocationUtils::deserializeString).collect(Collectors.toList());
    }

    public Location getExitLocation() {
        return LocationUtils.deserializeString(LegendBukkit.getInstance().getSettings().getString("end.exit"));
    }

    public Location getEntranceLocation() {
        return LocationUtils.deserializeString(LegendBukkit.getInstance().getSettings().getString("end.entrance"));
    }

}
