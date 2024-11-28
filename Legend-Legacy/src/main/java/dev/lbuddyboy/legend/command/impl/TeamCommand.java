package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.component.FancyBuilder;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.TeamHandler;
import dev.lbuddyboy.legend.team.model.*;
import dev.lbuddyboy.legend.team.model.claim.ClaimMapView;
import dev.lbuddyboy.legend.timer.impl.HomeTimer;
import dev.lbuddyboy.legend.util.PlaceholderUtil;
import dev.lbuddyboy.legend.util.UUIDUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@CommandAlias("team|t|f|fac|faction")
public class TeamCommand extends BaseCommand {

    private final TeamHandler teamHandler = LegendBukkit.getInstance().getTeamHandler();

    @Subcommand("create")
    public void create(Player sender, @Name("name") String name) {
        this.teamHandler.getTeam(name).ifPresentOrElse((team) -> {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.create.already-exists")));
        }, () -> {


            this.teamHandler.createTeam(name, sender.getUniqueId());
            Bukkit.broadcastMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.create.broadcast")
                    .replaceAll("%player%", sender.getName())
                    .replaceAll("%team%", name)
            ));
        });
    }

    @Subcommand("disband")
    public void disband(Player sender) {
        this.teamHandler.getTeam(sender.getUniqueId()).ifPresentOrElse(team -> {
            if (!team.isLeader(sender.getUniqueId())) {
                sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-permission")
                        .replaceAll("%role%", "Leader")
                ));
                return;
            }

            if (team.isDTRFrozen()) {
                sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.dtr-freeze")));
                return;
            }

            this.teamHandler.disbandTeam(team, true);
            Bukkit.broadcastMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.disband.broadcast")
                    .replaceAll("%player%", sender.getName())
                    .replaceAll("%team%", team.getName())
            ));
        }, () -> sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender"))));
    }

    @Subcommand("home|hq")
    public void home(Player sender) {
        HomeTimer timer = (HomeTimer) LegendBukkit.getInstance().getTimerHandler().getTimer(HomeTimer.class);

        timer.start(sender);
    }

    @Subcommand("sethome|sethq")
    public void setHQ(Player sender) {
        this.teamHandler.getTeam(sender.getUniqueId()).ifPresentOrElse(team -> {
            Optional<TeamMember> memberOpt = team.getMember(sender.getUniqueId());

            if (memberOpt.isEmpty()) {
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

        }, () -> sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender"))));
    }

    @Subcommand("leave")
    public void leave(Player sender) {
        this.teamHandler.getTeam(sender.getUniqueId()).ifPresentOrElse(team -> {
            if (team.isDTRFrozen()) {
                sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.dtr-freeze")));
                return;
            }

            this.teamHandler.removePlayerFromTeam(team, sender.getUniqueId());
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.leave.left")));
            team.sendMessage(LegendBukkit.getInstance().getLanguage().getString("team.leave.announcement")
                    .replaceAll("%player%", sender.getName())
            );

        }, () -> sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender"))));
    }

    @Subcommand("claim")
    public void claim(Player sender) {
        this.teamHandler.getTeam(sender.getUniqueId()).ifPresentOrElse(team -> {
            sender.getInventory().removeItem(this.teamHandler.getClaimHandler().getClaimWand());
            sender.getInventory().addItem(this.teamHandler.getClaimHandler().createClaimWand());
        }, () -> sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender"))));
    }

    @Subcommand("map")
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
    public void kick(Player sender, @Name("player") TeamMember member) {
        this.teamHandler.getTeam(sender.getUniqueId()).ifPresentOrElse(team -> {
            Optional<TeamMember> senderMemberOpt = team.getMember(sender.getUniqueId());

            if (senderMemberOpt.isEmpty()) {
                sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
                return;
            }

            TeamMember senderMember = senderMemberOpt.get();

            if (!senderMember.getRole().isHigher(member.getRole())) {
                sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.kick.no-permission")));
                return;
            }

            if (team.isDTRFrozen()) {
                sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.dtr-freeze")));
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

        }, () -> sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender"))));
    }

    @Subcommand("info|i|who|f")
    @CommandCompletion("@playerTeams")
    public void info(Player sender, @Name("team") @co.aikar.commands.annotation.Optional Team team) {
        if (team == null) {
            if (this.teamHandler.getTeam(sender.getUniqueId()).isEmpty()) {
                sender.sendMessage(CC.translate("&cPlease provide a team name."));
                return;
            }

            team = this.teamHandler.getTeam(sender.getUniqueId()).get();
        }

        this.teamHandler.sendTeamInfo(sender, team);
    }

    @Subcommand("invite")
    @CommandCompletion("@players")
    public void invite(Player sender, @Name("player") OfflinePlayer target) {
        this.teamHandler.getTeam(sender.getUniqueId()).ifPresentOrElse(team -> {
            Optional<TeamMember> memberOpt = team.getMember(sender.getUniqueId());

            if (memberOpt.isEmpty()) {
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

        }, () -> sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender"))));
    }

    @Subcommand("roster add")
    @CommandCompletion("@players @teamRoles")
    public void rosterAdd(Player sender, @Name("player") OfflinePlayer target, @Name("role") TeamRole role) {
        this.teamHandler.getTeam(sender.getUniqueId()).ifPresentOrElse(team -> {
            Optional<TeamMember> memberOpt = team.getMember(sender.getUniqueId());

            if (memberOpt.isEmpty()) {
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
        }, () -> sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender"))));
    }

    @Subcommand("roster remove")
    @CommandCompletion("@players @teamRoles")
    public void rosterRemove(Player sender, @Name("player") OfflinePlayer target) {
        this.teamHandler.getTeam(sender.getUniqueId()).ifPresentOrElse(team -> {
            Optional<TeamMember> memberOpt = team.getMember(sender.getUniqueId());

            if (memberOpt.isEmpty()) {
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
        }, () -> sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender"))));
    }

    @Subcommand("roster list")
    public void rosterList(Player sender) {
        this.teamHandler.getTeam(sender.getUniqueId()).ifPresentOrElse(team -> {
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
        }, () -> sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender"))));
    }

    @Subcommand("join|accept")
    @CommandCompletion("@playerTeams")
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
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.dtr-freeze")));
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

            Team team = this.teamHandler.getTeamById(entry.teamUUID()).orElse(null);
            if (team == null) continue;

            FancyBuilder builder = new FancyBuilder(LegendBukkit.getInstance().getLanguage().getString("team.top.format")
                    .replaceAll("%team-name%", team.getName(sender))
                    .replaceAll("%value%", APIConstants.formatNumber(entry.type().getGetter().apply(team)))
                    .replaceAll("%place%", APIConstants.formatNumber(i))
            );

            if (LegendBukkit.getInstance().getLanguage().getBoolean("team.top.hover.enabled")) {
                builder.hover(LegendBukkit.getInstance().getLanguage().getStringList("team.top.hover.info").stream().map(s -> team.applyPlaceholders(s, sender)).toList());
            }

            builder.send(sender);
        }

        LegendBukkit.getInstance().getLanguage().getStringList("team.top.footer").forEach(s -> sender.sendMessage(CC.translate(s.replaceAll("%type%", TopType.POINTS.getDisplayName()))));

    }

    /**
     * Staff Commands
     */

    @Subcommand("top update")
    @CommandPermission("legend.command.team.points.set")
    @CommandCompletion("@playerTeams")
    @Private
    public void topUpdate(CommandSender sender) {
        this.teamHandler.updateTopTeams();
        sender.sendMessage(CC.translate("&aUpdated all team placements."));
    }

    @Subcommand("points set")
    @CommandPermission("legend.command.team.points.set")
    @CommandCompletion("@playerTeams")
    @Private
    public void pointsSet(CommandSender sender, @Name("team") Team team, @Name("amount") int amount) {
        team.setPoints(amount);
        sender.sendMessage(CC.translate(PlaceholderUtil.applyTeamPlaceholders(LegendBukkit.getInstance().getLanguage().getString("team.points.set"), team)
                .replaceAll("%amount%", APIConstants.formatNumber(amount))
        ));
    }

}
