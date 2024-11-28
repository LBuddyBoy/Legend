package dev.lbuddyboy.legend.team.model.claim;

import dev.lbuddyboy.commons.util.LocationUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.thread.ClaimBorderThread;
import dev.lbuddyboy.legend.util.Coordinate;
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
                if (location.distanceSquared(player.getLocation()) <= ClaimBorderThread.REGION_DISTANCE_SQUARED) {
                    continue;
                }
            }
            player.sendBlockChange(location, location.getBlock().getType().createBlockData());
            iterator.remove();
        }
    }

    public void showBorder(Claim claim) {
        Cuboid cuboid = claim.getBounds();
        ClaimBorder wall = getClaimWall();

        if (wall == null) return;

        LegendBukkit.getInstance().getLogger().info(" ");

        Cuboid.BorderIterator iterator = cuboid.borderIterator();

        while (iterator.hasNext()) {
            Coordinate coordinate = iterator.next();

            Location onPlayerY = new Location(player.getWorld(), coordinate.getX(), player.getLocation().getY(), coordinate.getZ());

            if (onPlayerY.distanceSquared(player.getLocation()) > ClaimBorderThread.REGION_DISTANCE_SQUARED) {
                continue;
            }

            for (int i = -4; i < 5; i++) {
                Location check = onPlayerY.clone().add(0, i, 0);
                if (!check.getWorld().isChunkLoaded(check.getBlockX() >> 4, check.getBlockZ() >> 4)) continue;
                if (check.getBlock().getType().isSolid()) continue;
                if (check.distanceSquared(onPlayerY) > ClaimBorderThread.REGION_DISTANCE_SQUARED) continue;

                this.blockChanges.add(check);
                player.sendBlockChange(check, Material.RED_STAINED_GLASS.createBlockData());
            }
        }

    }

}
