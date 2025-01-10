package dev.lbuddyboy.legend.timer.impl;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.timer.PlayerTimer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public class EnderPearlTimer extends PlayerTimer {

    @Override
    public String getId() {
        return "enderpearl";
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof EnderPearl pearl)) return;
        if (!(pearl.getShooter() instanceof Player player)) return;

        if (!isActive(player.getUniqueId())) {
            apply(player.getUniqueId());
            return;
        }

        event.setCancelled(true);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("enderpearl-cooldown")
                .replaceAll("%cooldown%", getRemainingSeconds(player.getUniqueId()))
        ));
    }

    @EventHandler
    public void onLand(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof EnderPearl pearl)) return;
        if (!(pearl.getShooter() instanceof Player shooter)) return;
        if (!TeamType.SPAWN.appliesAt(pearl.getLocation())) return;

        event.setCancelled(true);
        remove(shooter.getUniqueId());
        shooter.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Location to = event.getTo(), from = event.getFrom();

        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;
        if (!TeamType.SPAWN.appliesAt(to)) return;
        if (TeamType.SPAWN.appliesAt(from)) return;

        event.setCancelled(true);
    }

}
