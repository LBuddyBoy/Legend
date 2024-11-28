package dev.lbuddyboy.legend.team.listener;

import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.api.TeamMemberDeathEvent;
import dev.lbuddyboy.legend.team.TeamHandler;
import dev.lbuddyboy.legend.team.model.log.impl.TeamDTRChangeLog;
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

            double previousDTR = team.getDeathsUntilRaidable();

            team.applyDTRFreeze(new TimeDuration(LegendBukkit.getInstance().getSettings().getString("team.regeneration.cooldown", "30m")).transform());
            team.setDeathsUntilRaidable(team.getDeathsUntilRaidable() - LegendBukkit.getInstance().getSettings().getDouble("team.regeneration.loss", 1.0D));
            team.createTeamLog(new TeamDTRChangeLog(previousDTR, team.getDeathsUntilRaidable(), player.getUniqueId(), TeamDTRChangeLog.ChangeCause.MEMBER_DEATH));
            team.flagSave();
        });

    }

}
