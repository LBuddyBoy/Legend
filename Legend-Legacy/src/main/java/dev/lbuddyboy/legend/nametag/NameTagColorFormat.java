package dev.lbuddyboy.legend.nametag;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.timer.impl.ArcherTagTimer;
import dev.lbuddyboy.legend.timer.impl.InvincibilityTimer;
import dev.lbuddyboy.legend.timer.server.SOTWTimer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;
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

    //PERSONAL(ChatColor.DARK_GREEN, (viewer, target) -> target == viewer),
    TEAMMATE(ChatColor.DARK_GREEN, (viewer, target) -> {
        Optional<Team> teamOpt = LegendBukkit.getInstance().getTeamHandler().getTeam(target);

        return teamOpt.map(team -> team.isMember(viewer.getUniqueId())).orElse(false);
    }),
    ALLY(ChatColor.DARK_AQUA, (viewer, target) -> {
        Optional<Team> teamOpt = LegendBukkit.getInstance().getTeamHandler().getTeam(target);

        return teamOpt.map(team -> team.isAlly(viewer)).orElse(false);
    }),
    PVP_TIMER(ChatColor.YELLOW, (viewer, target) -> {
        InvincibilityTimer invincibilityTimer = LegendBukkit.getInstance().getTimerHandler().getTimer(InvincibilityTimer.class);
        SOTWTimer sotwTimer = LegendBukkit.getInstance().getTimerHandler().getServerTimer(SOTWTimer.class);

        return invincibilityTimer.isActive(target.getUniqueId()) || (sotwTimer.isActive() && !sotwTimer.isEnabled(target));
    }),
    ARCHER_TAG(ChatColor.GREEN, (viewer, target) -> {
        return LegendBukkit.getInstance().getTimerHandler().getTimer(ArcherTagTimer.class).isActive(target.getUniqueId());
    });

    private final ChatColor color;
    private final BiPredicate<Player, Player> predicate;

}
