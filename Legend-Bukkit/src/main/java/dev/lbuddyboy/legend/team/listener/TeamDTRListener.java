package dev.lbuddyboy.legend.team.listener;

import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.api.TeamMemberDeathEvent;
import dev.lbuddyboy.legend.api.TeamSetRaidableEvent;
import dev.lbuddyboy.legend.team.TeamHandler;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.log.impl.TeamDTRChangeLog;
import dev.lbuddyboy.legend.team.model.log.impl.TeamNowRaidableLog;
import dev.lbuddyboy.legend.team.model.log.impl.TeamPointsChangeLog;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.text.DecimalFormat;
import java.util.UUID;

public class TeamDTRListener implements Listener {

    private final TeamHandler teamHandler = LegendBukkit.getInstance().getTeamHandler();

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        this.teamHandler.getTeam(player.getUniqueId()).ifPresent(team -> {
            TeamMemberDeathEvent deathEvent = new TeamMemberDeathEvent(player, team);

            LegendBukkit.getInstance().getServer().getPluginManager().callEvent(deathEvent);

            if (deathEvent.isCancelled()) return;

            double previousDTR = team.getDeathsUntilRaidable();
            double newDTR = previousDTR - SettingsConfig.TEAM_DTR_LOSS.getDouble();

            if (previousDTR > 0 && newDTR <= 0) {
                UUID killerUUID = null;

                if (killer != null) {
                    killerUUID = killer.getUniqueId();

                    this.teamHandler.getTeam(killerUUID).ifPresent(killerTeam -> {
                        killerTeam.setKills(killerTeam.getKills() + 1);
                    });
                }

                Bukkit.getPluginManager().callEvent(new TeamSetRaidableEvent(team, player.getUniqueId(), killerUUID));
            }

            team.applyDTRFreeze(SettingsConfig.TEAM_REGEN_COOLDOWN.getTimeDuration().transform());
            team.setDeathsUntilRaidable(newDTR);
            team.setDeaths(team.getDeaths() + 1);
            team.createTeamLog(new TeamDTRChangeLog(team.getId(), previousDTR, team.getDeathsUntilRaidable(), player.getUniqueId(), TeamDTRChangeLog.ChangeCause.MEMBER_DEATH));

            team.flagSave();
        });

    }

    @EventHandler
    public void onRaidable(TeamSetRaidableEvent event) {
        Team team = event.getTeam();
        UUID killerUUID = event.getKillerUUID();
        UUID victimUUID = event.getVictimUUID();

        if (killerUUID != null) {
            this.teamHandler.getTeam(killerUUID).ifPresent(killerTeam -> {
                killerTeam.setPoints(
                        killerTeam.getPoints() + SettingsConfig.POINTS_MADE_RADIABLE.getInt(),
                        killerUUID,
                        TeamPointsChangeLog.ChangeCause.MADE_RAIDABLE
                );
                killerTeam.setMadeRaidable(killerTeam.getMadeRaidable() + 1);
            });
        }

        team.createTeamLog(new TeamNowRaidableLog(team.getId(), victimUUID, killerUUID, TeamNowRaidableLog.RaidableCause.PLAYER));
        team.setPoints(
                team.getPoints() + SettingsConfig.POINTS_WENT_RAIDABLE.getInt(),
                victimUUID,
                TeamPointsChangeLog.ChangeCause.WENT_RAIDABLE
        );
    }

}
