package dev.lbuddyboy.legend.timer.impl;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.timer.PlayerTimer;
import dev.lbuddyboy.legend.util.BukkitUtil;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArcherTagTimer extends PlayerTimer {

    @Override
    public String getId() {
        return "archer-tag";
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player victim = (Player) event.getEntity();
        Player shooter = BukkitUtil.getDamager(event);

        if (shooter == null) return;
        if (!isActive(victim.getUniqueId())) return;

        System.out.println("Before: " + event.getDamage());
        event.setDamage(event.getDamage() * 1.25D);
        System.out.println("After: " + event.getDamage());
        System.out.println(" ");
    }

}
