package dev.lbuddyboy.legend.listener;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BufferListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (!isInBuffer(block.getLocation())) return;

        event.setCancelled(true);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("buffer.cant-break")
                .replaceAll("%buffer%", APIConstants.formatNumber(getBuffer(block.getWorld())))
        ));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (!isInBuffer(block.getLocation())) return;

        event.setCancelled(true);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("buffer.cant-place")
                .replaceAll("%buffer%", APIConstants.formatNumber(getBuffer(block.getWorld())))
        ));
    }

    @EventHandler(ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (!isInBuffer(block.getLocation())) return;

        event.setCancelled(true);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("buffer.cant-place")
                .replaceAll("%buffer%", APIConstants.formatNumber(getBuffer(block.getWorld())))
        ));
    }

    @EventHandler(ignoreCancelled = true)
    public void onBucketFill(PlayerBucketFillEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (!isInBuffer(block.getLocation())) return;

        event.setCancelled(true);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("buffer.cant-place")
                .replaceAll("%buffer%", APIConstants.formatNumber(getBuffer(block.getWorld())))
        ));
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Block clicked = event.getClickedBlock();
        Player player = event.getPlayer();

        if (clicked == null) return;
        if (!clicked.getType().isInteractable()) return;
        if (!isInBuffer(clicked.getLocation())) return;

        event.setUseInteractedBlock(Event.Result.DENY);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("buffer.cant-interact")
                .replaceAll("%buffer%", APIConstants.formatNumber(getBuffer(clicked.getWorld())))
        ));
    }

    public int getBuffer(World world) {
        int buffer = LegendBukkit.getInstance().getSettings().getInt("buffer.overworld");

        if (world.getEnvironment() == World.Environment.NETHER) buffer = LegendBukkit.getInstance().getSettings().getInt("buffer.nether");
        if (world.getEnvironment() == World.Environment.THE_END) buffer = LegendBukkit.getInstance().getSettings().getInt("buffer.end");

        return buffer;
    }

    public boolean isInBuffer(Location location) {
        World world = location.getWorld();
        int buffer = getBuffer(world);
        int x = location.getBlockX(), z = location.getBlockZ();

        return x >= -buffer && x <= buffer && z >= -buffer && z <= buffer;
    }

}
