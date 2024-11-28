package dev.lbuddyboy.legend.team;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.claim.Claim;
import dev.lbuddyboy.legend.team.model.claim.ClaimMapView;
import dev.lbuddyboy.legend.team.model.claim.ClaimProcess;
import dev.lbuddyboy.legend.team.model.claim.GridCoordinate;
import dev.lbuddyboy.legend.util.Cuboid;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ClaimHandler implements IModule, Listener {

    private final Map<UUID, ClaimProcess> claimProcesses;
    private final Map<String, Multimap<GridCoordinate, Claim>> claims;
    private final Map<UUID, ClaimMapView> mapViews;

    private ItemStack claimWand;
    private int minimumSizeX, minimumSizeZ, maximumSizeX, maximumSizeZ, maximumClaims, bufferSize;

    public ClaimHandler() {
        this.claimProcesses = new HashMap<>();
        this.claims = new ConcurrentHashMap<>();
        this.mapViews = new ConcurrentHashMap<>();
    }

    @Override
    public void load() {
        this.claimWand = ItemUtils.itemStackFromConfigSect("team.claim.wand", LegendBukkit.getInstance().getSettings());
        this.minimumSizeX = LegendBukkit.getInstance().getSettings().getInt("team.claim.minimum-size.x");
        this.minimumSizeZ = LegendBukkit.getInstance().getSettings().getInt("team.claim.minimum-size.z");
        this.maximumSizeX = LegendBukkit.getInstance().getSettings().getInt("team.claim.maximum-size.x");
        this.maximumSizeZ = LegendBukkit.getInstance().getSettings().getInt("team.claim.maximum-size.z");
        this.maximumClaims = LegendBukkit.getInstance().getSettings().getInt("team.claim.maximum-claims");
        this.bufferSize = LegendBukkit.getInstance().getSettings().getInt("team.claim.buffer-size");

        LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(this, LegendBukkit.getInstance());

        Bukkit.getWorlds().forEach(this::registerWorld);

        LegendBukkit.getInstance().getTeamHandler().getTeams().forEach(team -> team.getClaims().forEach(claim -> setClaim(team, claim)));
    }

    @Override
    public void unload() {
        this.claims.clear();
    }

    public void stopClaimProcess(Player player) {
        this.claimProcesses.remove(player.getUniqueId());
        player.getInventory().remove(this.claimWand);
    }

    public void updateMapView(Player player) {
        ClaimMapView view = this.mapViews.get(player.getUniqueId());

        view.clearClaims();
        view.showTeams();
        player.setMetadata("CLAIM_MAP_VIEW_LOCATION", new FixedMetadataValue(LegendBukkit.getInstance(), player.getLocation()));
    }

    public void createMapView(Player player) {
        ClaimMapView view = new ClaimMapView(player);

        view.showTeams();
        this.mapViews.put(player.getUniqueId(), view);
        player.setMetadata("CLAIM_MAP_VIEW_LOCATION", new FixedMetadataValue(LegendBukkit.getInstance(), player.getLocation()));
    }

    public void removeMapView(Player player) {
        ClaimMapView view = this.mapViews.get(player.getUniqueId());

        if (view != null) {
            view.clearClaims();
        }

        this.mapViews.remove(player.getUniqueId());
        player.removeMetadata("CLAIM_MAP_VIEW_LOCATION", LegendBukkit.getInstance());
    }

    public Set<Claim> getClaims(Location location, int distance) {
        Location min = new Location(location.getWorld(), location.getBlockX() - distance, location.getBlockY(), location.getBlockZ() - distance);
        Location max = new Location(location.getWorld(), location.getBlockX() + distance, location.getBlockY(), location.getBlockZ() + distance);

        return getClaims(min, max);
    }

    public Set<Claim> getClaims(Location min, Location max) {
        Set<Claim> claims = new HashSet<>();
        int step = 1 << GridCoordinate.BITS;

        for (int x = min.getBlockX(); x < max.getBlockX() + step; x += step) {
            for (int z = min.getBlockZ(); z < max.getBlockZ() + step; z += step) {
                GridCoordinate coordinateSet = new GridCoordinate(x, z);

                for (Claim claim : this.claims.get(min.getWorld().getName()).get(coordinateSet)) {
                    if (!claims.contains(claim)) {
                        Cuboid bounds = claim.getBounds();
                        if ((max.getBlockX() >= bounds.getX1()) && (min.getBlockX() <= bounds.getX2())
                                && (max.getBlockZ() >= bounds.getZ1()) && (min.getBlockZ() <= bounds.getZ2())
                                && (max.getBlockY() >= bounds.getY1()) && (min.getBlockY() <= bounds.getY2())) {
                            claims.add(claim);
                        }
                    }
                }
            }
        }

        return claims;
    }

    public Optional<Team> getTeam(Location location) {
        return getClaim(location) != null ? getClaim(location).getTeam() : Optional.empty();
    }

    public Claim getClaim(Location location) {

        for (Claim data : this.claims.get(location.getWorld().getName()).get(new GridCoordinate(location.getBlockX(), location.getBlockZ()))) {
            if (data.getBounds().contains(location)) {
                return (data);
            }
        }

        return (null);
    }

    public void setClaim(Team team, Claim claim) {
        if (claim == null) {
            LegendBukkit.getInstance().getLogger().info("Error caching claim for " + team.getName() + ": Null claim");
            return;
        }

        int step = 1 << GridCoordinate.BITS;
        Cuboid bounds = claim.getBounds();

        for (int x = bounds.getX1(); x < bounds.getX2() + step; x += step) {
            for (int z = bounds.getZ1(); z < bounds.getZ2() + step; z += step) {
                Multimap<GridCoordinate, Claim> worldMap = this.claims.get(bounds.getWorldName());

                if (worldMap == null) {
                    continue;
                }

                worldMap.put(new GridCoordinate(x, z), claim);
            }
        }

        this.mapViews.values().forEach(ClaimMapView::updateClaims);
    }

    public void removeClaim(Claim claim) {
        if (claim == null) {
            LegendBukkit.getInstance().getLogger().info("Error removing claim: Null claim");
            return;
        }

        int step = 1 << GridCoordinate.BITS;
        Cuboid bounds = claim.getBounds();

        for (int x = bounds.getX1(); x < bounds.getX2() + step; x += step) {
            for (int z = bounds.getZ1(); z < bounds.getZ2() + step; z += step) {
                Multimap<GridCoordinate, Claim> worldMap = this.claims.get(bounds.getWorldName());

                if (worldMap == null) {
                    continue;
                }

                worldMap.remove(new GridCoordinate(x, z), claim);
            }
        }

        this.mapViews.values().forEach(ClaimMapView::updateClaims);
    }

    public boolean isClaimOverLapping(Claim claim) {
        Cuboid bounds = claim.getBounds();

        for (int x = bounds.getX1(); x <= bounds.getX2(); x++) {
            for (int z = bounds.getZ1(); z <= bounds.getZ2(); z++) {
                Location location = new Location(bounds.getWorld(), x, 100, z);
                Claim claimAt = getClaim(location);

                if (claimAt == null) continue;

                return true;
            }
        }

        return false;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        registerWorld(event.getWorld());
    }

    public void registerWorld(World world) {
        this.claims.put(world.getName(), HashMultimap.create());
    }

    public Optional<Team> getClaimingFor(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) return null;

        NBTItem item = new NBTItem(stack);

        return item.hasTag("claim-for") ? LegendBukkit.getInstance().getTeamHandler().getTeamById(item.getUUID("claim-for")) : Optional.empty();
    }

    public boolean isClaimWand(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) return false;

        return new NBTItem(stack).hasTag("legend-claim-wand");
    }

    public ItemStack createClaimWand() {
        NBTItem nbtItem = new NBTItem(this.claimWand.clone());

        nbtItem.setBoolean("legend-claim-wand", true);

        return nbtItem.getItem();
    }

    public ItemStack createClaimForWand(Team team) {
        NBTItem nbtItem = new NBTItem(new ItemFactory(createClaimWand()).addToLore(" ", "&aClaiming for team: " + team.getName() + " (" + team.getId() + ")").build());

        nbtItem.setUUID("claim-for", team.getId());

        return nbtItem.getItem();
    }

}
