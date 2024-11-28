package dev.lbuddyboy.legend.listener;

import dev.lbuddyboy.commons.util.LocationUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.timer.impl.CombatTimer;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PortalTravelAgent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftOfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.CraftTravelAgent;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import java.util.List;
import java.util.stream.Collectors;

public class EndListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo(), from = event.getFrom();

        if (to.getWorld().getEnvironment() == World.Environment.THE_END) {
            event.setTo(getEntranceLocation());
            event.useTravelAgent(false);
            event.getPortalTravelAgent().setCanCreatePortal(false);
        } else if (from.getWorld().getEnvironment() == World.Environment.THE_END && to.getWorld().getEnvironment() == World.Environment.NORMAL) {
            event.setTo(getExitLocation());
            event.useTravelAgent(false);
            event.getPortalTravelAgent().setCanCreatePortal(false);
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
