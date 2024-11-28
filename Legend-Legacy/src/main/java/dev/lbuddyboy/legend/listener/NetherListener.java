package dev.lbuddyboy.legend.listener;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftTravelAgent;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.PortalCreateEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NetherListener implements Listener {

    /**
     * Automatically teleport players that are in overworld spawn to nether spawn.
     * @param event
     */

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo(), from = event.getFrom();

        Team team = LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getTeam(from).orElse(null);
        if (team == null) return;
        if (team.getTeamType() != TeamType.SPAWN) {
            event.setPortalTravelAgent(new LegendTravelAgent(((CraftWorld) to.getWorld()).getHandle()));
            event.useTravelAgent(true);
            return;
        }

        event.useTravelAgent(false);
        event.setTo(to.getWorld().getSpawnLocation());
    }

    public static class LegendTravelAgent extends CraftTravelAgent {

        public LegendTravelAgent(WorldServer worldserver) {
            super(worldserver);
        }

        @Override
        public Location findOrCreate(Location target) {
            Location location = super.findOrCreate(target);
            Team teamAt = LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getTeam(location).orElse(null);

            if (teamAt == null) return location;

            int x = location.getBlockX(), z = location.getBlockZ();

            boolean south = z > 0 && x > 0 || z > 0 && x < 0;
            boolean north = z < 0 && x > 0 || z > 0 && x < 0;
            boolean west = x < 0 && z > 0 || x > 0 && z < 0;
            boolean east = x > 0 && z > 0 || x > 0 && z < 0;

            for (int i = -32; i <= 32; i++) {
                Location search = location.clone();

                if (south || north) search.add(i, 0, 0);
                if (west || east) search.add(0, 0, i);

                teamAt = LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getTeam(search).orElse(null);

                if (teamAt == null) return search;

            }

            return location;
        }
    }

}
