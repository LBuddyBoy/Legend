package dev.lbuddyboy.legend.command.context;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.team.model.TeamRole;
import dev.lbuddyboy.legend.team.model.TeamType;

public class TeamRoleContext implements ContextResolver<TeamRole, BukkitCommandExecutionContext> {

    @Override
    public TeamRole getContext(BukkitCommandExecutionContext arg) throws InvalidCommandArgument {
        String source = arg.popFirstArg();

        try {
            return TeamRole.valueOf(source);
        } catch (Exception ignored) {

        }

        throw new InvalidCommandArgument(CC.translate("&cThat team role does not exist."));
    }

}
