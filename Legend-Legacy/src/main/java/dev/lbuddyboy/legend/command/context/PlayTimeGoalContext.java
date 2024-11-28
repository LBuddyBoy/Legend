package dev.lbuddyboy.legend.command.context;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.commons.api.CommonsAPI;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;

import java.util.Optional;
import java.util.UUID;

public class TeamContext implements ContextResolver<Team, BukkitCommandExecutionContext> {

    @Override
    public Team getContext(BukkitCommandExecutionContext arg) throws InvalidCommandArgument {
        String source = arg.popFirstArg();

        /*
        Check for team names first.
         */

        Optional<Team> teamOpt = LegendBukkit.getInstance().getTeamHandler().getTeam(source);

        if (teamOpt.isPresent()) return teamOpt.get();

        /*
        Check for a players name as well.
         */

        UUID playerUUID = CommonsAPI.getInstance().getUUIDCache().getUUID(source);
        if (playerUUID != null) {
            teamOpt = LegendBukkit.getInstance().getTeamHandler().getTeam(playerUUID);

            if (teamOpt.isPresent()) return teamOpt.get();
        }

        throw new InvalidCommandArgument(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.not-existent")));
    }

}
