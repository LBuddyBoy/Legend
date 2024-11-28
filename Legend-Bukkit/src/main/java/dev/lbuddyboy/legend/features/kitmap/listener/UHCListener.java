package dev.lbuddyboy.legend.features.kitmap.listener;

import dev.lbuddyboy.legend.LegendConstants;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class UHCListener implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();



    }

}
