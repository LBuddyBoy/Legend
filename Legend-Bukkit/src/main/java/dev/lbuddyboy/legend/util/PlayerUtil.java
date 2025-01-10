package dev.lbuddyboy.legend.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;

public class PlayerUtil {

    public static Player getLastDamageCause(Player victim) {
        Player killer = null;
        EntityDamageEvent var3 = victim.getLastDamageCause();

        if (var3 instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
            killer = getLastDamageCause(entityDamageByEntityEvent);
        }

        return killer;
    }

    public static Player getLastDamageCause(EntityDamageByEntityEvent event) {
        Player damager = null;
        Entity var5 = event.getDamager();
        if (var5 instanceof Projectile projectile) {
            ProjectileSource var6 = projectile.getShooter();
            if (var6 instanceof Player shooter) {
                damager = shooter;
                return damager;
            }
        }

        var5 = event.getDamager();
        if (var5 instanceof Player shooter) {
            damager = shooter;
        }

        return damager;
    }
}
