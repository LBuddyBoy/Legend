package dev.lbuddyboy.legend.team.thread;

import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.api.TeamNoLongerRaidableEvent;
import dev.lbuddyboy.legend.team.model.Team;
import org.bukkit.Bukkit;

import java.util.List;

public class DTRThread extends Thread {

    @Override
    public void run() {
        while (true) {
            try {
                List<Team> teams = LegendBukkit.getInstance().getTeamHandler().getPlayerTeams();

                for (Team team : teams) {
                    if (team.isDTRFrozen() || team.isFullyRegenerated()) continue;
                    if (team.getNextDTRRegen() > 0) continue;

                    double toAdd = LegendBukkit.getInstance().getSettings().getDouble("team.regeneration.dtr");

                    team.regenDTR(toAdd);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1_000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
