package dev.lbuddyboy.legend.team.thread;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;

import java.util.List;

public class TeamSaveThread extends Thread {

    @Override
    public void run() {
        while (LegendBukkit.isENABLED()) {
            try {
                List<Team> teams = LegendBukkit.getInstance().getTeamHandler().getTeams();

                teams.stream().filter(Team::isEdited).forEach(team -> LegendBukkit.getInstance().getTeamHandler().saveTeam(team, true));
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
