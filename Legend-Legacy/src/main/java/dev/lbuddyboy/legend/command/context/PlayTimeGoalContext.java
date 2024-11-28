package dev.lbuddyboy.legend.command.context;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.commons.api.CommonsAPI;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.playtime.model.PlayTimeGoal;
import dev.lbuddyboy.legend.team.model.Team;

import java.util.Optional;
import java.util.UUID;

public class PlayTimeGoalContext implements ContextResolver<PlayTimeGoal, BukkitCommandExecutionContext> {

    @Override
    public PlayTimeGoal getContext(BukkitCommandExecutionContext arg) throws InvalidCommandArgument {
        String source = arg.popFirstArg();
        PlayTimeGoal playTimeGoal = LegendBukkit.getInstance().getPlayTimeGoalHandler().getPlayTimeGoals().get(source.toLowerCase());

        if (playTimeGoal != null) return playTimeGoal;

        throw new InvalidCommandArgument(CC.translate("&cThat playtime goal does not exist!"));
    }

}
