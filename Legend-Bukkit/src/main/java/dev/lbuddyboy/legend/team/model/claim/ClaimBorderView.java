package dev.lbuddyboy.legend.team.model.claim;

import dev.lbuddyboy.legend.util.Cuboid;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class ClaimBorderView {

    private final Player player;
    private final List<Location> blockChanges = new ArrayList<>();

    public ClaimBorderView(Player player) {
        this.player = player;
    }

    public ClaimBorder getClaimWall() {
        return Arrays.stream(ClaimBorder.values()).filter(wall -> wall.getQualifier().test(this.player.getUniqueId())).findFirst().orElse(null);
    }

    public void clearBorders() {
        if (this.blockChanges.isEmpty()) return;

        Iterator<Location> iterator = this.blockChanges.iterator();
        while (iterator.hasNext()) {
            Location location = iterator.next();
            if (getClaimWall() != null) {
                if (location.distance(player.getLocation()) <= 7.0) {
                    continue;
                }
            }
            player.sendBlockChange(location, location.getBlock().getType(), location.getBlock().getData());
            iterator.remove();
        }
    }

    public void showBorder(Claim claim) {
        Cuboid cuboid = claim.getBounds();
        Location location = player.getLocation();
        List<Block> walls = cuboid.getWalls(location.getBlockY() - 3, location.getBlockY() + 3);
        ClaimBorder wall = getClaimWall();
        if (wall == null) return;

        for (Block block : walls) {
            if (block.getLocation().distance(location) > 7) continue;
            if (!block.getChunk().isLoaded()) continue;
            if (block.getType() != Material.AIR) continue;

            this.blockChanges.add(block.getLocation());
            player.sendBlockChange(block.getLocation(), wall.getMaterialData().getItemType(), wall.getMaterialData().getData());
        }
    }

}
