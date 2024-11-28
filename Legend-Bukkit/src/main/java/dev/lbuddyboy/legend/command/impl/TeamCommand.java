package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.component.FancyBuilder;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.FancyPagedItem;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.TeamHandler;
import dev.lbuddyboy.legend.team.model.*;
import dev.lbuddyboy.legend.team.model.claim.Claim;
import dev.lbuddyboy.legend.team.model.claim.ClaimMapView;
import dev.lbuddyboy.legend.team.model.log.TeamLog;
import dev.lbuddyboy.legend.team.model.log.impl.*;
import dev.lbuddyboy.legend.timer.impl.HomeTimer;
import dev.lbuddyboy.legend.timer.impl.StuckTimer;
import dev.lbuddyboy.legend.user.model.ChatMode;
import dev.lbuddyboy.legend.user.model.LegendUser;
import dev.lbuddyboy.legend.util.BukkitUtil;
import dev.lbuddyboy.legend.util.CommandUtil;
import dev.lbuddyboy.legend.util.PlaceholderUtil;
import dev.lbuddyboy.legend.util.UUIDUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Optional;
import java.util.stream.Collectors;

@CommandAlias("team|t|f|fac|faction")
public class TeamCommand extends BaseCommand {

    private final TeamHandler teamHandler = LegendBukkit.getInstance().getTeamHandler();

    @Default
    @HelpCommand
    @Subcommand("help")
    public static void defp(CommandSender sender, @Name("help") @co.aikar.commands.annotation.Optional CommandHelp help) {
        CommandUtil.generateCommandHelp(help);
    }

