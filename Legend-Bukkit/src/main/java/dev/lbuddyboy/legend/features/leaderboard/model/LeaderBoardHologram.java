package dev.lbuddyboy.legend.features.leaderboard;

import dev.lbuddyboy.arrow.Arrow;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.commons.util.LocationUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Data
public class LeaderBoardHologram implements Cloneable {

    private ILeaderBoardStat type;
    private Location location = null;
    private Hologram hologram;
    private List<HologramLine> placeLines = new ArrayList<>(), otherLines = new ArrayList<>();

    public LeaderBoardHologram(ILeaderBoardStat type) {
        this.type = type;
    }

    public LeaderBoardHologram(ILeaderBoardStat type, Location location) {
        this.type = type;
        this.location = location;
    }

    public LeaderBoardHologram(String key, Config config) {
        this.type = LegendBukkit.getInstance().getLeaderBoardHandler().getStatById(key).get();

        String locationString = config.getString("holograms." + key);
        if (locationString.equalsIgnoreCase("none")) return;

        this.location = LocationUtils.deserializeString(locationString);
    }

    public void spawn() {
        if (this.location == null) return;

        this.hologram = DHAPI.createHologram(UUID.randomUUID().toString(), this.location, false);

        DHAPI.addHologramLine(this.hologram, this.type.getMenuItem().getType());

        DHAPI.addHologramLine(this.hologram, "");
        otherLines.add(DHAPI.addHologramLine(this.hologram, ItemUtils.getName(this.type.getMenuItem())));
        DHAPI.addHologramLine(this.hologram, "");
        for (int i = 1; i <= 10; i++) {
            placeLines.add(DHAPI.addHologramLine(this.hologram, CC.translate("&e#" + i + ") &cN/A: &f0")));
        }

        DHAPI.addHologramLine(this.hologram, "");
    }

    public void despawn() {
        this.hologram.delete();
    }

    public void update() {
        if (this.location == null) return;
        if (this.hologram == null) return;

        DHAPI.moveHologram(this.hologram, this.location);
        this.otherLines.get(0).setText(ItemUtils.getName(this.type.getMenuItem()));

        int position = 1;
        for (Map.Entry<UUID, Integer> entry : LegendBukkit.getInstance().getLeaderBoardHandler().getLeaderBoard(this.type.getClass())) {
            HologramLine line = placeLines.get(position - 1);
            line.setText(CC.translate("&e#" + position + ") &7" + Arrow.getInstance().getUUIDCache().getName(entry.getKey()) + ": &f" + type.format(Double.valueOf(entry.getValue()))));
            position++;
        }

        this.hologram.updateAll();
    }

    public void save() {
        LegendBukkit.getInstance().getLeaderBoardHandler().getConfig().set("holograms." + this.type.getId(), this.location == null ? "none" : LocationUtils.serializeString(this.location));
        LegendBukkit.getInstance().getLeaderBoardHandler().getConfig().save();
    }

    @Override
    public LeaderBoardHologram clone() {
        try {
            LeaderBoardHologram clone = (LeaderBoardHologram) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
