package dev.lbuddyboy.samurai.map.leaderboard;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.leaderboard.impl.KillLeaderBoardStat;
import lombok.Data;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.List;

@Data
public class RotatingHologram {

    private LeaderBoardHologram leaderBoardHologram;

    public RotatingHologram(Location location) {
        this.leaderBoardHologram = new LeaderBoardHologram(Samurai.getInstance().getLeaderBoardHandler().getStatByClass(KillLeaderBoardStat.class).get(), location);
        this.leaderBoardHologram.spawn();
    }

    public void rotate() {
        List<ILeaderBoardStat> stats = Samurai.getInstance().getLeaderBoardHandler().getLeaderBoardStats();
        int size = stats.size();
        int currentIndex = stats.indexOf(this.leaderBoardHologram.getType());
        this.leaderBoardHologram.setType(currentIndex + 1 >= size ? stats.getFirst() : stats.get(++currentIndex));
        leaderBoardHologram.update();
    }

}
