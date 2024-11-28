package dev.lbuddyboy.legend.features.leaderboard.comparator;

import dev.lbuddyboy.legend.features.leaderboard.LeaderBoardUser;

import java.util.Comparator;

public class LeaderBoardComparator implements Comparator<LeaderBoardUser> {
    @Override
    public int compare(LeaderBoardUser o1, LeaderBoardUser o2) {
        return Double.compare(o1.getScore(), o2.getScore());
    }
}
