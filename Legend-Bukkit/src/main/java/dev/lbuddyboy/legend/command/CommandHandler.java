package dev.lbuddyboy.legend.command;

import co.aikar.commands.ACFBukkitHelpTopic;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.BukkitRootCommand;
import co.aikar.commands.PaperCommandManager;
import dev.lbuddyboy.arrow.command.context.TimeDurationContext;
import dev.lbuddyboy.arrow.command.context.UUIDContext;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.util.ShortPrice;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.command.completion.PlayTimeGoalCommandsCompletion;
import dev.lbuddyboy.legend.command.completion.TeamCompletion;
import dev.lbuddyboy.legend.command.completion.TeamInvitationsCompletion;
import dev.lbuddyboy.legend.command.completion.TeamMemberCompletion;
import dev.lbuddyboy.legend.command.context.*;
import dev.lbuddyboy.legend.command.impl.*;
import dev.lbuddyboy.legend.command.impl.admin.*;
import dev.lbuddyboy.legend.command.topic.LegendHelpTopic;
import dev.lbuddyboy.legend.features.kitmap.kit.Kit;
import dev.lbuddyboy.legend.features.leaderboard.ILeaderBoardStat;
import dev.lbuddyboy.legend.features.playtime.model.PlayTimeGoal;
import dev.lbuddyboy.legend.features.recipe.AbstractRecipe;
import dev.lbuddyboy.legend.features.schedule.ScheduleEntry;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamMember;
import dev.lbuddyboy.legend.team.model.TeamRole;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.timer.PlayerTimer;
import dev.lbuddyboy.legend.user.model.ChatMode;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpTopic;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class CommandHandler implements IModule {

    private PaperCommandManager commandManager;

    @Override
    public void load() {
        this.commandManager = new PaperCommandManager(LegendBukkit.getInstance());
        this.commandManager.enableUnstableAPI("help");

        this.commandManager.getCommandCompletions().registerCompletion("teams", new TeamCompletion("all"));
        this.commandManager.getCommandCompletions().registerCompletion("systemTeams", new TeamCompletion("system"));
        this.commandManager.getCommandCompletions().registerCompletion("playerTeams", new TeamCompletion("player"));
        this.commandManager.getCommandCompletions().registerCompletion("teamMembers", new TeamMemberCompletion());
        this.commandManager.getCommandCompletions().registerCompletion("teamInvitations", new TeamInvitationsCompletion());
        this.commandManager.getCommandCompletions().registerCompletion("chatModes", c -> Arrays.stream(ChatMode.values()).map(ChatMode::getIdentifiers).flatMap(Collection::stream).collect(Collectors.toList()));
        this.commandManager.getCommandCompletions().registerCompletion("playTimeGoalCommands", new PlayTimeGoalCommandsCompletion());
        this.commandManager.getCommandCompletions().registerCompletion("teamTypes", (ctx) -> Arrays.stream(TeamType.values()).map(Enum::name).collect(Collectors.toList()));
        this.commandManager.getCommandCompletions().registerCompletion("teamRoles", (ctx) -> Arrays.stream(TeamRole.values()).map(Enum::name).collect(Collectors.toList()));
        this.commandManager.getCommandCompletions().registerCompletion("playerTimers", (ctx) -> LegendBukkit.getInstance().getTimerHandler().getTimers().stream().map(PlayerTimer::getId).collect(Collectors.toList()));
        this.commandManager.getCommandCompletions().registerCompletion("playTimeGoals", (ctx) -> LegendBukkit.getInstance().getPlayTimeGoalHandler().getPlayTimeGoals().keySet());
        this.commandManager.getCommandCompletions().registerCompletion("leaderboard-types", c -> LegendBukkit.getInstance().getLeaderBoardHandler().getLeaderBoardStats().stream().map(ILeaderBoardStat::getId).collect(Collectors.toList()));
        this.commandManager.getCommandCompletions().registerCompletion("kits", c -> LegendBukkit.getInstance().getKitMapHandler().getKits().keySet());
        this.commandManager.getCommandCompletions().registerCompletion("customItems", c -> LegendBukkit.getInstance().getShopHandler().getCustomItems().keySet());
        this.commandManager.getCommandCompletions().registerCompletion("scheduleEntries", c -> LegendBukkit.getInstance().getScheduleHandler().getScheduleEntries().stream().map(ScheduleEntry::getId).toList());
        this.commandManager.getCommandCompletions().registerCompletion("recipes", c -> LegendBukkit.getInstance().getRecipeHandler().getRecipes().keySet());

        // Arrow Contexts
        this.commandManager.getCommandContexts().registerContext(UUID.class, new UUIDContext());
        this.commandManager.getCommandContexts().registerContext(TimeDuration.class, new TimeDurationContext());

        this.commandManager.getCommandContexts().registerContext(AbstractRecipe.class, new AbstractRecipeContext());
        this.commandManager.getCommandContexts().registerContext(Kit.class, new KitContext());
        this.commandManager.getCommandContexts().registerContext(PlayTimeGoal.class, new PlayTimeGoalContext());
        this.commandManager.getCommandContexts().registerContext(Team.class, new TeamContext());
        this.commandManager.getCommandContexts().registerContext(TeamMember.class, new TeamMemberContext());
        this.commandManager.getCommandContexts().registerContext(TeamRole.class, new TeamRoleContext());
        this.commandManager.getCommandContexts().registerContext(TeamType.class, new TeamTypeContext());
        this.commandManager.getCommandContexts().registerContext(ScheduleEntry.class, new ScheduleEntryContext());
        this.commandManager.getCommandContexts().registerContext(ShortPrice.class, new ShortPriceContext());
        this.commandManager.getCommandContexts().registerContext(ChatMode.class, (s) -> ChatMode.findMode(s.popFirstArg()));

        this.commandManager.registerCommand(new CobbleCommand());
        this.commandManager.registerCommand(new ScheduleCommand());
        this.commandManager.registerCommand(new TrashCommand());
        this.commandManager.registerCommand(new LogoutCommand());
        this.commandManager.registerCommand(new ShopCommand());
        this.commandManager.registerCommand(new KitCommand());
        this.commandManager.registerCommand(new StatsCommand());
        this.commandManager.registerCommand(new LeaderBoardCommand());
        this.commandManager.registerCommand(new RecipeCommand());
        this.commandManager.registerCommand(new PayCommand());
        this.commandManager.registerCommand(new PvPCommand());
        this.commandManager.registerCommand(new SOTWCommand());
        this.commandManager.registerCommand(new SpawnCommand());
        this.commandManager.registerCommand(new EndCommand());
        this.commandManager.registerCommand(new EconomyCommand());
        this.commandManager.registerCommand(new GlowstoneCommand());
        this.commandManager.registerCommand(new BuildCommand());
        this.commandManager.registerCommand(new TeamCommand());
        this.commandManager.registerCommand(new TimerCommand());
        this.commandManager.registerCommand(new PlayTimeCommand());
        this.commandManager.registerCommand(new SystemTeamCommand());
        this.commandManager.registerCommand(new SettingsCommand());
        this.commandManager.registerCommand(new LegendCommand());

        if (LegendBukkit.getInstance().getSettings().getBoolean("server.deathbans", true)) {
            this.commandManager.registerCommand(new LivesCommand());
            this.commandManager.registerCommand(new DeathbanCommand());
        }

        if (LegendBukkit.getInstance().getSettings().getBoolean("kitmap.enabled", true)) {
            this.commandManager.registerCommand(new KillStreakCommand());
        }

        Bukkit.getHelpMap().registerHelpTopicFactory(BukkitRootCommand.class, (command) -> new LegendHelpTopic(this.commandManager, (BukkitRootCommand) command));

    }

    @Override
    public void unload() {
        this.commandManager.unregisterCommands();
    }
}
