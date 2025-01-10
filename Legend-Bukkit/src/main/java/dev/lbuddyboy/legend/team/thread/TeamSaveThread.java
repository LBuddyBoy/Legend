package dev.lbuddyboy.legend.team.thread;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.brew.BrewResult;
import dev.lbuddyboy.legend.team.model.brew.BrewType;

import java.util.List;

public class TeamSaveThread extends Thread {

    @Override
    public void run() {
        while (true) {
            try {
                List<Team> teams = LegendBukkit.getInstance().getTeamHandler().getTeams();

                teams.stream().forEach(team -> {
                    if (!team.isEdited()) return;

                    LegendBukkit.getInstance().getTeamHandler().saveTeam(team, true);
                });

                for (Team team : LegendBukkit.getInstance().getTeamHandler().getPlayerTeams()) {
                    if (team.getOnlinePlayers().isEmpty()) continue;

                    if (team.getRalliedAt() > 0) {
                        if (team.getRalliedAt() + (60_000L * 5) < System.currentTimeMillis()) {
                            team.setRallyLocation(null);
                        }
                    }

                    if (!SettingsConfig.SETTINGS_DISABLE_TEAM_BREW.getBoolean()) {
                        for (BrewType type : BrewType.values()) {
                            if (!team.getBrewData().isBrewing(type)) continue;

                            BrewResult result = team.getBrewData().attemptBrew(team, type);

                            if (result == BrewResult.INVALID_MATERIALS) {
                                team.getBrewData().getStartedBrewing().put(type, false);
                                team.flagSave();
                                continue;
                            }
                            if (result == BrewResult.BREW_DELAY) continue;

                            team.getBrewData().brewPotion(type);
                            team.flagSave();
                        }
                    }

                    if (!SettingsConfig.SETTINGS_DISABLE_TEAM_GENERATOR.getBoolean()) {
                        team.getGeneratorData().cleanUpGeneratedItems(team);
                        team.getGeneratorData().tick(team);
                    }
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
