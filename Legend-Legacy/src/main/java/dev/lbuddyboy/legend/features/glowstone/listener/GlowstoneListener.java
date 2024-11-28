package dev.lbuddyboy.legend.glowstone.listener;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.glowstone.GlowstoneHandler;
import dev.lbuddyboy.legend.team.model.Team;
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

        if (block.getType() != Material.GLOWSTONE) return;
        if (!this.glowstoneHandler.isSetup()) return;

        Team teamAt = LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getTeam(block.getLocation()).orElse(null);
        if (teamAt == null) return;
        if (!teamAt.getName().equals("Glowstone")) return;

        event.setCancelled(true);
        block.breakNaturally(player.getItemInHand());
        this.glowstoneHandler.setBlocksLeft(this.glowstoneHandler.getBlocksLeft() - 1);
    }

}
