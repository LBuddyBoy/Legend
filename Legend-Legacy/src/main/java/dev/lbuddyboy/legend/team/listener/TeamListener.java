package dev.lbuddyboy.legend.team.listener;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.TeamHandler;
import dev.lbuddyboy.legend.team.model.Team;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Optional;

public class TeamListener implements Listener {

    private final TeamHandler teamHandler = LegendBukkit.getInstance().getTeamHandler();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        Player damager = null;
        if (event.getDamager() instanceof Player player) damager = player;
        if (event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player player) damager = player;
        if (damager == null) return;

        Optional<Team> teamOpt = this.teamHandler.getTeam(victim);
        if (teamOpt.isEmpty()) return;

        Team team = teamOpt.get();
        if (!team.isMember(damager.getUniqueId())) return;

        event.setCancelled(true);
        damager.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.same-team")
                .replaceAll("%victim%", victim.getName())
        ));
    }

}
