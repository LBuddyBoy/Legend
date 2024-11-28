package dev.lbuddyboy.legend.timer.impl;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.api.PlayerClaimChangeEvent;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.timer.PlayerTimer;
import org.bukkit.Location;
import org.bukkit.World;
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

public class CombatTimer extends PlayerTimer {

    private final Map<UUID, Long> messageDelay = new HashMap<>();

    @Override
    public String getId() {
        return "combat";
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player victim = (Player) event.getEntity();

        Player damager = null;
        if (event.getDamager() instanceof Player) damager = (Player) event.getDamager();
        if (event.getDamager() instanceof Projectile && ((Projectile)event.getDamager()).getShooter() instanceof Player) damager = (Player) ((Projectile) event.getDamager()).getShooter();

        if (damager == null) return;

        apply(damager.getUniqueId());
        apply(victim.getUniqueId());
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();

        if (isActive(player.getUniqueId())) {
            event.setCancelled(true);

            if (messageDelay.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis()) return;

            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("combat-tagged.portal")));
            messageDelay.put(player.getUniqueId(), System.currentTimeMillis() + 1_000L);
            return;
        }

        messageDelay.remove(player.getUniqueId());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        messageDelay.remove(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        messageDelay.remove(event.getPlayer().getUniqueId());
    }

}
