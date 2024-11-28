package dev.lbuddyboy.legend.features.kitmap.model;

import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.util.LocationUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

@AllArgsConstructor
@Data
public class ResetBlock {

    private Location location;
    private BlockData previousBlock;
    private long placedAt, duration;

    public boolean shouldReset() {
        boolean resets = this.placedAt + this.duration < System.currentTimeMillis();

        if (resets) {
            this.reset();
        }

        return resets;
    }

    public void reset() {
        this.location.getBlock().setBlockData(this.previousBlock);
        LegendBukkit.getInstance().getLogger().info("[Reset Block] Block has been reset " + LocationUtils.toString(location) + " to [" + this.previousBlock.getAsString() + "]");

    }

}
