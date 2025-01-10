package dev.lbuddyboy.legend.util;

import dev.lbuddyboy.arrow.staffmode.StaffModeConstants;
import dev.lbuddyboy.arrow.staffmode.model.StaffMode;
import dev.lbuddyboy.legend.LegendBukkit;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;
import java.util.stream.Collectors;

public class BukkitUtil {

    public static List<Player> getOnlinePlayers() {
        return LegendBukkit.getInstance().getServer().getOnlinePlayers().stream().map(p -> ((Player)p)).filter(p -> !CitizensAPI.getNPCRegistry().isNPC(p)).collect(Collectors.toList());
    }

    public static List<? extends Player> getStaffPlayers() {
        return LegendBukkit.getInstance().getServer().getOnlinePlayers().stream().filter(p -> {
            return p.hasPermission("arrow.staff");
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

    /**
     * @param d Number to round up
     *
     * @return The number passed as parameter rounded up if it is positive. Returns 0 if it is negative.
     */
    public static int roundUpPositive(final double d) {
        int i = (int) d;
        final double remainder = d - i;
        if (remainder > 0.0)
            i++;

        return Math.max(i, 0);
    }

    /**
     * @param d   Number to round up
     * @param max Upper limit
     *
     * @return {@code max} if the number passed as parameter exceeds the maximum while it returns 0 if it is negative. If
     * {@literal d > 0} and {@literal d <= max} then the number is rounded up and returned.
     * @see #roundUpPositive(double)
     */
    public static int roundUpPositiveWithMax(final double d, final int max) {
        return d > max ? max : roundUpPositive(d);
    }

}
