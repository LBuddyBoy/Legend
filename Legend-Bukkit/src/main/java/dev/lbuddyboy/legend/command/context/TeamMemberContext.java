package dev.lbuddyboy.legend.command.context;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamMember;
import dev.lbuddyboy.legend.util.UUIDUtils;
import org.bukkit.entity.Player;

public class TeamMemberContext implements ContextResolver<TeamMember, BukkitCommandExecutionContext> {

    @Override
    public TeamMember getContext(BukkitCommandExecutionContext arg) throws InvalidCommandArgument {
        String source = arg.popFirstArg();

        Player player = arg.getPlayer();
        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(player).orElse(null);

        if (team != null) {
            TeamMember member = team.getMember(UUIDUtils.uuid(source)).orElse(null);

            if (member != null) return member;

            throw new InvalidCommandArgument(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.friendly")));
        }

        throw new InvalidCommandArgument(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
    }

}
