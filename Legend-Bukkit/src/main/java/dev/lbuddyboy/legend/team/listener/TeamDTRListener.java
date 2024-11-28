package dev.lbuddyboy.legend.team.listener;

import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.api.TeamMemberDeathEvent;
import dev.lbuddyboy.legend.team.TeamHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class TeamDTRListener implements Listener {

    private final TeamHandler teamHandler = LegendBukkit.getInstance().getTeamHandler();

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        this.teamHandler.getTeam(player.getUniqueId()).ifPresent(team -> {
            TeamMemberDeathEvent deathEvent = new TeamMemberDeathEvent(player, team);

            LegendBukkit.getInstance().getServer().getPluginManager().callEvent(deathEvent);

            if (deathEvent.isCancelled()) return;

            team.applyDTRFreeze(new TimeDuration("30m").transform());
            team.setDeathsUntilRaidable(team.getDeathsUntilRaidable() - 1);
        });

    }

}
