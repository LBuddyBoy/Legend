package dev.lbuddyboy.legend.team.listener;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.TeamHandler;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.util.BukkitUtil;
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
        if (!(event.getEntity() instanceof Player)) return;
        Player victim = (Player) event.getEntity();
        Player damager = BukkitUtil.getDamager(event);

        if (damager == null) return;
        if (damager.equals(victim)) return;

        Optional<Team> teamOpt = this.teamHandler.getTeam(victim);
        if (!teamOpt.isPresent()) return;

        Team team = teamOpt.get();
        if (!team.isMember(damager.getUniqueId()) && !team.isAlly(damager)) return;

        event.setCancelled(true);

        if (team.isAlly(damager)) {
            damager.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.ally")
                    .replaceAll("%victim%", victim.getName())
            ));
            return;
        }

        damager.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.same-team")
                .replaceAll("%victim%", victim.getName())
        ));
    }

}
