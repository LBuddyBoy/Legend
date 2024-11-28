package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.TeamHandler;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.team.model.claim.Claim;
import dev.lbuddyboy.legend.util.CommandUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("steam|systemteam|systeam")
@CommandPermission("legend.command.systemteam")
public class SystemTeamCommand extends BaseCommand {

    private final TeamHandler teamHandler = LegendBukkit.getInstance().getTeamHandler();

    @Default
    @HelpCommand
    @Subcommand("help")
    public static void defp(CommandSender sender, @Name("help") @Optional CommandHelp help) {
        CommandUtil.generateCommandHelp(help);
    }

    @Subcommand("create")
    @CommandCompletion("@teamTypes")
    public void create(Player sender, @Name("type") TeamType type, @Name("name") String name) {
        Team team = this.teamHandler.getTeam(name).orElse(null);

        if (team != null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.create.already-exists")));
            return;
        }

        this.teamHandler.createTeam(name, null, type);
        sender.sendMessage(CC.translate("<blend:&6;&e>&lSYSTEM TEAM</> &7» &aSuccessfully created '" + name + "' with the team type of " + type.name() + "!"));
    }

    @Subcommand("sethome")
    @CommandCompletion("@systemTeams")
    public void sethome(Player sender, @Name("team") Team team) {
        team.setHome(sender.getLocation());
        team.flagSave();
        sender.sendMessage(CC.translate("<blend:&6;&e>&lSYSTEM TEAM</> &7» &cSuccessfully set the '" + team.getName() + "' home with the team type of " + team.getTeamType().name() + "!"));
    }

    @Subcommand("delete|disband")
    @CommandCompletion("@systemTeams")
    public void disband(Player sender, @Name("team") Team team) {
        this.teamHandler.disbandTeam(team, true);
        sender.sendMessage(CC.translate("<blend:&6;&e>&lSYSTEM TEAM</> &7» &cSuccessfully deleted the '" + team.getName() + "' with the team type of " + team.getTeamType().name() + "!"));
    }

    @Subcommand("claimfor|claim")
    @CommandCompletion("@systemTeams")
    public void claimfor(Player sender, @Name("team") Team team) {
        sender.getInventory().addItem(this.teamHandler.getClaimHandler().createClaimForWand(team));
    }

    @Subcommand("unclaimfor|unclaim")
    @CommandCompletion("@systemTeams")
    public void unclaimfor(Player sender, @Name("team") Team team) {
        Claim claim = this.teamHandler.getClaimHandler().getClaim(sender.getLocation());

        if (claim == null) {
            sender.sendMessage(CC.translate("&cYou need to be standing in the teams claim."));
            return;
        }

        Team claimTeam = claim.getTeam().orElse(null);
        if (claimTeam == null || !claimTeam.equals(team)) {
            sender.sendMessage(CC.translate("&cThat team does not own this land."));
            return;
        }

        this.teamHandler.getClaimHandler().removeClaim(claim);
        team.getClaims().remove(claim);
        sender.sendMessage(CC.translate("<blend:&6;&e>&lSYSTEM TEAM</> &7» &cSuccessfully removed a claim for '" + team.getName() + "' with the team type of " + team.getTeamType().name() + "!"));
    }

}