    @Subcommand("create")
    @Description("Create a new team with the given name.")
    public void create(Player sender, @Name("name") String name) {
        Team team = this.teamHandler.getTeam(name).orElse(null);

        if (team != null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.create.already-exists")));
            return;
        }

        this.teamHandler.createTeam(name, sender.getUniqueId());
        Bukkit.broadcastMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.create.broadcast")
                .replaceAll("%player%", sender.getName())
                .replaceAll("%team%", name)
        ));
    }

    @Subcommand("disband")
    @Description("Disband the team you are currently leading.")
    public void disband(Player sender) {
        Team team = this.teamHandler.getTeam(sender.getUniqueId()).orElse(null);

        if (team == null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        if (!team.isLeader(sender.getUniqueId())) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-permission")
                    .replaceAll("%role%", "Leader")
            ));
            return;
        }

        if (team.isDTRFrozen()) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.dtr-freeze.active")));
            return;
        }

        this.teamHandler.disbandTeam(team, true);
        Bukkit.broadcastMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.disband.broadcast")
                .replaceAll("%player%", sender.getName())
                .replaceAll("%team%", team.getName())
        ));
    }

    @Subcommand("home|hq")
    @Description("Teleport to your team's headquarters.")
    public void home(Player sender) {
        HomeTimer timer = (HomeTimer) LegendBukkit.getInstance().getTimerHandler().getTimer(HomeTimer.class);

        timer.start(sender);
    }

    @Subcommand("stuck")
    @Description("Start a timer to get unstuck and teleport to a safe location.")
    public void stuck(Player sender) {
        StuckTimer timer = (StuckTimer) LegendBukkit.getInstance().getTimerHandler().getTimer(StuckTimer.class);

        timer.start(sender);
    }

    @Subcommand("sethome|sethq")
    @Description("Set your team's headquarters to your current location.")
    public void setHQ(Player sender) {
        Team team = this.teamHandler.getTeam(sender.getUniqueId()).orElse(null);

        if (team == null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        Optional<TeamMember> memberOpt = team.getMember(sender.getUniqueId());

        if (!memberOpt.isPresent()) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        TeamMember member = memberOpt.get();

        if (!member.isAtLeast(TeamRole.CAPTAIN)) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-permission")
                    .replaceAll("%role%", "Captain")
            ));
            return;
        }

        if (!this.teamHandler.getClaimHandler().getTeam(sender.getLocation()).map(t -> t.equals(team)).orElse(false)) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.home.failed")));
            return;
        }

        team.setHome(sender.getLocation());
        team.sendMessage(LegendBukkit.getInstance().getLanguage().getString("team.home.set")
                .replaceAll("%player%", sender.getName())
        );

    }

    @Subcommand("ally")
    @CommandCompletion("@playerTeams")
    @Description("Send or Accept an alliance request to another team.")
    public void ally(Player sender, @Name("team") Team targetTeam) {
        Team team = this.teamHandler.getTeam(sender.getUniqueId()).orElse(null);

        if (team == null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        if (team.getAllianceRequests().contains(targetTeam.getId())) {
            int limit = LegendBukkit.getInstance().getSettings().getInt("server.allies");
            if (team.getAlliances().size() >= limit) {
                sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.alliance.error.too-many")
                        .replaceAll("%limit%", String.valueOf(limit))
                ));
                return;
            }
            team.acceptAllianceRequest(targetTeam);
            team.sendMessage(targetTeam.applyPlaceholders(LegendBukkit.getInstance().getLanguage().getString("team.alliance.accepted.sender")
                    .replaceAll("%sender%", sender.getName()), null));
            team.flagSave();
            targetTeam.flagSave();
            return;
        }

        if (targetTeam.getAllianceRequests().contains(team.getId())) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.alliance.error.already-requested")));
            return;
        }

        team.sendAllianceRequest(targetTeam);
        team.sendMessage(targetTeam.applyPlaceholders(LegendBukkit.getInstance().getLanguage().getString("team.alliance.request.sent.sender")
                .replaceAll("%sender%", sender.getName()), null));
    }

    @Subcommand("unally")
    @Description("Remove an alliance with another team.")
    @CommandCompletion("@playerTeams")
    public void unally(Player sender, @Name("team") Team targetTeam) {
        Team team = this.teamHandler.getTeam(sender.getUniqueId()).orElse(null);

        if (team == null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        if (!team.getAlliances().contains(targetTeam.getId())) {
            if (targetTeam.getAllianceRequests().contains(team.getId())) {
                team.removeAllianceRequest(targetTeam);
                team.sendMessage(targetTeam.applyPlaceholders(LegendBukkit.getInstance().getLanguage().getString("team.alliance.request.removed")
                        .replaceAll("%sender%", sender.getName()), null));
                return;
            }
            sender.sendMessage(CC.translate(targetTeam.applyPlaceholders(LegendBukkit.getInstance().getLanguage().getString("team.alliance.error.no-alliance"), null)));
            return;
        }

        team.removeAlliance(targetTeam);
        team.sendMessage(targetTeam.applyPlaceholders(LegendBukkit.getInstance().getLanguage().getString("team.alliance.broken.sender")
                .replaceAll("%sender%", sender.getName()), null));
        team.flagSave();
        targetTeam.flagSave();
    }

    @Subcommand("leave")
    @Description("Leave your current team.")
    public void leave(Player sender) {
        Team team = this.teamHandler.getTeam(sender.getUniqueId()).orElse(null);

        if (team == null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        if (team.getMember(sender.getUniqueId()).map(m -> m.getRole() == TeamRole.LEADER).orElse(false)) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.leave.leader")));
            return;
        }

        if (team.isDTRFrozen()) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.dtr-freeze.active")));
            return;
        }

        this.teamHandler.removePlayerFromTeam(team, sender.getUniqueId());
        sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.leave.left")));
        team.sendMessage(LegendBukkit.getInstance().getLanguage().getString("team.leave.announcement")
                .replaceAll("%player%", sender.getName())
        );
        team.createTeamLog(new TeamMemberRemovedLog(
                sender.getUniqueId(),
                "&6" + sender.getName() + " &cleft &ethe team.",
                TeamMemberRemovedLog.LeftCause.LEFT
        ));
    }

    @Subcommand("unrally")
    @Description("Remove your team's rally point.")
    public void unrally(Player sender) {
        Team team = this.teamHandler.getTeam(sender.getUniqueId()).orElse(null);

        if (team == null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        team.setRallyLocation(null);
        team.sendMessage("&3Rally point updated!");
    }

    @Subcommand("rally")
    @Description("Set your team's rally point to your current location.")
    public void rally(Player sender) {
        Team team = this.teamHandler.getTeam(sender.getUniqueId()).orElse(null);

        if (team == null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        team.setRallyLocation(sender.getLocation());
        team.sendMessage("&3Rally point updated!");
    }

    @Subcommand("focus")
    @Description("Focus your team on attacking another team.")
    @CommandCompletion("@playerTeams")
    public void focus(Player sender, @Name("team") @co.aikar.commands.annotation.Optional Team targetTeam) {
        Team team = this.teamHandler.getTeam(sender.getUniqueId()).orElse(null);

        if (team == null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        if (targetTeam == null && sender.getLastDamageCause() != null) {
            Player damager = BukkitUtil.getDamager(sender.getLastDamageCause());
            if (damager == null) {
                sender.sendMessage(CC.translate("&cTeam focus failed: Couldn't find the player that last hit you."));
                return;
            }

            targetTeam = this.teamHandler.getTeam(damager).orElse(null);
            if (targetTeam == null) {
                sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.enemy")
                        .replaceAll("%target%", damager.getName())
                ));
                return;
            }
        }

        team.setFocusedTeam(targetTeam);
        team.sendMessage("&dFocused team updated!");
    }

    @Subcommand("unfocus")
    @Description("Remove the focus from the currently focused team.")
    public void unfocus(Player sender) {
        Team team = this.teamHandler.getTeam(sender.getUniqueId()).orElse(null);

        if (team == null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        team.setFocusedTeam(null);
        team.sendMessage("&dFocused team updated!");
    }

    @Subcommand("claim")
    @Description("Claim land for your team.")
    public void claim(Player sender) {
        Team team = this.teamHandler.getTeam(sender.getUniqueId()).orElse(null);

        if (team == null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        sender.getInventory().removeItem(this.teamHandler.getClaimHandler().createClaimWand());
        sender.getInventory().addItem(this.teamHandler.getClaimHandler().createClaimWand());
    }

    @Subcommand("unclaim")
    @Description("Unclaim land for your team.")
    public void unclaim(Player sender) {
        Team team = this.teamHandler.getTeam(sender.getUniqueId()).orElse(null);

        if (team == null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        Claim claimAt = this.teamHandler.getClaimHandler().getClaim(sender.getLocation());

        if (claimAt == null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.unclaim.not-in-claim")));
            return;
        }


        Optional<TeamMember> memberOpt = team.getMember(sender.getUniqueId());

        if (!memberOpt.isPresent()) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        TeamMember member = memberOpt.get();

        if (!member.isAtLeast(TeamRole.CO_LEADER)) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-permission")
                    .replaceAll("%role%", "Co Leader")
            ));
            return;
        }

        this.teamHandler.getClaimHandler().removeClaim(claimAt);
        team.getClaims().remove(claimAt);
        team.flagSave();
        team.sendMessage(LegendBukkit.getInstance().getLanguage().getString("team.unclaim.success").replaceAll("%player%", sender.getName()));
    }

    @Subcommand("map")
    @Description("Toggle the claim map view.")
    public void map(Player sender) {
        ClaimMapView view = this.teamHandler.getClaimHandler().getMapViews().get(sender.getUniqueId());

        if (view != null) {
            this.teamHandler.getClaimHandler().removeMapView(sender);
            return;
        }

        this.teamHandler.getClaimHandler().createMapView(sender);
    }

    @Subcommand("kick")
    @CommandCompletion("@teamMembers")
    @Description("Kick a member from your team.")
    public void kick(Player sender, @Name("player") TeamMember member) {
        Team team = this.teamHandler.getTeam(sender.getUniqueId()).orElse(null);

        if (team == null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        Optional<TeamMember> senderMemberOpt = team.getMember(sender.getUniqueId());

        if (!senderMemberOpt.isPresent()) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        TeamMember senderMember = senderMemberOpt.get();

        if (!senderMember.getRole().isHigher(member.getRole())) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.kick.no-permission")));
            return;
        }

        if (team.isDTRFrozen()) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.dtr-freeze.active")));
            return;
        }

        this.teamHandler.removePlayerFromTeam(team, member.getUuid());

        if (member.getPlayer() != null) {
            member.getPlayer().sendMessage(CC.translate(PlaceholderUtil.applyTeamPlaceholders(LegendBukkit.getInstance().getLanguage().getString("team.kick.kicked"), team)
                    .replaceAll("%sender%", sender.getName())
            ));
        }

        team.sendMessage(LegendBukkit.getInstance().getLanguage().getString("team.kick.announcement")
                .replaceAll("%player%", member.getName())
                .replaceAll("%sender%", sender.getName())
        );
        team.createTeamLog(new TeamMemberRemovedLog(
                sender.getUniqueId(),
                "&6" + sender.getName() + " &ckicked &6" + member.getName() + " &efrom the team.",
                TeamMemberRemovedLog.LeftCause.KICKED
        ));
    }

    @Subcommand("info|i|who|f")
    @CommandCompletion("@playerTeams")
    @Description("Display information about a team.")
    public void info(Player sender, @Name("team") @co.aikar.commands.annotation.Optional Team team) {
        if (team == null) {
            if (!this.teamHandler.getTeam(sender.getUniqueId()).isPresent()) {
                sender.sendMessage(CC.translate("&cPlease provide a team name."));
                return;
            }

            team = this.teamHandler.getTeam(sender.getUniqueId()).get();
        }

        this.teamHandler.sendTeamInfo(sender, team);
    }

    @Subcommand("invite")
    @CommandCompletion("@players")
    @Description("Invite a player to join your team.")
    public void invite(Player sender, @Name("player") OfflinePlayer target) {
        Team team = this.teamHandler.getTeam(sender.getUniqueId()).orElse(null);

        if (team == null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        Optional<TeamMember> memberOpt = team.getMember(sender.getUniqueId());

        if (!memberOpt.isPresent()) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        TeamMember member = memberOpt.get();

        if (!member.isAtLeast(TeamRole.CAPTAIN)) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-permission")
                    .replaceAll("%role%", "Captain")
            ));
            return;
        }

        if (this.teamHandler.getTeam(target.getUniqueId()).isPresent()) {
            sender.sendMessage(CC.translate(PlaceholderUtil.applyTeamPlaceholders(LegendBukkit.getInstance().getLanguage().getString("team.invite.has-team"), team)
                    .replaceAll("%sender%", sender.getName())
                    .replaceAll("%target%", target.getName())
            ));
            return;
        }

        if (team.getInvitations().contains(target.getUniqueId())) {
            sender.sendMessage(CC.translate(PlaceholderUtil.applyTeamPlaceholders(LegendBukkit.getInstance().getLanguage().getString("team.invite.already-invited"), team)
                    .replaceAll("%sender%", sender.getName())
                    .replaceAll("%target%", target.getName())
            ));
            return;
        }

        team.createTeamLog(new TeamInvitationLog(sender.getUniqueId(), target.getUniqueId()));
        team.getInvitations().add(target.getUniqueId());
        team.sendMessage(LegendBukkit.getInstance().getLanguage().getString("team.invite.announcement")
                .replaceAll("%sender%", sender.getName())
                .replaceAll("%target%", target.getName())
        );

        if (target.isOnline() && target.getPlayer() != null) {
            new FancyBuilder(PlaceholderUtil.applyTeamPlaceholders(LegendBukkit.getInstance().getLanguage().getString("team.invite.target.message"), team)
                    .replaceAll("%sender%", sender.getName())
                    .replaceAll("%target%", target.getName())
            ).hover(PlaceholderUtil.applyTeamPlaceholders(LegendBukkit.getInstance().getLanguage().getString("team.invite.target.hover"), team)
                    .replaceAll("%sender%", sender.getName())
                    .replaceAll("%target%", target.getName())
            ).click(ClickEvent.Action.RUN_COMMAND, "/t accept " + team.getName()).send(target.getPlayer());
        }

    }

    @Subcommand("uninvite")
    @CommandCompletion("@teamInvitations")
    @Description("Revokes an invite for a player to join your team.")
    public void uninvite(Player sender, @Name("player") OfflinePlayer target) {
        Team team = this.teamHandler.getTeam(sender.getUniqueId()).orElse(null);

        if (team == null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        Optional<TeamMember> memberOpt = team.getMember(sender.getUniqueId());

        if (!memberOpt.isPresent()) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        TeamMember member = memberOpt.get();

        if (!member.isAtLeast(TeamRole.CAPTAIN)) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-permission")
                    .replaceAll("%role%", "Captain")
            ));
            return;
        }

        if (!team.getInvitations().contains(target.getUniqueId())) {
            sender.sendMessage(CC.translate(PlaceholderUtil.applyTeamPlaceholders(LegendBukkit.getInstance().getLanguage().getString("team.invite.not-invited"), team)
                    .replaceAll("%sender%", sender.getName())
                    .replaceAll("%target%", target.getName())
            ));
            return;
        }

        team.createTeamLog(new TeamInvitationRevokedLog(sender.getUniqueId(), target.getUniqueId()));
        team.getInvitations().remove(target.getUniqueId());
        team.sendMessage(LegendBukkit.getInstance().getLanguage().getString("team.invite.revoked-announcement")
                .replaceAll("%sender%", sender.getName())
                .replaceAll("%target%", target.getName())
        );

    }

    @Subcommand("roster add")
    @CommandCompletion("@players @teamRoles")
    @Description("Add a player to your team's roster with a specified role.")
    public void rosterAdd(Player sender, @Name("player") OfflinePlayer target, @Name("role") TeamRole role) {
        Team team = this.teamHandler.getTeam(sender.getUniqueId()).orElse(null);

        if (team == null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        Optional<TeamMember> memberOpt = team.getMember(sender.getUniqueId());

        if (!memberOpt.isPresent()) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        TeamMember member = memberOpt.get();

        if (role == TeamRole.LEADER) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.roster.error.leader")));
            return;
        }

        if (!member.getRole().isHigher(role)) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-permission")
                    .replaceAll("%role%", role.next().getDisplayName())
            ));
            return;
        }

        team.getRoster().put(target.getUniqueId(), role);
        team.flagSave();
        team.sendMessage(LegendBukkit.getInstance().getLanguage().getString("team.roster.added")
                .replaceAll("%sender%", sender.getName())
                .replaceAll("%target%", target.getName())
                .replaceAll("%role%", role.getDisplayName())
        );
    }

    @Subcommand("roster remove")
    @CommandCompletion("@players @teamRoles")
    @Description("Remove a player from your team's roster.")
    public void rosterRemove(Player sender, @Name("player") OfflinePlayer target) {
        Team team = this.teamHandler.getTeam(sender.getUniqueId()).orElse(null);

        if (team == null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        Optional<TeamMember> memberOpt = team.getMember(sender.getUniqueId());

        if (!memberOpt.isPresent()) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        if (!team.getRoster().containsKey(target.getUniqueId())) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.roster.error.not-rostered")
                    .replaceAll("%target%", target.getName())
            ));
            return;
        }

        TeamRole rosterRole = team.getRoster().get(target.getUniqueId());
        TeamMember member = memberOpt.get();

        if (rosterRole.isHigherOrEqual(member.getRole())) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-permission")
                    .replaceAll("%role%", rosterRole.next().getDisplayName())
            ));
            return;
        }

        team.getRoster().remove(target.getUniqueId());
        team.flagSave();
        team.sendMessage(LegendBukkit.getInstance().getLanguage().getString("team.roster.removed")
                .replaceAll("%sender%", sender.getName())
                .replaceAll("%target%", target.getName())
        );
    }

    @Subcommand("roster list")
    @Description("List all players in your team's roster.")
    public void rosterList(Player sender) {
        Team team = this.teamHandler.getTeam(sender.getUniqueId()).orElse(null);

        if (team == null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        String header = LegendBukkit.getInstance().getLanguage().getString("team.roster.list.header");
        String emptyText = LegendBukkit.getInstance().getLanguage().getString("team.roster.list.empty-text");
        String format = LegendBukkit.getInstance().getLanguage().getString("team.roster.list.format");
        Map<UUID, TeamRole> roster = team.getRoster();

        sender.sendMessage(CC.translate(header.replaceAll("%roster-size%", APIConstants.formatNumber(roster.size()))));

        if (roster.isEmpty()) {
            sender.sendMessage(CC.translate(emptyText));
            return;
        }

        roster.forEach((k, v) -> sender.sendMessage(CC.translate(format
                .replaceAll("%player%", UUIDUtils.name(k))
                .replaceAll("%role%", v.getDisplayName())
        )));
    }

    @Subcommand("join|accept")
    @CommandCompletion("@playerTeams")
    @Description("Accept a team invitation or join a team you are rostered.")
    public void accept(Player sender, @Name("team") Team team) {

        if (this.teamHandler.getTeam(sender.getUniqueId()).isPresent()) {
            sender.sendMessage(CC.translate(PlaceholderUtil.applyTeamPlaceholders(LegendBukkit.getInstance().getLanguage().getString("team.has-team"), team)));
            return;
        }

        if (!team.getInvitations().contains(sender.getUniqueId()) && !team.getRoster().containsKey(sender.getUniqueId())) {
            sender.sendMessage(CC.translate(PlaceholderUtil.applyTeamPlaceholders(LegendBukkit.getInstance().getLanguage().getString("team.join.not-invited"), team)));
            return;
        }

        if (team.isDTRFrozen()) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.dtr-freeze.active")));
            return;
        }

        if (team.getMembers().size() >= LegendBukkit.getInstance().getSettings().getInt("server.team-size")) {
            if (!team.getRoster().containsKey(sender.getUniqueId())) {
                sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.join.no-space")));
                return;
            }

            Optional<TeamMember> replacementOpt = team.getRosterReplacement(sender.getUniqueId());
            TeamMember member = replacementOpt.orElse(null);

            if (member == null) {
                sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.roster.error.no-replacement")));
                return;
            }

            LegendBukkit.getInstance().getTeamHandler().removePlayerFromTeam(team, member.getUuid());
        }

        LegendBukkit.getInstance().getTeamHandler().addPlayerToTeam(team, sender.getUniqueId(), team.getRoster().getOrDefault(sender.getUniqueId(), TeamRole.MEMBER));
        team.sendMessage(LegendBukkit.getInstance().getLanguage().getString("team.join.announcement")
                .replaceAll("%player%", sender.getName())
        );
    }

    @Subcommand("promote")
    @CommandCompletion("@teamMembers")
    @Description("Promote a team member to the next higher rank.")
    public void promote(Player sender, @Name("member") TeamMember member) {
        TeamRole role = member.getRole();
        Team team = this.teamHandler.getTeam(sender.getUniqueId()).orElse(null);

        if (team == null) return;

        TeamMember senderMember = team.getMember(sender.getUniqueId()).orElse(null);
        if (senderMember == null) return;

        if (role.next() == TeamRole.LEADER) {
            sender.getPlayer().sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.role.error.leader")));
            return;
        }

        if (!senderMember.getRole().isHigher(role.next())) {
            sender.getPlayer().sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-permission")
                    .replaceAll("%role%", role.next().getDisplayName())
            ));
            return;
        }

        member.setRole(role.next());
        team.flagSave();
        team.sendMessage(LegendBukkit.getInstance().getLanguage().getString("team.role.promoted")
                .replaceAll("%sender%", sender.getName())
                .replaceAll("%target%", member.getDisplayName())
                .replaceAll("%role%", role.next().getDisplayName())
        );
    }

    @Subcommand("demote")
    @CommandCompletion("@teamMembers")
    @Description("Demote a team member to the next lower rank.")
    public void demote(Player sender, @Name("member") TeamMember member) {
        TeamRole role = member.getRole();
        Team team = this.teamHandler.getTeam(sender.getUniqueId()).orElse(null);

        if (team == null) return;

        TeamMember senderMember = team.getMember(sender.getUniqueId()).orElse(null);
        if (senderMember == null) return;

        if (role == TeamRole.MEMBER) {
            sender.getPlayer().sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.role.error.member")));
            return;
        }

        if (!senderMember.getRole().isHigher(role)) {
            sender.getPlayer().sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-permission")
                    .replaceAll("%role%", role.next().getDisplayName())
            ));
            return;
        }

        member.setRole(role.previous());
        team.flagSave();
        team.sendMessage(LegendBukkit.getInstance().getLanguage().getString("team.role.demoted")
                .replaceAll("%sender%", sender.getName())
                .replaceAll("%target%", member.getDisplayName())
                .replaceAll("%role%", role.previous().getDisplayName())
        );
    }

    @Subcommand("leader")
    @CommandCompletion("@teamMembers")
    @Description("Assign a team member as the leader.")
    public void leader(Player sender, @Name("member") TeamMember member) {
        Team team = this.teamHandler.getTeam(sender.getUniqueId()).orElse(null);

        if (team == null) return;

        TeamMember senderMember = team.getMember(sender.getUniqueId()).orElse(null);
        if (senderMember == null) return;

        if (senderMember.getRole() != TeamRole.LEADER) {
            sender.getPlayer().sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-permission")
                    .replaceAll("%role%", "Leader")
            ));
            return;
        }

        member.setRole(TeamRole.LEADER);
        team.flagSave();
        team.sendMessage(LegendBukkit.getInstance().getLanguage().getString("team.role.promoted")
                .replaceAll("%sender%", sender.getName())
                .replaceAll("%target%", member.getDisplayName())
                .replaceAll("%role%", "Leader")
        );
    }

    @Subcommand("top")
    @Description("Display the top teams based on points.")
    public void top(Player sender) {
        Map<Integer, TopEntry> teams = LegendBukkit.getInstance().getTeamHandler().getTopTeams().get(TopType.POINTS);
        int amount = LegendBukkit.getInstance().getLanguage().getInt("team.top.amount");

        LegendBukkit.getInstance().getLanguage().getStringList("team.top.header").forEach(s -> sender.sendMessage(CC.translate(s.replaceAll("%type%", TopType.POINTS.getDisplayName()))));

        for (int i = 1; i <= amount; i++) {
            TopEntry entry = teams.get(i);

            if (entry == null) {
                sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.top.empty-format")
                        .replaceAll("%place%", APIConstants.formatNumber(i))
                ));
                continue;
            }

            Team team = this.teamHandler.getTeamById(entry.getTeamUUID()).orElse(null);
            if (team == null) continue;

            FancyBuilder builder = new FancyBuilder(LegendBukkit.getInstance().getLanguage().getString("team.top.format")
                    .replaceAll("%team-name%", team.getName(sender))
                    .replaceAll("%value%", APIConstants.formatNumber(entry.getType().getGetter().apply(team)))
                    .replaceAll("%place%", APIConstants.formatNumber(i))
            );

            if (LegendBukkit.getInstance().getLanguage().getBoolean("team.top.hover.enabled")) {
                builder.hover(LegendBukkit.getInstance().getLanguage().getStringList("team.top.hover.info").stream().map(s -> team.applyPlaceholders(s, sender)).collect(Collectors.toList()));
            }

            builder.click(ClickEvent.Action.RUN_COMMAND, "/t i " + team.getName());

            builder.send(sender);
        }

        LegendBukkit.getInstance().getLanguage().getStringList("team.top.footer").forEach(s -> sender.sendMessage(CC.translate(s.replaceAll("%type%", TopType.POINTS.getDisplayName()))));

    }

    @Subcommand("c|chat")
    @CommandCompletion("@chatModes")
    public void chat(Player sender, @Name("mode") ChatMode mode) {

        if (mode == null) {
            sender.sendMessage(CC.translate("<blend:&4;&c>That chat mode does not exist.</>"));
            return;
        }
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(sender.getUniqueId());

        user.setChatMode(mode);
        sender.sendMessage(CC.translate("&eChat mode updated to " + mode.getName().toLowerCase() + " chat!"));
    }

    @Subcommand("list")
    @Description("List all online teams.")
    public void list(Player sender, @Name("page") @co.aikar.commands.annotation.Optional Integer page) {
        if (page == null) page = 1;

        List<Team> teams = LegendBukkit.getInstance().getTeamHandler().getOnlineTeams().stream().sorted((o1, o2) -> Integer.compare(o2.getOnlineMembers().size(), o1.getOnlineMembers().size())).collect(Collectors.toList());
        int amount = LegendBukkit.getInstance().getLanguage().getInt("team.list.amount");
        List<FancyBuilder> builders = new ArrayList<>();

        int number = 1;
        for (Team team : teams) {
            FancyBuilder builder = new FancyBuilder(team.applyPlaceholders(LegendBukkit.getInstance().getLanguage().getString("team.list.format"), sender)
                    .replaceAll("%number%", APIConstants.formatNumber(number++))
            );

            if (LegendBukkit.getInstance().getLanguage().getBoolean("team.list.hover.enabled")) {
                builder.hover(LegendBukkit.getInstance().getLanguage().getStringList("team.list.hover.info").stream().map(s -> team.applyPlaceholders(s, sender)).collect(Collectors.toList()));
            }

            builder.click(ClickEvent.Action.RUN_COMMAND, "/t i " + team.getName());

            builders.add(builder);
        }

        FancyPagedItem pagedItem = new FancyPagedItem(
                builders,
                LegendBukkit.getInstance().getLanguage().getStringList("team.list.header").stream().map(s -> s.replaceAll("%amount%", APIConstants.formatNumber(teams.size()))).collect(Collectors.toList()),
                amount
        );

        pagedItem.send(sender, page);

        LegendBukkit.getInstance().getLanguage().getStringList("team.list.footer").forEach(s -> sender.sendMessage(CC.translate(s)));

    }

    @Subcommand("deposit|d|depo")
    public void depo(Player sender, @Name("amount") String priceString) {
        Team team = this.teamHandler.getTeam(sender.getUniqueId()).orElse(null);
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(sender.getUniqueId());

        if (team == null) return;

        double amount = 0.0D;

        try {
            amount = Double.parseDouble(priceString);
        } catch (NumberFormatException e) {
            if (!priceString.equalsIgnoreCase("all")) {

                return;
            }

            amount = LegendBukkit.getInstance().getUserHandler().getUser(sender.getUniqueId()).getBalance();
        }

        if (amount == 0) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.withdrawn.error.too-low")));
            return;
        }

        if (user.getBalance() < amount) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.deposited.error.insufficient")));
            return;
        }

        team.setBalance(team.getBalance() + amount);
        team.flagSave();
        user.setBalance(user.getBalance() - amount);
        team.sendMessage(LegendBukkit.getInstance().getLanguage().getString("team.deposited.announcement")
                .replaceAll("%sender%", sender.getName())
                .replaceAll("%amount%", APIConstants.formatNumber(amount))
        );
    }

    @Subcommand("withdraw|w")
    public void withdraw(Player sender, @Name("amount") String priceString) {
        Team team = this.teamHandler.getTeam(sender.getUniqueId()).orElse(null);
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(sender.getUniqueId());

        if (team == null) return;

        double amount = 0.0D;

        try {
            amount = Double.parseDouble(priceString);
        } catch (NumberFormatException e) {
            if (!priceString.equalsIgnoreCase("all")) {

                return;
            }

            amount = team.getBalance();
        }

        if (amount == 0) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.withdrawn.error.too-low")));
            return;
        }

        if (team.getBalance() < amount) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.withdrawn.error.insufficient")));
            return;
        }

        team.setBalance(team.getBalance() - amount);
        team.flagSave();
        user.setBalance(user.getBalance() + amount);
        team.sendMessage(LegendBukkit.getInstance().getLanguage().getString("team.withdrawn.announcement")
                .replaceAll("%sender%", sender.getName())
                .replaceAll("%amount%", APIConstants.formatNumber(amount))
        );
    }

    /**
     * Staff Commands
     */

    @Subcommand("top update")
    @CommandPermission("legend.command.team.points.set")
    @CommandCompletion("@playerTeams")
    public void topUpdate(CommandSender sender) {
        this.teamHandler.updateTopTeams();
        sender.sendMessage(CC.translate("&aUpdated all team placements."));
    }

    @Subcommand("forceleader")
    @CommandPermission("legend.command.team.points.set")
    @CommandCompletion("@players")
    public void forceleader(CommandSender sender, @Name("member") UUID playerUUID) {
        Team team = this.teamHandler.getTeam(playerUUID).orElse(null);

        if (team == null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        team.getMember(playerUUID).ifPresentOrElse(member -> {
            UUID oldLeader = team.getLeader().map(leader -> {
                leader.setRole(TeamRole.CO_LEADER);
                return leader.getUuid();
            }).orElse(null);

            member.setRole(TeamRole.LEADER);
            sender.sendMessage(CC.translate(PlaceholderUtil.applyTeamPlaceholders(LegendBukkit.getInstance().getLanguage().getString("team.leader.forced"), team)
                    .replaceAll("%old-leader%", oldLeader == null ? "None" : UUIDUtils.name(oldLeader))
                    .replaceAll("%new-leader%", UUIDUtils.name(playerUUID))
            ));
        }, () -> {

        });
    }

    @Subcommand("points set")
    @CommandPermission("legend.command.team.points.set")
    @CommandCompletion("@playerTeams")
    public void pointsSet(CommandSender sender, @Name("team") Team team, @Name("amount") int amount) {
        team.setPoints(amount, (sender instanceof Player player ? player.getUniqueId() : null), TeamPointsChangeLog.ChangeCause.FORCED);
        team.updateNameTags();
        sender.sendMessage(CC.translate(PlaceholderUtil.applyTeamPlaceholders(LegendBukkit.getInstance().getLanguage().getString("team.points.set"), team)
                .replaceAll("%amount%", APIConstants.formatNumber(amount))
        ));
    }

    @Subcommand("dtrfreeze set")
    @CommandPermission("legend.command.team.dtrfreeze")
    @CommandCompletion("@playerTeams")
    public void setFreeze(CommandSender sender, @Name("team") Team team, @Name("duration") TimeDuration duration) {
        team.applyDTRFreeze(duration.transform());
        team.updateNameTags();
        sender.sendMessage(CC.translate(PlaceholderUtil.applyTeamPlaceholders(LegendBukkit.getInstance().getLanguage().getString("team.dtr-freeze.set"), team)
                .replaceAll("%time%", duration.fancy())
        ));
    }

    @Subcommand("dtrfreeze stop")
    @CommandPermission("legend.command.team.dtrfreeze")
    @CommandCompletion("@playerTeams")
    public void stopFreeze(CommandSender sender, @Name("team") Team team) {
        setFreeze(sender, team, new TimeDuration("0s"));
    }

    @Subcommand("dtr set")
    @CommandPermission("legend.command.team.dtr")
    @CommandCompletion("@playerTeams")
    public void dtrSet(CommandSender sender, @Name("team") Team team, @Name("amount") double dtr) {
        team.createTeamLog(new TeamDTRChangeLog(
                team.getDeathsUntilRaidable(),
                dtr,
                (sender instanceof Player player ? player.getUniqueId() : null),
                TeamDTRChangeLog.ChangeCause.FORCED
        ));

        team.setDeathsUntilRaidable(dtr);
        team.updateNameTags();
        sender.sendMessage(CC.translate(PlaceholderUtil.applyTeamPlaceholders(LegendBukkit.getInstance().getLanguage().getString("team.dtr.set"), team)
                .replaceAll("%dtr%", APIConstants.formatNumber(dtr))
        ));
    }

    @Subcommand("teleport|tp")
    @CommandPermission("legend.command.team.dtr")
    @CommandCompletion("@playerTeams")
    public void tp(Player sender, @Name("team") Team team) {
        Location location = team.getHome();

        if (location == null) {
            if (team.getClaims().isEmpty()) {
                sender.sendMessage(CC.translate("&cThat team doesn't have any claims or home."));
                return;
            }

            location = team.getClaims().getFirst().getBounds().getUpperSW();
        }

        sender.teleport(location);
    }

    @Subcommand("spy")
    @CommandPermission("legend.command.team.spy")
    public void spy(Player sender) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(sender.getUniqueId());

        user.setTeamSpy(!user.isTeamSpy());
        sender.sendMessage(CC.translate("&eYou are " + (user.isTeamSpy() ? "&anow" : "&cno longer") + " &espying on teams."));
    }

    @Subcommand("logs")
    @CommandPermission("legend.command.team.points.set")
    @CommandCompletion("@teams")
    public void pointsSet(CommandSender sender, @Name("team") Team team, @Name("page") @co.aikar.commands.annotation.Optional Integer page) {
        if (page == null) page = 1;

        List<String> header = CC.translate(Arrays.asList(
                CommandUtil.CHAT_BAR,
                "<blend:&6;&e>&lTeam Logs</> &7(" + team.getName() + ") &f[%page%/%max-pages%] &7(Hover for more info)"
        ));

        List<FancyBuilder> builders = new ArrayList<>();
        int index = 1;

        for (TeamLog log : team.getSortedLogs()) {
            builders.add(new FancyBuilder("&d" + (index++) + ") " + log.getTitle()).hover(CC.translate(log.getLog())));
        }

        FancyPagedItem pagedItem = new FancyPagedItem(builders, header, 10);

        pagedItem.send(sender, page);

        sender.sendMessage(" ");
        sender.sendMessage(CC.translate("&7You can do /f logs " + team.getName() + " <page>"));
        sender.sendMessage(CommandUtil.CHAT_BAR);
    }

}
