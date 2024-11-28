package dev.lbuddyboy.legend.command.context;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.team.model.TeamType;

public class TeamTypeContext implements ContextResolver<TeamType, BukkitCommandExecutionContext> {

    @Override
    public TeamType getContext(BukkitCommandExecutionContext arg) throws InvalidCommandArgument {
        String source = arg.popFirstArg();

        try {
            return TeamType.valueOf(source);
        } catch (Exception ignored) {

        }

        throw new InvalidCommandArgument(CC.translate("&cThat team type does not exist."));
    }

}
