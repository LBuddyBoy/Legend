package dev.lbuddyboy.legend.listener;

import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import net.minecraft.server.v1_8_R3.PotionBrewer;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Furnace;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBrewingStand;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftFurnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;

public class FastListener implements Listener {

    @EventHandler
    public void onBurn(FurnaceBurnEvent event) {
        Furnace furnace = (Furnace) event.getBlock().getState();
        furnace.setCookTime((short) 135);
    }

    @EventHandler
    public void onSmelt(FurnaceSmeltEvent event) {
        Furnace furnace = (Furnace) event.getBlock().getState();
        furnace.setCookTime((short) 135);
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        BrewingStand stand = (BrewingStand) event.getBlock().getState();

        stand.removeMetadata("started_brewing", LegendBukkit.getInstance());
    }

    @EventHandler
    public void onMove(InventoryMoveItemEvent event) {
        Inventory initiator = event.getInitiator(), destination = event.getDestination();

        if (!(destination instanceof BrewerInventory)) return;
        BrewerInventory brewerInventory = (BrewerInventory) destination;
        BrewingStand stand = brewerInventory.getHolder();

        if (stand.hasMetadata("started_brewing")) {
            if (brewerInventory.getIngredient() == null || brewerInventory.getIngredient().getType() == Material.AIR) {
                stand.removeMetadata("started_brewing", LegendBukkit.getInstance());
            } else if (brewerInventory.getContents() == null || Arrays.stream(brewerInventory.getContents()).noneMatch(t -> t != null && (t.getType() == Material.POTION || t.getType() == Material.GLASS_BOTTLE))) {
                stand.removeMetadata("started_brewing", LegendBukkit.getInstance());
            } else {
                return;
            }
        }

        Tasks.run(() -> {
            stand.setBrewingTime(100);
            stand.setMetadata("started_brewing", new FixedMetadataValue(LegendBukkit.getInstance(), System.currentTimeMillis()));
        });
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof BrewerInventory)) return;
        BrewerInventory brewerInventory = (BrewerInventory) event.getInventory();
        BrewingStand stand = brewerInventory.getHolder();

        if (stand.hasMetadata("started_brewing")) {
            if (brewerInventory.getIngredient() == null || brewerInventory.getIngredient().getType() == Material.AIR) {
                stand.removeMetadata("started_brewing", LegendBukkit.getInstance());
            } else if (brewerInventory.getContents() == null || Arrays.stream(brewerInventory.getContents()).noneMatch(t -> t != null && (t.getType() == Material.POTION || t.getType() == Material.GLASS_BOTTLE))) {
                stand.removeMetadata("started_brewing", LegendBukkit.getInstance());
            } else {
                return;
            }
        }

        Tasks.run(() -> {
            stand.setBrewingTime(100);
            stand.setMetadata("started_brewing", new FixedMetadataValue(LegendBukkit.getInstance(), System.currentTimeMillis()));
        });
    }

}
