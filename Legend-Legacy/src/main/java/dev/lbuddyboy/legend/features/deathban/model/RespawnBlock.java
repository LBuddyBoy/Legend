package dev.lbuddyboy.legend.features.deathban.model;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.model.LegendUser;
import lombok.AllArgsConstructor;
import org.bukkit.Location;

@AllArgsConstructor
public class RespawnBlock {

    private Location location;
    private long brokenAt;

    public boolean shouldRespawn() {
        return this.brokenAt + (LegendBukkit.getInstance().getDeathbanHandler().getRespawnDelay() * 1000L) < System.currentTimeMillis();
    }

    public void respawn() {
        this.location.getBlock().setType(LegendBukkit.getInstance().getDeathbanHandler().getBreakMaterial());
    }

}
