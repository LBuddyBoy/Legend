package dev.lbuddyboy.legend.user.model.nametag;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class LegendScoreboard {

    private final Scoreboard internal;
    private final Player player;
    private final String id;

    public LegendScoreboard(Player player) {
        this.player = player;
        this.internal = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        this.id = "legend-" + Integer.toHexString(ThreadLocalRandom.current().nextInt());

        for (ScoreboardEntryType entryType : ScoreboardEntryType.values()) {
            Team team = getInternal().registerNewTeam(entryType.getScoreboardTeamName());
            team.setColor(entryType.getColor());

            if (entryType.canAlwaysSeeNametag && !entryType.isObfuscated()) {
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            }

            if (entryType.isObfuscated()) {
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
                team.setCanSeeFriendlyInvisibles(false);
            }

            if (entryType.canSeeInvisibles) {
                team.setCanSeeFriendlyInvisibles(true);
            }
        }
    }

}