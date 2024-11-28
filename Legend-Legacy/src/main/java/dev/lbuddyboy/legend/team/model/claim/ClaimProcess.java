package dev.lbuddyboy.legend.team.model.claim;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.util.Cuboid;
import lombok.Getter;
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

    public void showPillars(Player player) {
        this.clearPillars(player);

        if (this.positionOne != null) showPillar(player, this.positionOne.clone(), Material.WOOL, 4, Material.STAINED_GLASS, 4);
        if (this.positionTwo != null) showPillar(player, this.positionTwo.clone(), Material.WOOL, 6, Material.STAINED_GLASS, 6);
    }

    private void showPillar(Player player, Location location, Material materialOne, int dataOne, Material materialTwo, int dataTwo) {
        World world = location.getWorld();
        if (world == null) return;

        for (int i = 1; i < world.getMaxHeight(); i++) {
            Block block = world.getBlockAt(location.getBlockX(), i, location.getBlockZ());
            if (block.getType() != Material.AIR) continue;

            this.blockChanges.add(block.getLocation());

            if (i % 5 == 0) {
                player.sendBlockChange(block.getLocation(), materialOne, (byte) dataOne);
                continue;
            }

            player.sendBlockChange(block.getLocation(), materialTwo, (byte) dataTwo);
        }
    }

    public void clearPillars(Player player) {
        this.blockChanges.forEach(l -> player.sendBlockChange(l, Material.AIR, (byte) 0));
        this.blockChanges.clear();
    }

    public Cuboid getSelection() {
        return new Cuboid(this.positionOne, this.positionTwo);
    }

    public void setPositionOne(Location positionOne) {
        this.positionOne = positionOne;
        this.positionOne.setY(0);
    }

    public void setPositionTwo(Location positionTwo) {
        this.positionTwo = positionTwo;
        this.positionTwo.setY(this.positionTwo.getWorld().getMaxHeight());
    }

    public double getPrice() {
        return LegendBukkit.getInstance().getSettings().getDouble("team.claim.price-per-block") * (getSelection().getSizeX() * getSelection().getSizeZ());
    }

}
