package dev.lbuddyboy.legend.command.completion;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.util.BukkitUtil;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class TeamCompletion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

    private String flag;

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        List<String> completions = new ArrayList<>();

        if (this.flag.equalsIgnoreCase("all")) {
            completions.addAll(LegendBukkit.getInstance().getTeamHandler().getTeams().stream().map(Team::getName).collect(Collectors.toList()));
        } else if (this.flag.equalsIgnoreCase("player")) {
            completions.addAll(LegendBukkit.getInstance().getTeamHandler().getPlayerTeams().stream().map(Team::getName).collect(Collectors.toList()));
        } else if (this.flag.equalsIgnoreCase("system")) {
            completions.addAll(LegendBukkit.getInstance().getTeamHandler().getSystemTeams().stream().map(Team::getName).collect(Collectors.toList()));
        }

        completions.addAll(BukkitUtil.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        completions.sort(new TeamComparator());

        return completions;
    }

    public class TeamComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            int weight1 = Bukkit.getPlayer(o1) != null ? 0 : 1;
            int weight2 = Bukkit.getPlayer(o2) != null ? 0 : 1;

            return Integer.compare(weight1, weight2);
        }
    }

}
