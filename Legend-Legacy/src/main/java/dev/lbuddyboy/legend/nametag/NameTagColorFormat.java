package dev.lbuddyboy.legend.nametag;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;
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
        Optional<Team> teamOpt = LegendBukkit.getInstance().getTeamHandler().getTeam(viewer);

        return teamOpt.map(team -> team.isMember(viewer.getUniqueId())).orElse(false);
    }),
    ALLY(ChatColor.DARK_AQUA, (viewer, target) -> {
        Optional<Team> teamOpt = LegendBukkit.getInstance().getTeamHandler().getTeam(viewer);

        return teamOpt.map(team -> team.isMember(viewer.getUniqueId())).orElse(false);
    });

    private final ChatColor color;
    private final BiPredicate<Player, Player> predicate;

}
