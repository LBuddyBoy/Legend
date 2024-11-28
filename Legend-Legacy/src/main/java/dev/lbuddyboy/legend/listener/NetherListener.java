package dev.lbuddyboy.legend.listener;

import dev.lbuddyboy.legend.LegendBukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftTravelAgent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.PortalCreateEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NetherPortalListener implements Listener {

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo(), from = event.getFrom();

        if (to.getWorld().getEnvironment() != World.Environment.NETHER) return;

        event.useTravelAgent(true);

    }

    @EventHandler
    public void onCreate(PortalCreateEvent event) {
        List<Block> blocks = event.getBlocks();
        List<Block> editedBlocks = new ArrayList<>(blocks);
        
        if (!LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getTeam(blocks.get(0).getLocation()).isPresent()) return;

        int x = blocks.get(0).getLocation().getBlockX(), z = blocks.get(0).getLocation().getBlockZ();

        boolean south = z > 0 && x > 0 || z > 0 && x < 0;
        boolean north = z < 0 && x > 0 || z > 0 && x < 0;
        boolean west = x < 0 && z > 0 || x > 0 && z < 0;
        boolean east = x > 0 && z > 0 || x > 0 && z < 0;

        for (int add = -12; add <= 12; add++) {
            editedBlocks.clear();
            int finalAdd = add;
            editedBlocks.addAll(blocks.stream().map(b -> b.getRelative((north || south ? finalAdd : 0), 0, (west || east ? finalAdd : 0))).collect(Collectors.toList()));
        }

        event.getBlocks().clear();
        event.getBlocks().addAll(editedBlocks);

    }

}
