package dev.lbuddyboy.legend.command.completion;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.util.BukkitUtil;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class TeamCompletion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

    private String flag;

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        List<String> completions = new ArrayList<>();

        if (this.flag.equalsIgnoreCase("all")) {
            completions.addAll(LegendBukkit.getInstance().getTeamHandler().getTeams().stream().map(Team::getName).toList());
        } else if (this.flag.equalsIgnoreCase("player")) {
            completions.addAll(LegendBukkit.getInstance().getTeamHandler().getPlayerTeams().stream().map(Team::getName).toList());
        } else if (this.flag.equalsIgnoreCase("system")) {
            completions.addAll(LegendBukkit.getInstance().getTeamHandler().getPlayerTeams().stream().map(Team::getName).toList());
        }

        completions.addAll(BukkitUtil.getOnlinePlayers().stream().map(Player::getName).toList());

        return completions;
    }

}
