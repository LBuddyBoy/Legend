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
public class ClaimMapView {

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

    public ClaimMapView(Player player) {
        this.player = player;
    }

    public void clearClaims() {
        this.blockChanges.forEach(l -> player.sendBlockChange(l, Material.AIR, (byte) 0));
        this.blockChanges.clear();
    }

    public void updateClaims() {
        clearClaims();
        showTeams();
    }

    public void showTeams() {
        this.clearClaims();

        Map<Team, Material> teams = new HashMap<>();
        ClaimHandler claimHandler = LegendBukkit.getInstance().getTeamHandler().getClaimHandler();
        Set<Claim> near = claimHandler.getClaims(player.getLocation(), 50);

        for (Claim claim : near) {
            claim.getTeam().ifPresent(team -> {
                if (!teams.containsKey(team)) {
                    teams.put(team, getRandomMaterial(teams));
                }

                showClaim(claim, teams.get(team));
            });
        }

        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.map.viewing.header")
                .replaceAll("%claims%", String.valueOf(near.size()))
                .replaceAll("%teams%", String.valueOf(teams.size()))
        ));

        teams.forEach((key, value) -> player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.map.viewing.format")
                .replaceAll("%team%", key.getName())
                .replaceAll("%material%", ItemUtils.getName(value))
        )));
    }

    public void showClaim(Claim claim, Material material) {
        Cuboid cuboid = claim.getBounds();
        Material glassData = Material.GLASS;

        for (Block corner : cuboid.fourCorners()) {
            List<Location> pillar = createPillar(corner.getLocation());

            for (Location location : pillar) {
                player.sendBlockChange(location, location.getBlockY() % 5 == 0 ? material : glassData, (byte) 0);
                this.blockChanges.add(location);
            }
        }
    }

    public List<Location> createPillar(Location location) {
        World world = location.getWorld();
        List<Location> locations = new ArrayList<>();

        if (world == null) return new ArrayList<>();

        for (int i = 1; i < world.getMaxHeight(); i++) {
            Block block = world.getBlockAt(location.getBlockX(), i, location.getBlockZ());
            if (block.getType() != Material.AIR) continue;

            locations.add(block.getLocation());
        }

        return locations;
    }

    private Material getRandomMaterial(Map<Team, Material> map) {
        Material material = CLAIM_MATERIALS.get(ThreadLocalRandom.current().nextInt(CLAIM_MATERIALS.size()));

        if (map.containsValue(material)) return getRandomMaterial(map);

        return material;
    }

}
