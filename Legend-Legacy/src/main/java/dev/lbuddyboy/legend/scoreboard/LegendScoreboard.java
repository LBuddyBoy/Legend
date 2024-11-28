package dev.lbuddyboy.legend.scoreboard;

import dev.lbuddyboy.commons.scoreboard.ScoreboardImpl;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.timer.PlayerTimer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LegendScoreboard implements ScoreboardImpl {

    @Override
    public boolean qualifies(Player player) {
        return true;
    }

    @Override
    public int getWeight() {
        return 100;
    }

    @Override
    public String getTitle(Player player) {
        return "Test Board";
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();

        for (PlayerTimer timer : LegendBukkit.getInstance().getTimerHandler().getScoreboardTimers()) {
            if (!timer.isActive(player.getUniqueId())) continue;

            lines.add(timer.getColoredName() + "&f: " + timer.getSecondaryColor() + timer.getRemaining(player.getUniqueId()));
        }

        return lines;
    }
}
