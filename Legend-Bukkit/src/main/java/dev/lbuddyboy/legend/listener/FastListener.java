package dev.lbuddyboy.legend.listener;

import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BrewingStartEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;

public class FastListener implements Listener {

    @EventHandler
    public void onBurn(FurnaceBurnEvent event) {
        Furnace furnace = (Furnace) event.getBlock().getState();
        furnace.setCookTimeTotal(60);
    }

    @EventHandler
    public void onSmelt(FurnaceSmeltEvent event) {
        Furnace furnace = (Furnace) event.getBlock().getState();
        furnace.setCookTimeTotal(60);
    }

    @EventHandler
    public void onBrew(BrewingStartEvent event) {
        if (!SettingsConfig.SETTINGS_FAST_BREW.getBoolean()) return;

        event.setTotalBrewTime(100);
    }

}
