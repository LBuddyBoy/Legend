package dev.lbuddyboy.legend.listener;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.LegendConstants;
import dev.lbuddyboy.legend.team.listener.TeamClaimListener;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BufferListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (!LegendConstants.isWilderness(block.getLocation())) return;
        if (LegendConstants.isAdminBypass(player)) return;

        event.setCancelled(true);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("buffer.cant-break")
                .replaceAll("%buffer%", APIConstants.formatNumber(LegendConstants.getBuffer(block.getWorld())))
        ));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (!LegendConstants.isWilderness(block.getLocation())) return;
        if (LegendConstants.isAdminBypass(player)) return;

        event.setCancelled(true);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("buffer.cant-place")
                .replaceAll("%buffer%", APIConstants.formatNumber(LegendConstants.getBuffer(block.getWorld())))
        ));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Block block = event.getBlockClicked();
        Player player = event.getPlayer();

        if (!LegendConstants.isWilderness(block.getLocation())) return;
        if (LegendConstants.isAdminBypass(player)) return;

        event.setCancelled(true);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("buffer.cant-place")
                .replaceAll("%buffer%", APIConstants.formatNumber(LegendConstants.getBuffer(block.getWorld())))
        ));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBucketFill(PlayerBucketFillEvent event) {
        Block block = event.getBlockClicked();
        Player player = event.getPlayer();

        if (!LegendConstants.isWilderness(block.getLocation())) return;
        if (LegendConstants.isAdminBypass(player)) return;

        event.setCancelled(true);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("buffer.cant-place")
                .replaceAll("%buffer%", APIConstants.formatNumber(LegendConstants.getBuffer(block.getWorld())))
        ));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Block clicked = event.getClickedBlock();
        Player player = event.getPlayer();

        if (clicked == null) return;
        if (!event.getAction().name().contains("RIGHT_CLICK_")) return;
        if (!TeamClaimListener.INTERACTABLES.contains(clicked.getType())) return;
        if (!LegendConstants.isWilderness(clicked.getLocation())) return;
        if (LegendConstants.isAdminBypass(player)) return;

        event.setUseInteractedBlock(Event.Result.DENY);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("buffer.cant-interact")
                .replaceAll("%buffer%", APIConstants.formatNumber(LegendConstants.getBuffer(clicked.getWorld())))
        ));
    }

}
