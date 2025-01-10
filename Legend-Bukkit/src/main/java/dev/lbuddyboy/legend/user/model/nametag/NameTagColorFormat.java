package dev.lbuddyboy.legend.user.model.nametag;

import dev.lbuddyboy.arrow.staffmode.StaffModeConstants;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.timer.impl.ArcherTagTimer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.function.BiPredicate;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project Legend
 * @file dev.lbuddyboy.legend.nametag
 * @since 7/16/2024
 */

@AllArgsConstructor
@Getter
public enum NameTagColorFormat {

    TEAMMATE(ScoreboardEntryType.FRIENDLY, (viewer, target) -> LegendBukkit.getInstance().getTeamHandler().getTeam(target).map(team -> team.isMember(viewer.getUniqueId())).orElse(false)),
    ALLY(ScoreboardEntryType.ALLY, (viewer, target) -> LegendBukkit.getInstance().getTeamHandler().getTeam(target).map(team -> team.isAlly(viewer)).orElse(false)),
    FOCUSED(ScoreboardEntryType.FOCUS, (viewer, target) -> LegendBukkit.getInstance().getTeamHandler().getTeam(viewer).map(team -> (team.getFocusedTeam() != null && team.getFocusedTeam().isMember(target.getUniqueId())) || team.getFocusedPlayer() != null && team.getFocusedPlayer().equals(target.getUniqueId())).orElse(false)),
    DEFAULT(ScoreboardEntryType.DEFAULT, (viewer, target) -> true);

    private final ScoreboardEntryType scoreboardEntryType;
    private final BiPredicate<Player, Player> predicate;

}
