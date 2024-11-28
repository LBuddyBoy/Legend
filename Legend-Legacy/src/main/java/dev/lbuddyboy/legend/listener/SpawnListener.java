package dev.lbuddyboy.legend.listener;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.api.PlayerClaimChangeEvent;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class SpawnListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Team team = LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getTeam(player.getLocation()).orElse(null);

        if (team == null) return;
        if (team.getTeamType() != TeamType.SPAWN) return;

        event.setCancelled(true);
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

}
