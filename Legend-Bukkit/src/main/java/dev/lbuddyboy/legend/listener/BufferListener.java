package dev.lbuddyboy.legend.listener;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.LegendConstants;
import dev.lbuddyboy.legend.team.listener.TeamClaimListener;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Cow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class BufferListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (LegendConstants.isWarzone(block.getLocation())) return;
        if (LegendConstants.isAdminBypass(player)) return;

        if (LegendConstants.isUHCKitMap(block.getLocation())) {
            if (LegendBukkit.getInstance().getKitMapHandler().isResetBlock(block.getLocation())) {
                LegendBukkit.getInstance().getKitMapHandler().removeResetBlock(block.getLocation());
                return;
            }
        }

        event.setCancelled(true);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("buffer.cant-break")
                .replaceAll("%buffer%", APIConstants.formatNumber(LegendConstants.getBuffer(block.getWorld())))
        ));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (LegendConstants.isWarzone(block.getLocation())) return;
        if (LegendConstants.isAdminBypass(player)) return;

        if (LegendConstants.isUHCKitMap(block.getLocation())) {
            LegendBukkit.getInstance().getKitMapHandler().cacheResetBlock(
                    block.getLocation(),
                    Material.AIR.createBlockData(),
                    10_000L
            );
            return;
        }

        event.setCancelled(true);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("buffer.cant-place")
                .replaceAll("%buffer%", APIConstants.formatNumber(LegendConstants.getBuffer(block.getWorld())))
        ));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Block block = event.getBlockClicked();
        Player player = event.getPlayer();

        if (LegendConstants.isWarzone(block.getLocation())) return;
        if (LegendConstants.isAdminBypass(player)) return;

        if (LegendConstants.isUHCKitMap(event.getBlock().getLocation())) {
            LegendBukkit.getInstance().getKitMapHandler().cacheResetBlock(
                    event.getBlock().getLocation(),
                    event.getBlock().getBlockData(),
                    10_000L
            );
            return;
        }

        event.setCancelled(true);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("buffer.cant-place")
                .replaceAll("%buffer%", APIConstants.formatNumber(LegendConstants.getBuffer(block.getWorld())))
        ));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBucketFill(PlayerBucketFillEvent event) {
        Block block = event.getBlockClicked();
        Player player = event.getPlayer();

        if (LegendConstants.isWarzone(block.getLocation())) return;
        if (LegendConstants.isAdminBypass(player)) return;

        if (LegendConstants.isUHCKitMap(event.getBlock().getLocation())) {
            if (LegendBukkit.getInstance().getKitMapHandler().isResetBlock(event.getBlock().getLocation())) {
                LegendBukkit.getInstance().getKitMapHandler().removeResetBlock(event.getBlock().getLocation());
                return;
            }
        }

        List<LivingEntity> entities = TeamClaimListener.getEntities(player);
        for (LivingEntity entity : entities) {
            if (!TeamClaimListener.getLookingAt(player, entity)) continue;
            if (entity instanceof Cow) return;
        }

        event.setCancelled(true);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("buffer.cant-place")
                .replaceAll("%buffer%", APIConstants.formatNumber(LegendConstants.getBuffer(block.getWorld())))
        ));
    }

/*
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Block clicked = event.getClickedBlock();
        Player player = event.getPlayer();

        if (clicked == null) return;
        if (!event.getAction().name().contains("RIGHT_CLICK_")) return;
        if (!clicked.getType().isInteractable()) return;
        if (LegendConstants.isWarzone(clicked.getLocation())) return;
        if (LegendConstants.isAdminBypass(player)) return;
        if (clicked.getType() == Material.AIR) return;

        event.setUseInteractedBlock(Event.Result.DENY);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("buffer.cant-interact")
                .replaceAll("%buffer%", APIConstants.formatNumber(LegendConstants.getBuffer(clicked.getWorld())))
        ));
    }*/

    @EventHandler
    public void onBlockChange(BlockFadeEvent event) {
        Block block = event.getBlock();

        if (LegendConstants.isWarzone(block.getLocation())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        Block block = event.getBlock();

        if (LegendConstants.isWarzone(block.getLocation())) return;

        if (LegendConstants.isUHCKitMap(event.getBlock().getLocation())) {
            LegendBukkit.getInstance().getKitMapHandler().cacheResetBlock(
                    block.getLocation(),
                    block.getBlockData(),
                    10_000L
            );
            return;
        }

        event.setCancelled(true);
    }

}
