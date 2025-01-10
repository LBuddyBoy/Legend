package dev.lbuddyboy.legend.features.gateways.model;

import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.util.Cuboid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Selection {
    public static final ItemStack SELECTION_WAND = new ItemFactory(Material.IRON_HOE).displayName("&dGateway Wand").build();
    public static final String SELECTION_METADATA_KEY = "gateways_selection";
    private Location loc1;
    private Location loc2;

    public Cuboid getCuboid() {
        if (!this.isComplete()) {
            return null;
        }
        return new Cuboid(this.loc1, this.loc2);
    }

    public boolean isComplete() {
        return this.loc1 != null && this.loc2 != null;
    }

    public static Selection getOrCreateSelection(Player player) {
        if (player.hasMetadata(SELECTION_METADATA_KEY)) {
            return (Selection) player.getMetadata(SELECTION_METADATA_KEY).get(0).value();
        }
        Selection selection = new Selection();
        player.setMetadata(SELECTION_METADATA_KEY, new FixedMetadataValue(LegendBukkit.getInstance(), selection));
        return selection;
    }

}
