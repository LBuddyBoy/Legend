package dev.lbuddyboy.legend.team.thread;

import dev.lbuddyboy.legend.LegendBukkit;

public class TeamTopThread extends Thread {

    @Override
    public void run() {
        while (true) {
            try {
                LegendBukkit.getInstance().getTeamHandler().updateTopTeams();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(60_000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
