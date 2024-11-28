package dev.lbuddyboy.legend.team.model.claim;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.util.Cuboid;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ClaimProcess {

    private Location positionOne, positionTwo;
    private final List<Location> blockChanges = new ArrayList<>();
    @Setter private boolean exact;

    public void showPillars(Player player) {
        this.clearPillars(player);

        if (this.positionOne != null) showPillar(player, this.positionOne.clone(), Material.YELLOW_WOOL, Material.YELLOW_STAINED_GLASS);
        if (this.positionTwo != null) showPillar(player, this.positionTwo.clone(), Material.PINK_WOOL, Material.PINK_STAINED_GLASS);
    }

    private void showPillar(Player player, Location location, Material materialOne, Material materialTwo) {
        World world = location.getWorld();
        if (world == null) return;

        for (int i = 1; i < world.getMaxHeight(); i++) {
            Block block = world.getBlockAt(location.getBlockX(), i, location.getBlockZ());
            if (block.getType().isSolid()) continue;

            this.blockChanges.add(block.getLocation());

            if (i % 5 == 0) {
                player.sendBlockChange(block.getLocation(), materialOne.createBlockData());
                continue;
            }

            player.sendBlockChange(block.getLocation(), materialTwo.createBlockData());
        }
    }

    public void clearPillars(Player player) {
        this.blockChanges.forEach(l -> player.sendBlockChange(l, Material.AIR.createBlockData()));
        this.blockChanges.clear();
    }

    public Cuboid getSelection() {
        return new Cuboid(this.positionOne, this.positionTwo);
    }

    public void setPositionOne(Location positionOne) {
        this.positionOne = positionOne;
        if (!this.exact) this.positionOne.setY(0);
    }

    public void setPositionTwo(Location positionTwo) {
        this.positionTwo = positionTwo;
        if (!this.exact) this.positionTwo.setY(this.positionTwo.getWorld().getMaxHeight());
    }

    public double getPrice() {
        return LegendBukkit.getInstance().getSettings().getDouble("team.claim.price-per-block") * (getSelection().getSizeX() * getSelection().getSizeZ());
    }

}
