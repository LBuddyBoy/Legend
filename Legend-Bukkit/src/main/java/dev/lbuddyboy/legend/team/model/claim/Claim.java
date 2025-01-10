package dev.lbuddyboy.legend.team.model.claim;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.util.Cuboid;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Optional;
import java.util.UUID;

@Data
public class Claim {

    private UUID id;
    private UUID owner;
    private String world;
    private int x1, y1, z1, x2, y2, z2;

    private transient Cuboid bounds;

    public Claim(UUID owner, Location l1, Location l2) {
        this.id = UUID.randomUUID();
        this.owner = owner;
        this.bounds = new Cuboid(l1, l2);
        this.x1 = bounds.getX1();
        this.y1 = bounds.getY1();
        this.z1 = bounds.getZ1();
        this.x2 = bounds.getX2();
        this.y2 = bounds.getY2();
        this.z2 = bounds.getZ2();
        this.world = bounds.getWorldName();
    }

    public String getName() {
        return this.getTeam().map(Team::getName).orElse(null);
    }

    public Optional<Team> getTeam() {
        return LegendBukkit.getInstance().getTeamHandler().getTeamById(this.owner);
    }

    public Cuboid getBounds() {
        if (bounds == null) {
            World world = Bukkit.getWorld(this.world);
            if (world == null) return null;

            this.bounds = new Cuboid(world, this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
        }

        return this.bounds;
    }

}
