package dev.lbuddyboy.legend.timer.impl;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.api.PlayerClaimChangeEvent;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.timer.PlayerTimer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CombatTimer extends PlayerTimer {

    @Override
    public String getId() {
        return "combat";
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        Player damager = null;
        if (event.getDamager() instanceof Player player) damager = player;
        if (event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player player) damager = player;
        if (damager == null) return;

        apply(damager.getUniqueId());
        apply(victim.getUniqueId());
    }

    @EventHandler
    public void onClaimChange(PlayerClaimChangeEvent event) {
        Player player = event.getPlayer();
        if (!isActive(player.getUniqueId())) return;

        Team fromTeam = event.getFromTeam(), toTeam = event.getToTeam();

        if (toTeam != null && toTeam.getTeamType() == TeamType.SPAWN) {
            event.setCancelled(true);
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("combat-tagged")));
        }

    }

}
