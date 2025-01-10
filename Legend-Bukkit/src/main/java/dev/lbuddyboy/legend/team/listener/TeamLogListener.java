package dev.lbuddyboy.legend.team.listener;

import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.deathmessage.DeathMessageProvider;
import dev.lbuddyboy.commons.deathmessage.event.PlayerKilledEvent;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.team.TeamHandler;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.log.impl.TeamMemberDeathLog;
import dev.lbuddyboy.legend.team.model.log.impl.TeamPointsChangeLog;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;

public class TeamLogListener implements Listener {

    private final TeamHandler teamHandler = LegendBukkit.getInstance().getTeamHandler();

    @EventHandler
    public void onDeath(PlayerKilledEvent event) {
        Player victim = event.getVictim(), killer = event.getKiller();
        String deathMessage = "";

        DeathMessageProvider provider = CommonsPlugin.getInstance().getDeathMessageHandler().getProvider();

        if (!provider.getLastDeathMessage(victim).equalsIgnoreCase("N/A")) {
            deathMessage = provider.getLastDeathMessage(victim);
        }

        Team victimTeam = this.teamHandler.getTeam(victim.getUniqueId()).orElse(null);

        if (victimTeam != null) {
            victimTeam.setPoints(
                    victimTeam.getPoints() + SettingsConfig.POINTS_DEATH.getInt(),
                    victim.getUniqueId(),
                    TeamPointsChangeLog.ChangeCause.DEATH
            );

            victimTeam.createTeamLog(new TeamMemberDeathLog(victimTeam.getId(), victim.getUniqueId(), deathMessage));
        }

        if (killer != null) {
            if (SettingsConfig.KITMAP_KILL_REWARDS_ENABLED.getBoolean()) {
                List<String> commands = LegendBukkit.getInstance().getSettings().getStringList("kitmap.kill-reward.commands");

                commands.forEach(s -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replaceAll("%player%", killer.getName())));
            }

            Team killerTeam = this.teamHandler.getTeam(killer.getUniqueId()).orElse(null);
            if (killerTeam == null) return;

            killerTeam.setPoints(
                    killerTeam.getPoints() + SettingsConfig.POINTS_KILL.getInt(),
                    killer.getUniqueId(),
                    TeamPointsChangeLog.ChangeCause.KILL
            );
        }

    }

}
