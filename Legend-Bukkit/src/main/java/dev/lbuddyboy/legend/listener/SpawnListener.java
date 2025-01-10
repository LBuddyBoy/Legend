package dev.lbuddyboy.legend.listener;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.api.PlayerClaimChangeEvent;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.util.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.entity.WindCharge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SpawnListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Team team = LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getTeam(player.getLocation()).orElse(null);

        if (team == null) return;
        if (team.getTeamType() != TeamType.SPAWN) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        Player damager = PlayerUtil.getLastDamageCause(event);

        if (TeamType.SPAWN.appliesAt(victim.getLocation()) || (damager != null && TeamType.SPAWN.appliesAt(damager.getLocation()))) {
            event.setCancelled(true);
            return;
        }

    }

    @EventHandler
    public void onFoodLoss(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        Team team = LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getTeam(player.getLocation()).orElse(null);

        if (team == null) return;
        if (team.getTeamType() != TeamType.SPAWN) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onClaimChange(PlayerClaimChangeEvent event) {
        Player player = event.getPlayer();
        Team fromTeam = event.getFromTeam(), toTeam = event.getToTeam();

        if (toTeam != null && toTeam.getTeamType() == TeamType.SPAWN) {
            player.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof WindCharge charge)) return;
        if (!(charge.getShooter() instanceof Player shooter)) return;
        if (!TeamType.SPAWN.appliesAt(shooter.getLocation())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onLand(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof WindCharge charge)) return;
        if (!(charge.getShooter() instanceof Player shooter)) return;
        if (!TeamType.SPAWN.appliesAt(charge.getLocation())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onRodCaughtPlayer(PlayerFishEvent event) {
        if (!(event.getCaught() instanceof Player caught)) return;
        if (!TeamType.SPAWN.appliesAt(caught.getLocation())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onRodThrow(PlayerFishEvent event) {
        Player player = event.getPlayer();

        if (!TeamType.SPAWN.appliesAt(player.getLocation())) return;

        event.setCancelled(true);
    }

}
