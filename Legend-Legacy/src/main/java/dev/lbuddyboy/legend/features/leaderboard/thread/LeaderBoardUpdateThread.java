package dev.lbuddyboy.samurai.map.leaderboard.thread;

import dev.lbuddyboy.samurai.Samurai;

public class LeaderBoardUpdateThread extends Thread {

    @Override
    public void run() {
        while (Samurai.getInstance().isEnabled()) {
            try {
                Samurai.getInstance().getLeaderBoardHandler().update();
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
