package dev.lbuddyboy.legend.features.gateways.model;

import dev.lbuddyboy.commons.gson.serialize.CuboidSerializer;
import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.commons.util.LocationUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.util.Cuboid;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

@Data
public class Gateway {

    private final Config config;
    private String id, displayName;
    private Cuboid entranceRegion, exitRegion;
    private Location entranceLocation, exitLocation;

    public Gateway(String id) {
        this.config = new Config(LegendBukkit.getInstance(), id, LegendBukkit.getInstance().getGatewayHandler().getDirectory());
        this.id = id;
        this.displayName = id;

        save();
    }

    public Gateway(Config config) {
        this.config = config;
        this.id = config.getFileName().replaceAll(".yml", "");
        this.displayName = config.getString("displayName");
        if (config.contains("entrance.min")) this.entranceRegion = new Cuboid(LocationUtils.deserializeString(config.getString("entrance.min")), LocationUtils.deserializeString(config.getString("entrance.max")));
        if (config.contains("exit.min")) this.exitRegion = new Cuboid(LocationUtils.deserializeString(config.getString("exit.min")), LocationUtils.deserializeString(config.getString("exit.max")));
        if (config.contains("teleport.entrance")) this.entranceLocation = LocationUtils.deserializeString(config.getString("teleport.entrance"));
        if (config.contains("teleport.exit")) this.exitLocation = LocationUtils.deserializeString(config.getString("teleport.exit"));
    }

    public void save() {
        if (this.entranceRegion != null) {
            this.config.set("entrance.min", LocationUtils.serializeString(this.entranceRegion.getLowerNE()));
            this.config.set("entrance.max", LocationUtils.serializeString(this.entranceRegion.getUpperSW()));
        }
        if (this.exitRegion != null) {
            this.config.set("exit.min", LocationUtils.serializeString(this.exitRegion.getLowerNE()));
            this.config.set("exit.max", LocationUtils.serializeString(this.exitRegion.getUpperSW()));
        }

        if (this.entranceLocation != null) this.config.set("teleport.entrance", LocationUtils.serializeString(this.entranceLocation));
        if (this.exitLocation != null) this.config.set("teleport.exit", LocationUtils.serializeString(this.exitLocation));

        this.config.save();
    }

    public void delete() {
        this.setEntranceRegion(null);
        this.setExitRegion(null);

        if (this.config.getFile().exists()) this.config.getFile().delete();
    }

    public void setEntranceRegion(Cuboid cuboid) {
        if (this.entranceRegion != null) {
            for (Block block : this.entranceRegion) {
                block.setType(Material.AIR);
            }
        }

        this.entranceRegion = cuboid;

        if (this.entranceRegion != null) {
            for (Block block : this.entranceRegion) {
                if (!block.getType().isAir()) continue;

                block.setType(Material.END_GATEWAY);
            }
        }
    }

    public void setExitRegion(Cuboid cuboid) {
        if (this.exitRegion != null) {
            for (Block block : this.exitRegion) {
                block.setType(Material.AIR);
            }
        }

        this.exitRegion = cuboid;

        if (this.exitRegion != null) {
            for (Block block : this.exitRegion) {
                if (!block.getType().isAir()) continue;

                block.setType(Material.END_GATEWAY);
            }
        }
    }

}
