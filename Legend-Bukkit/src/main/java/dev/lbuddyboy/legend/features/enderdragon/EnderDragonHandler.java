package dev.lbuddyboy.legend.features.enderdragon;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.loottable.impl.LootTable;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.LocationUtils;
import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.enderdragon.listener.EnderDragonListener;
import dev.lbuddyboy.legend.features.enderdragon.model.CustomEnderDragon;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class EnderDragonHandler implements IModule {

    private final Map<UUID, Double> teamDamage = new HashMap<>();
    private final Map<UUID, Double> individualDamage = new HashMap<>();
    private CustomEnderDragon enderDragon;
    private LootTable lootTable;

    @Override
    public void load() {
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() != World.Environment.THE_END) continue;

            if (world.getEnderDragonBattle() != null) {
                world.getEnderDragonBattle().getBossBar().removeAll();
                if (world.getEnderDragonBattle().getEnderDragon() != null) {
                    world.getEnderDragonBattle().getEnderDragon().remove();
                }
            }
        }

        this.lootTable = new LootTable("enderdragon");
        this.lootTable.register();

        Bukkit.getPluginManager().registerEvents(new EnderDragonListener(), LegendBukkit.getInstance());

    }

    @Override
    public void unload() {
        this.despawnDragon();
    }

    public void despawnDragon() {
        if (this.enderDragon == null) {
            return;
        }

        this.teamDamage.clear();
        this.individualDamage.clear();
        this.enderDragon.remove(Entity.RemovalReason.DISCARDED);
        this.enderDragon = null;
    }

    public void spawnDragon(Location location, double health) {
        if (this.enderDragon != null) {
            return;
        }

        if (location == null || location.getWorld() == null) {
            LegendBukkit.getInstance().getLogger().warning("Couldn't spawn dragon because location or world was not found.");
            return;
        }

        ServerLevel level = ((CraftWorld)location.getWorld()).getHandle();

        this.teamDamage.clear();
        this.individualDamage.clear();
        this.enderDragon = new CustomEnderDragon(location);
        this.enderDragon.setPos(location.getX(), location.getY(), location.getZ());
        this.enderDragon.getAttribute(Attributes.MAX_HEALTH).setBaseValue(health);
        this.enderDragon.setHealth(this.enderDragon.getMaxHealth());

        level.addFreshEntity(this.enderDragon, CreatureSpawnEvent.SpawnReason.CUSTOM);

        Arrays.asList(
                " ",
                        "&7███████",
                        "&7█&5█████&7█",
                        "&7█&5█&7█████",
                        "&7█&5█&7█████ <blend:&5;&d>&lEnder Dragon Event</>",
                        "&7█&5████&7██ &aThe ender dragon has spawned!",
                        "&7█&5█&7█████ &e" + LocationUtils.toString(location) + " &d[The End]",
                        "&7█&5█&7█████",
                        "&7█&5█████&7█",
                        "&7███████",
                        " "
        ).forEach(s -> Bukkit.broadcastMessage(CC.translate(s)));
    }

}
