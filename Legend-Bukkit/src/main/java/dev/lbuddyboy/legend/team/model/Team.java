package dev.lbuddyboy.legend.team.model;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.common.location.ApolloBlockLocation;
import com.lunarclient.apollo.module.waypoint.Waypoint;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.component.FancyBuilder;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.LocationUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.claim.Claim;
import dev.lbuddyboy.legend.team.model.log.TeamLog;
import dev.lbuddyboy.legend.team.model.log.TeamLogType;
import dev.lbuddyboy.legend.team.model.log.impl.TeamDTRChangeLog;
import dev.lbuddyboy.legend.team.model.log.impl.TeamPointsChangeLog;
import dev.lbuddyboy.legend.user.model.LegendUser;
import dev.lbuddyboy.legend.util.BukkitUtil;
import dev.lbuddyboy.legend.util.PlaceholderUtil;
import dev.lbuddyboy.legend.util.TypeTokens;
import lombok.Data;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class Team {

    private final UUID id;

    private String name;
    private List<TeamMember> members = new ArrayList<>();
    private List<Claim> claims = new ArrayList<>();
    private TeamType teamType = TeamType.PLAYER;

    private long dtrRegen, dtrRegenDuration, createdAt;
    private double deathsUntilRaidable = 1.0D;
    private Location home = null;
    private Map<UUID, TeamRole> roster = new HashMap<>();
    private int points, kills, deaths;
    private double balance;
    private List<UUID> alliances = new ArrayList<>(), allianceRequests = new ArrayList<>(), invitations = new ArrayList<>();
    private List<TeamLog> teamLogs = new ArrayList<>();

    private transient boolean edited = false;
    private transient long lastRegenerated = 0L;
    private final transient Map<TopType, Integer> places = new HashMap<>();
    private transient UUID focusedTeam;
    private transient Location rallyLocation;
    private transient Map<WaypointType, Waypoint> waypoints = new HashMap<>();

    public Team(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.createdAt = System.currentTimeMillis();

        flagSave();
    }

    public Team(Document document) {
        this.id = UUID.fromString(document.getString("id"));
        this.name = document.getString("name");
        this.members.addAll(APIConstants.GSON.fromJson(document.getString("members"), TypeTokens.TEAM_MEMBERS.getType()));
        this.claims.addAll(APIConstants.GSON.fromJson(document.getString("claims"), TypeTokens.CLAIMS.getType()));
        this.dtrRegen = document.getLong("dtrRegen");
        this.deathsUntilRaidable = document.getDouble("deathsUntilRaidable");
        this.teamType = TeamType.valueOf(document.getString("teamType"));
        this.points = document.getInteger("points", 0);
        this.balance = document.get("balance", 0.0D);
        this.createdAt = document.get("createdAt", System.currentTimeMillis());
        if (document.containsKey("allianceRequests"))
            this.allianceRequests = document.getList("allianceRequests", UUID.class);
        if (document.containsKey("alliances")) this.alliances = document.getList("alliances", UUID.class);
        if (document.containsKey("invitations")) this.invitations = document.getList("invitations", UUID.class);
        if (document.containsKey("home")) setHome(LocationUtils.deserializeString(document.getString("home")));
        if (document.containsKey("roster"))
            Document.parse(document.getString("roster")).forEach((k, v) -> this.roster.put(UUID.fromString(k), TeamRole.valueOf((String) v)));

        if (document.containsKey("teamLogs")) {
            this.teamLogs.addAll(document.getList("teamLogs", String.class)
                    .stream()
                    .map(Document::parse)
                    .map(d -> TeamLogType.valueOf(d.getString("type")).getCreationConsumer().apply(d))
                    .toList());
        }
    }

    public Document toDocument() {
        Document document = new Document(), rosterDocument = new Document();

        this.roster.forEach((k, v) -> rosterDocument.put(k.toString(), v.name()));

        document.put("id", this.id.toString());
        document.put("name", this.name);
        document.put("members", APIConstants.GSON.toJson(this.members, TypeTokens.TEAM_MEMBERS.getType()));
        document.put("claims", APIConstants.GSON.toJson(this.claims, TypeTokens.CLAIMS.getType()));
        document.put("dtrRegen", this.dtrRegen);
        document.put("deathsUntilRaidable", this.deathsUntilRaidable);
        document.put("points", this.points);
        document.put("balance", this.balance);
        document.put("createdAt", this.createdAt);
        document.put("alliances", this.alliances);
        document.put("invitations", this.invitations);
        document.put("allianceRequests", this.allianceRequests);
        document.put("teamType", this.teamType.name());
        document.put("roster", rosterDocument.toJson());
        document.put("teamLogs", this.teamLogs.stream().map(TeamLog::toDocument).map(Document::toJson).toList());

        if (home != null) document.put("home", LocationUtils.serializeString(this.home));

        return document;
    }

    public List<TeamLog> getSortedLogs() {
        return this.teamLogs.stream().sorted(Comparator.comparingLong(TeamLog::getLoggedAt).reversed()).toList();
    }

    public void createTeamLog(TeamLog log) {
        FancyBuilder builder = new FancyBuilder("<blend:&6;&e>&lTEAM SPY</> &7[" + this.name + "] &7» " + log.getTitle() + " &7&o(( HOVER ))").hover(log.getLog());

        BukkitUtil.getStaffPlayers().forEach(p -> {
            LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(p.getUniqueId());
            if (!user.isTeamSpy()) return;

            builder.send(p);
        });

        this.teamLogs.add(log);
        this.flagSave();
    }

    public Optional<TeamMember> getMember(UUID playerUUID) {
        return this.members.stream().filter(member -> member.getUuid().equals(playerUUID)).findFirst();
    }

    public boolean isFullyRegenerated() {
        return this.deathsUntilRaidable >= getMaxDTR();
    }

    public boolean isDTRFrozen() {
        return getDTRFreezeRemaining() > 0;
    }

    public long getDTRFreezeRemaining() {
        return (this.dtrRegen + this.dtrRegenDuration) - System.currentTimeMillis();
    }

    public void applyDTRFreeze(long duration) {
        this.dtrRegen = System.currentTimeMillis();
        this.dtrRegenDuration = duration;
        flagSave();
    }

    public Optional<TeamMember> getLeader() {
        return this.members.stream().filter(member -> member.getRole() == TeamRole.LEADER).findFirst();
    }

    public List<TeamMember> getAllMembersSorted() {
        return new ArrayList<TeamMember>() {{
            addAll(getOnlineMembers());
            addAll(getOfflineMembers());
        }};
    }

    public List<TeamMember> getOnlineMembers() {
        return this.members.stream().filter(TeamMember::isOnline).sorted(Comparator.comparingInt(m -> ((TeamMember) m).getRole().getWeight()).reversed()).collect(Collectors.toList());
    }

    public List<TeamMember> getOfflineMembers() {
        return this.members.stream().filter(m -> !m.isOnline()).sorted(Comparator.comparingInt(m -> ((TeamMember) m).getRole().getWeight()).reversed()).collect(Collectors.toList());
    }

    public List<Player> getOnlinePlayers() {
        return getOnlineMembers().stream().map(TeamMember::getPlayer).collect(Collectors.toList());
    }

    public boolean isMember(UUID check) {
        return this.getMembersByUUID().contains(check);
    }

    public boolean isLeader(UUID check) {
        return getMember(check).map(m -> m.getRole() == TeamRole.LEADER).orElse(false);
    }

    public void addMember(TeamMember member) {
        this.invitations.remove(member.getUuid());
        removeMember(member.getUuid());
        this.members.add(member);
        flagSave();
    }

    public int getMaxDTR() {
        return Math.min(this.members.size() + 1, LegendBukkit.getInstance().getSettings().getInt("team.maximum-dtr"));
    }

    public String getDTRSymbol() {
        if (isRaidable()) return "&4⚠";
        if (!isDTRFrozen() && this.deathsUntilRaidable < getMaxDTR()) return "&a♦";
        if (isDTRFrozen()) return "&c●";

        return "&2✦";
    }

    public List<Team> getAllies() {
        return this.alliances.stream().map(uuid -> LegendBukkit.getInstance().getTeamHandler().getTeamById(uuid).orElse(null)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public boolean isAlly(Player player) {
        return getAllies().stream().anyMatch(t -> t.isMember(player.getUniqueId()));
    }

    public void removeAlliance(Team team) {
        this.alliances.removeIf(uuid -> uuid.equals(team.getId()));
        team.getAlliances().removeIf(uuid -> uuid.equals(this.id));
        team.sendMessage(applyPlaceholders(LegendBukkit.getInstance().getLanguage().getString("team.alliance.broken.target"), null));
    }

    public void removeAllianceRequest(Team team) {
        team.getAllianceRequests().removeIf(uuid -> uuid.equals(this.id));
    }

    public void acceptAllianceRequest(Team team) {
        team.getAllianceRequests().removeIf(uuid -> uuid.equals(this.id));
        this.allianceRequests.removeIf(uuid -> uuid.equals(team.getId()));
        team.getAlliances().add(this.id);
        this.alliances.add(team.getId());
        team.sendMessage(applyPlaceholders(LegendBukkit.getInstance().getLanguage().getString("team.alliance.accepted.target"), null));
    }

    public void sendAllianceRequest(Team team) {
        team.getAllianceRequests().removeIf(uuid -> uuid.equals(this.id));
        team.getAllianceRequests().add(this.id);
        team.sendMessage(applyPlaceholders(LegendBukkit.getInstance().getLanguage().getString("team.alliance.request.sent.target"), null));
    }

    public String getDTRColor() {
        if (this.deathsUntilRaidable < 0)
            return LegendBukkit.getInstance().getLanguage().getString("team.dtr.raidable-color");
        if (this.deathsUntilRaidable > 0 && this.deathsUntilRaidable <= 1)
            return LegendBukkit.getInstance().getLanguage().getString("team.dtr.one-dtr-color");

        return LegendBukkit.getInstance().getLanguage().getString("team.dtr.normal-color");
    }

    public void removeMember(UUID playerUUID) {
        this.members.removeIf(member -> member.getUuid().equals(playerUUID));
        flagSave();
    }

    public long getNextDTRRegen() {
        return (this.lastRegenerated + (LegendBukkit.getInstance().getSettings().getInt("team.regeneration.speed") * 1000L)) - System.currentTimeMillis();
    }

    public List<UUID> getMembersByUUID() {
        return this.members.stream().map(TeamMember::getUuid).collect(Collectors.toList());
    }

    public void playSound(Sound sound, float volume) {
        getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), sound, volume, volume));
    }

    public void sendMessage(String message) {
        getOnlinePlayers().forEach(p -> p.sendMessage(CC.translate(PlaceholderUtil.applyTeamPlaceholders(message, this))));
    }

    public void regenDTR(double amount) {
        double previousDTR = this.deathsUntilRaidable;

        this.deathsUntilRaidable += amount;
        this.lastRegenerated = System.currentTimeMillis();
        this.createTeamLog(new TeamDTRChangeLog(previousDTR, this.deathsUntilRaidable, null, TeamDTRChangeLog.ChangeCause.NATURAL_REGEN));

        playSound(Sound.ENTITY_PLAYER_LEVELUP, 1.0f);

        if (isFullyRegenerated()) {
            sendMessage(LegendBukkit.getInstance().getLanguage().getString("team.regeneration.fully-regenerated")
                    .replaceAll("%dtr%", APIConstants.formatNumber(amount))
            );
            return;
        }

        sendMessage(LegendBukkit.getInstance().getLanguage().getString("team.regeneration.regenerated")
                .replaceAll("%dtr%", APIConstants.formatNumber(amount))
        );
        updateNameTags();
    }

    public Optional<TeamMember> getRosterReplacement(UUID playerUUID) {
        if (!roster.containsKey(playerUUID)) return Optional.empty();

        TeamRole role = roster.get(playerUUID);

        return this.members.stream().filter(m -> !m.isOnline() && !m.getRole().isHigher(role)).findFirst();
    }

    public boolean isRaidable() {
        return this.deathsUntilRaidable < 0;
    }

    public String getFancyPlace() {
        Map<Integer, TopEntry> teams = LegendBukkit.getInstance().getTeamHandler().getTopTeams().get(TopType.POINTS);

        for (int i = 1; i <= 3; i++) {
            TopEntry entry = teams.get(i);

            if (entry == null) continue;

            Team team = LegendBukkit.getInstance().getTeamHandler().getTeamById(entry.getTeamUUID()).orElse(null);

            if (team == null) continue;
            if (!team.getId().equals(this.id)) continue;

            if (i == 1) {
                return CC.translate("&6❶");
            } else if (i == 2) {
                return CC.translate("&e❷");
            } else {
                return CC.translate("&b❸");
            }
        }

        return "";
    }

    public void setHome(Location location) {
        this.home = location;

        removeWaypoint(WaypointType.HOME);

        if (this.home != null) {
            this.waypoints.put(WaypointType.HOME, Waypoint.builder()
                    .name(WaypointType.HOME.getWaypointName())
                    .location(ApolloBlockLocation.builder()
                            .world(this.home.getWorld().getName())
                            .x(this.home.getBlockX())
                            .y(this.home.getBlockY())
                            .z(this.home.getBlockZ())
                            .build()
                    )
                    .color(WaypointType.HOME.getColor())
                    .preventRemoval(false)
                    .hidden(false)
                    .build()
            );

            sendWaypoint(this.waypoints.get(WaypointType.HOME));
        } else this.waypoints.remove(WaypointType.HOME);

        flagSave();
    }

    public void setPoints(int amount, UUID causedBy, TeamPointsChangeLog.ChangeCause cause) {
        this.createTeamLog(new TeamPointsChangeLog(this.points, amount, causedBy, cause));
        this.points = amount;
        flagSave();
    }

    public String getName(Player player) {
        if (teamType != TeamType.PLAYER)
            return LegendBukkit.getInstance().getLanguage().getString("team.system.names." + teamType.name().toLowerCase())
                    .replaceAll("%team%", this.name);

        if (getMember(player.getUniqueId()).isPresent()) return ChatColor.DARK_GREEN + this.name;

        return ChatColor.RED + this.name;
    }

    public void setRallyLocation(Location location) {
        this.rallyLocation = location;

        removeWaypoint(WaypointType.RALLY);

        if (this.rallyLocation != null) {
            this.waypoints.put(WaypointType.RALLY, Waypoint.builder()
                    .name(WaypointType.RALLY.getWaypointName())
                    .location(ApolloBlockLocation.builder()
                            .world(location.getWorld().getName())
                            .x(location.getBlockX())
                            .y(location.getBlockY())
                            .z(location.getBlockZ())
                            .build()
                    )
                    .color(WaypointType.RALLY.getColor())
                    .preventRemoval(false)
                    .hidden(false)
                    .build()
            );

            sendWaypoint(this.waypoints.get(WaypointType.RALLY));
        } else this.waypoints.remove(WaypointType.RALLY);
    }

    public Team getFocusedTeam() {
        return this.focusedTeam == null ? null : LegendBukkit.getInstance().getTeamHandler().getTeamById(this.focusedTeam).orElse(null);
    }

    public void setFocusedTeam(Team team) {
        this.focusedTeam = team == null ? null : team.getId();

        removeWaypoint(WaypointType.FOCUSED);

        if (team != null && team.getHome() != null) {
            this.waypoints.put(WaypointType.FOCUSED, Waypoint.builder()
                    .name(WaypointType.FOCUSED.getWaypointName())
                    .location(ApolloBlockLocation.builder()
                            .world(team.getHome().getWorld().getName())
                            .x(team.getHome().getBlockX())
                            .y(team.getHome().getBlockY())
                            .z(team.getHome().getBlockZ())
                            .build()
                    )
                    .color(WaypointType.FOCUSED.getColor())
                    .preventRemoval(false)
                    .hidden(false)
                    .build()
            );

            sendWaypoint(this.waypoints.get(WaypointType.FOCUSED));
        } else this.waypoints.remove(WaypointType.FOCUSED);
    }

    public void removeWaypoint(WaypointType type) {
        getOnlinePlayers().forEach(p -> removeWaypoint(type.getWaypointName(), p));
    }

    public void sendWaypoint(Waypoint waypoint) {
        getOnlinePlayers().forEach(p -> sendWaypoint(waypoint, p));
    }

    public void removeWaypoint(String waypointName, Player player) {
        Apollo.getPlayerManager().getPlayer(player.getUniqueId()).ifPresent(apolloPlayer -> {
            WaypointModule waypointModule = Apollo.getModuleManager().getModule(WaypointModule.class);

            waypointModule.removeWaypoint(apolloPlayer, waypointName);
        });
    }

    public void sendWaypoint(Waypoint waypoint, Player player) {
        Apollo.getPlayerManager().getPlayer(player.getUniqueId()).ifPresent(apolloPlayer -> {
            WaypointModule waypointModule = Apollo.getModuleManager().getModule(WaypointModule.class);

            waypointModule.displayWaypoint(apolloPlayer, waypoint);
        });
    }

    public void flagSave() {
        this.edited = true;
    }

    public void updateNameTags() {
        this.getOnlinePlayers().forEach(CommonsPlugin.getInstance().getNameTagHandler()::updatePlayer);
    }

    public String applyPlaceholders(String s, Player player) {
        List<TeamMember> members = getAllMembersSorted();

        for (int i = 1; i <= 20; i++) {
            s = s.replaceAll("%team_member_" + (i) + "%", (members.size() >= i ? members.get(i - 1).getDisplayName() : ""));
        }

        return s
                .replaceAll("%glowstone-blocks-left%", APIConstants.formatNumber(LegendBukkit.getInstance().getGlowstoneHandler().getBlocksLeft()))
                .replaceAll("%glowstone-next-reset%", TimeUtils.formatIntoDetailedString(LegendBukkit.getInstance().getGlowstoneHandler().getNextReset()))
                .replaceAll("%balance%", APIConstants.formatNumber(getBalance()))
                .replaceAll("%created-at%", APIConstants.SDF.format(this.createdAt))
                .replaceAll("%points%", APIConstants.formatNumber(getPoints()))
                .replaceAll("%dtr%", APIConstants.formatNumber(getDeathsUntilRaidable()))
                .replaceAll("%dtr-regeneration%", isDTRFrozen() ? " - " + TimeUtils.formatIntoDetailedString(getDTRFreezeRemaining()) : "")
                .replaceAll("%dtr-symbol%", getDTRSymbol())
                .replaceAll("%dtr-color%", getDTRColor())
                .replaceAll("%online%", String.valueOf(getOnlineMembers().size()))
                .replaceAll("%total%", String.valueOf(getMembers().size()))
                .replaceAll("%leaders%", StringUtils.join(getOnlineMembers().stream().filter(r -> r.getRole() == TeamRole.LEADER).map(TeamMember::getDisplayName).collect(Collectors.toList()), ", "))
                .replaceAll("%co-leaders%", StringUtils.join(getOnlineMembers().stream().filter(r -> r.getRole() == TeamRole.CO_LEADER).map(TeamMember::getDisplayName).collect(Collectors.toList()), ", "))
                .replaceAll("%captains%", StringUtils.join(getOnlineMembers().stream().filter(r -> r.getRole() == TeamRole.CAPTAIN).map(TeamMember::getDisplayName).collect(Collectors.toList()), ", "))
                .replaceAll("%members%", StringUtils.join(getOnlineMembers().stream().filter(r -> r.getRole() == TeamRole.MEMBER).map(TeamMember::getDisplayName).collect(Collectors.toList()), ", "))
                .replaceAll("%online-members%", StringUtils.join(getOnlineMembers().stream().map(TeamMember::getDisplayName).collect(Collectors.toList()), ", "))
                .replaceAll("%offline-members%", StringUtils.join(getOfflineMembers().stream().map(TeamMember::getDisplayName).collect(Collectors.toList()), ", "))
                .replaceAll("%members%", StringUtils.join(getMembers().stream().map(TeamMember::getDisplayName).collect(Collectors.toList()), ", "))
                .replaceAll("%home%", (getHome() == null ? "N/A" : LocationUtils.toString(getHome()) + " &7[" + BukkitUtil.getWorldName(getHome().getWorld()) + "&7]"))
                .replaceAll("%team%", (player == null ? getName() : getName(player)));
    }

}
