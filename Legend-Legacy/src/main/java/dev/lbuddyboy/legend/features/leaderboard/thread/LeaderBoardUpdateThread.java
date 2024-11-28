package dev.lbuddyboy.legend.features.leaderboard.thread;

import dev.lbuddyboy.legend.LegendBukkit;

public class LeaderBoardUpdateThread extends Thread {

    @Override
    public void run() {
        while (LegendBukkit.getInstance().isEnabled()) {
            try {
                LegendBukkit.getInstance().getLeaderBoardHandler().update();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep((60_000L * 15));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
