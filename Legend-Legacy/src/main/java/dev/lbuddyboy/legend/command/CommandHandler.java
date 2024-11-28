package dev.lbuddyboy.legend.command;

import co.aikar.commands.PaperCommandManager;
import dev.lbuddyboy.arrow.command.param.TimeParam;
import dev.lbuddyboy.arrow.command.param.UUIDParam;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.command.completion.TeamCompletion;
import dev.lbuddyboy.legend.command.completion.TeamMemberCompletion;
import dev.lbuddyboy.legend.command.context.TeamContext;
import dev.lbuddyboy.legend.command.context.TeamMemberContext;
import dev.lbuddyboy.legend.command.context.TeamRoleContext;
import dev.lbuddyboy.legend.command.context.TeamTypeContext;
import dev.lbuddyboy.legend.command.impl.*;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamMember;
import dev.lbuddyboy.legend.team.model.TeamRole;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.timer.PlayerTimer;
import lombok.Getter;

import java.util.Arrays;
import java.util.UUID;

@Getter
public class CommandHandler implements IModule {

    private PaperCommandManager commandManager;

    @Override
    public void load() {
        this.commandManager = new PaperCommandManager(LegendBukkit.getInstance());

        this.commandManager.getCommandCompletions().registerCompletion("teams", new TeamCompletion("all"));
        this.commandManager.getCommandCompletions().registerCompletion("systemTeams", new TeamCompletion("system"));
        this.commandManager.getCommandCompletions().registerCompletion("playerTeams", new TeamCompletion("player"));
        this.commandManager.getCommandCompletions().registerCompletion("teamMembers", new TeamMemberCompletion());
        this.commandManager.getCommandCompletions().registerCompletion("teamTypes", (ctx) -> Arrays.stream(TeamType.values()).map(Enum::name).toList());
        this.commandManager.getCommandCompletions().registerCompletion("teamRoles", (ctx) -> Arrays.stream(TeamRole.values()).map(Enum::name).toList());
        this.commandManager.getCommandCompletions().registerCompletion("playerTimers", (ctx) -> LegendBukkit.getInstance().getTimerHandler().getTimers().stream().map(PlayerTimer::getId).toList());

        // Arrow Contexts
        this.commandManager.getCommandContexts().registerContext(UUID.class, new UUIDParam());
        this.commandManager.getCommandContexts().registerContext(TimeDuration.class, new TimeParam());

        this.commandManager.getCommandContexts().registerContext(Team.class, new TeamContext());
        this.commandManager.getCommandContexts().registerContext(TeamMember.class, new TeamMemberContext());
        this.commandManager.getCommandContexts().registerContext(TeamRole.class, new TeamRoleContext());
        this.commandManager.getCommandContexts().registerContext(TeamType.class, new TeamTypeContext());

        this.commandManager.registerCommand(new TeamCommand());
        this.commandManager.registerCommand(new TimerCommand());
        this.commandManager.registerCommand(new PlayTimeCommand());
        this.commandManager.registerCommand(new SystemTeamCommand());
        this.commandManager.registerCommand(new SettingsCommand());
    }

    @Override
    public void unload() {

    }
}
