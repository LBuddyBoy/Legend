package dev.lbuddyboy.legend.listener;

import com.google.common.collect.ImmutableSet;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.settings.Setting;
import dev.lbuddyboy.legend.team.model.Team;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Set;

public class DiamondListener implements Listener {

    public static final Set<BlockFace> CHECK_FACES = ImmutableSet.of(
            BlockFace.NORTH,
            BlockFace.SOUTH,
            BlockFace.EAST,
            BlockFace.WEST,
            BlockFace.NORTH_EAST,
            BlockFace.NORTH_WEST,
            BlockFace.SOUTH_EAST,
            BlockFace.SOUTH_WEST,
            BlockFace.UP,
            BlockFace.DOWN);

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.DIAMOND_ORE) {
            event.getBlock().setMetadata("DiamondPlaced", new FixedMetadataValue(LegendBukkit.getInstance(), true));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.DIAMOND_ORE && !event.getBlock().hasMetadata("DiamondPlaced")) {
            int diamonds = countRelative(event.getBlock());

            Team playerTeam = LegendBukkit.getInstance().getTeamHandler().getTeam(event.getPlayer()).orElse(null);
            if (playerTeam != null) {
                /*playerTeam.setDiamondsMined(playerTeam.getDiamondsMined() + diamonds);*/
            }

            for (Player player : LegendBukkit.getInstance().getServer().getOnlinePlayers()) {
                if (!Setting.DIAMOND_ALERT.isToggled(player)) continue;

                player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("found-diamonds")
                        .replaceAll("%player%", event.getPlayer().getName())
                        .replaceAll("%amount%", String.valueOf(diamonds))
                ));
            }
        }
    }

    public int countRelative(Block block) {
        int diamonds = 1;
        block.setMetadata("DiamondPlaced", new FixedMetadataValue(LegendBukkit.getInstance(), true));

        for (BlockFace checkFace : CHECK_FACES) {
            Block relative = block.getRelative(checkFace);

            if (relative.getType() == Material.DIAMOND_ORE && !relative.hasMetadata("DiamondPlaced")) {
                relative.setMetadata("DiamondPlaced", new FixedMetadataValue(LegendBukkit.getInstance(), true));
                diamonds += countRelative(relative);
            }
        }

        return (diamonds);
    }

}
