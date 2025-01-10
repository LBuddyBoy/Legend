package dev.lbuddyboy.legend.features.glowstone.listener;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.glowstone.GlowstoneHandler;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class GlowstoneListener implements Listener {

    private final GlowstoneHandler glowstoneHandler = LegendBukkit.getInstance().getGlowstoneHandler();

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getType() != Material.GLOWSTONE && block.getType() != Material.ANCIENT_DEBRIS) return;
        if (!this.glowstoneHandler.isSetup()) return;

        Team teamAt = LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getTeam(block.getLocation()).orElse(null);
        if (teamAt == null) return;
        if (teamAt.getTeamType() != TeamType.GLOWSTONE_MOUNTAIN) return;

        event.setCancelled(true);
        block.breakNaturally(player.getInventory().getItemInMainHand());
        this.glowstoneHandler.setBlocksLeft(this.glowstoneHandler.getBlocksLeft() - 1);

        if (this.glowstoneHandler.getConfig().getBoolean("placeholderBlock", false)) {
            block.setType(Material.OBSIDIAN);
        }
    }

}
