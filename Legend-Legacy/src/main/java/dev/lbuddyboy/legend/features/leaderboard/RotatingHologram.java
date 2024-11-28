package dev.lbuddyboy.legend.features.leaderboard;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.leaderboard.impl.KillLeaderBoardStat;
import lombok.Data;
import org.bukkit.Location;

import java.util.List;

@Data
public class RotatingHologram {

    private LeaderBoardHologram leaderBoardHologram;

    public RotatingHologram(Location location) {
        this.leaderBoardHologram = new LeaderBoardHologram(LegendBukkit.getInstance().getLeaderBoardHandler().getStatByClass(KillLeaderBoardStat.class), location);
        this.leaderBoardHologram.spawn();
    }

    public void rotate() {
        List<ILeaderBoardStat> stats = LegendBukkit.getInstance().getLeaderBoardHandler().getLeaderBoardStats();
        int size = stats.size();
        int currentIndex = stats.indexOf(this.leaderBoardHologram.getType());
        this.leaderBoardHologram.setType(currentIndex + 1 >= size ? stats.get(0) : stats.get(++currentIndex));
        leaderBoardHologram.update();
    }

}
