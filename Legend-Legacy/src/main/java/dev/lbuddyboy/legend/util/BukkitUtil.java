package dev.lbuddyboy.legend.util;

import dev.lbuddyboy.arrow.staffmode.StaffModeConstants;
import dev.lbuddyboy.arrow.staffmode.model.StaffMode;
import dev.lbuddyboy.legend.LegendBukkit;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;
import java.util.stream.Collectors;

public class BukkitUtil {

    public static List<? extends Player> getOnlinePlayers() {
        return LegendBukkit.getInstance().getServer().getOnlinePlayers().stream().filter(p -> {
            return !p.hasMetadata(StaffModeConstants.VANISH_META_DATA);
        }).collect(Collectors.toList());
    }

    public static void broadcast(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {

        }
    }

    public static Player getDamager(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;
            Player damager = null;

            if (entityDamageByEntityEvent.getDamager() instanceof Projectile && ((Projectile)entityDamageByEntityEvent.getDamager()).getShooter() instanceof Player) {
                damager = (Player) ((Projectile) entityDamageByEntityEvent.getDamager()).getShooter();
            } else if (entityDamageByEntityEvent.getDamager() instanceof Player) {
                damager = (Player) entityDamageByEntityEvent.getDamager();
            }

            return damager;
        }

        return null;
    }

    public static String getWorldName(World world) {
        if (world.getEnvironment() == World.Environment.NETHER) return "&cNether";
        if (world.getEnvironment() == World.Environment.THE_END) return "&5The End";

        return "&aOverworld";
    }

}
