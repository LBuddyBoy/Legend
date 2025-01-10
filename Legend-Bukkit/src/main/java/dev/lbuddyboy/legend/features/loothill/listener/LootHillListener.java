package dev.lbuddyboy.legend.features.loothill.listener;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.glowstone.GlowstoneHandler;
import dev.lbuddyboy.legend.features.loothill.LootHillHandler;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class LootHillListener implements Listener {

    private final LootHillHandler lootHillHandler = LegendBukkit.getInstance().getLootHillHandler();

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getType() != Material.DIAMOND_BLOCK) return;
        if (!this.lootHillHandler.isSetup()) return;

        Team teamAt = LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getTeam(block.getLocation()).orElse(null);
        if (teamAt == null) return;
        if (teamAt.getTeamType() != TeamType.LOOTHILL) return;

        event.setCancelled(true);
        block.setType(Material.AIR);
        this.lootHillHandler.setBlocksLeft(this.lootHillHandler.getBlocksLeft() - 1);
        this.lootHillHandler.getLootTable().open(player);
    }

}
