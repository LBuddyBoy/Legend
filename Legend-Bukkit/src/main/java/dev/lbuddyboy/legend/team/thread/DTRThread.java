package dev.lbuddyboy.legend.team.thread;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;

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

                    team.regenDTR(LegendBukkit.getInstance().getSettings().getDouble("team.regeneration.dtr"));
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
