package dev.lbuddyboy.legend.team.model.claim;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.ClaimHandler;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.util.Cuboid;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class ClaimWallView {

    private static final List<Material> CLAIM_MATERIALS = Arrays.asList(
            Material.WOOD,
            Material.DIAMOND_BLOCK,
            Material.LAPIS_BLOCK,
            Material.REDSTONE_BLOCK,
            Material.EMERALD_BLOCK,
            Material.EMERALD_ORE,
            Material.COBBLESTONE,
            Material.STONE,
            Material.PRISMARINE,
            Material.MOSSY_COBBLESTONE,
            Material.BOOKSHELF,
            Material.DIRT,
            Material.DIODE,
            Material.GOLD_ORE,
            Material.DIAMOND_ORE,
            Material.GOLD_BLOCK
    );

    private final Player player;
    private final List<Location> blockChanges = new ArrayList<>();

    public ClaimWallView(Player player) {
        this.player = player;
    }

    public ClaimWall getClaimWall() {
        return Arrays.stream(ClaimWall.values()).filter(wall -> wall.getQualifier().test(this.player.getUniqueId())).findFirst().orElse(null);
    }

    public void clearWalls() {
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

    public void showWall(Claim claim) {
        Cuboid cuboid = claim.getBounds();
        Location location = player.getLocation();
        List<Block> walls = cuboid.getWalls(location.getBlockY() - 5, location.getBlockY() + 5);
        ClaimWall wall = getClaimWall();
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
