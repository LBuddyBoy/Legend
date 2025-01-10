package dev.lbuddyboy.legend.features.oremountain.listener;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.loothill.LootHillHandler;
import dev.lbuddyboy.legend.features.oremountain.OreMountainHandler;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class OreMountainListener implements Listener {

    private final OreMountainHandler oreMountainHandler = LegendBukkit.getInstance().getOreMountainHandler();

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!block.getType().name().endsWith("_ORE")) return;
        if (!this.oreMountainHandler.isSetup()) return;

        Team teamAt = LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getTeam(block.getLocation()).orElse(null);
        if (teamAt == null) return;
        if (teamAt.getTeamType() != TeamType.ORE_MOUNTAIN) return;

        event.setCancelled(true);
        block.breakNaturally(player.getInventory().getItemInMainHand());
        block.setType(Material.AIR);
        this.oreMountainHandler.setBlocksLeft(this.oreMountainHandler.getBlocksLeft() - 1);
    }

}
