package dev.lbuddyboy.legend.team;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.scoreboard.ScoreboardHandler;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.listener.*;
import dev.lbuddyboy.legend.team.model.*;
import dev.lbuddyboy.legend.team.thread.ClaimBorderThread;
import dev.lbuddyboy.legend.team.thread.DTRThread;
import dev.lbuddyboy.legend.team.thread.TeamSaveThread;
import dev.lbuddyboy.legend.team.thread.TeamTopThread;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TeamHandler implements IModule {

    private final Map<UUID, Team> teamIds;
    private final Map<String, Team> teamNames;
    private final Map<UUID, Team> playerTeams;
    @Getter private final Map<TopType, Map<Integer, TopEntry>> topTeams;

    private MongoCollection<Document> collection;
    private Thread teamSaveThread, dtrThread, topThread, borderThread;

    @Getter private final ClaimHandler claimHandler;
    @Getter private final MovementHandler movementHandler;

    public TeamHandler() {
        this.teamIds = new ConcurrentHashMap<>();
        this.teamNames = new ConcurrentHashMap<>();
        this.playerTeams = new ConcurrentHashMap<>();
        this.topTeams = new ConcurrentHashMap<>();
        this.claimHandler = new ClaimHandler();
        this.movementHandler = new MovementHandler();
    }

    @Override
    public void load() {
        this.collection = LegendBukkit.getInstance().getMongoHandler().getDatabase().getCollection("Teams");

        this.loadListeners();
        this.loadTeams();
        this.claimHandler.load();

        new TeamSaveThread().start();
        new DTRThread().start();
        new TeamTopThread().start();
        new ClaimBorderThread().start();
    }

    @Override
    public void unload() {
        this.teamIds.values().forEach(team -> saveTeam(team, false));
    }

    private void loadTeams() {
        FindIterable<Document> documents = this.collection.find();
        int success = 0, failed = 0;

        for (Document document : documents) {
            try {
                this.cacheTeam(new Team(document));
                success++;
            } catch (Exception e) {
                e.printStackTrace();
                failed++;
            }
        }

        LegendBukkit.getInstance().getLogger().info("Loaded " + success + " teams successfully, " + failed + " failures.");
    }

    private void loadListeners() {
        LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(new ClaimWandListener(), LegendBukkit.getInstance());
        LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(new TeamClaimListener(), LegendBukkit.getInstance());
        LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(new TeamDTRListener(), LegendBukkit.getInstance());
        LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(new TeamListener(), LegendBukkit.getInstance());
        LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(new TeamWaypointListener(), LegendBukkit.getInstance());
        LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(new SubclaimListener(), LegendBukkit.getInstance());
        LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(this.movementHandler, LegendBukkit.getInstance());
    }

    public Optional<Team> getTeam(String name) {
        return Optional.ofNullable(this.teamNames.getOrDefault(name.toLowerCase(), null));
    }

    public Optional<Team> getTeam(UUID playerUUID) {
        return Optional.ofNullable(this.playerTeams.getOrDefault(playerUUID, null));
    }

    public Optional<Team> getTeamById(UUID id) {
        return Optional.ofNullable(this.teamIds.getOrDefault(id, null));
    }

    public Optional<Team> getTeam(Player player) {
        return getTeam(player.getUniqueId());
    }

    public void removePlayerFromTeam(Team team, UUID playerUUID) {
        team.removeMember(playerUUID);
        this.playerTeams.remove(playerUUID);

        team.getOnlinePlayers().forEach(CommonsPlugin.getInstance().getNametagHandler()::reloadPlayer);
        if (!team.isDTRFrozen()) {
            team.setDeathsUntilRaidable(team.getMaxDTR() - 1);
        }

        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) Arrays.stream(WaypointType.values()).forEach(type -> team.removeWaypoint(type.getWaypointName(), player));
    }

    public void addPlayerToTeam(Team team, UUID playerUUID, TeamRole role) {
        TeamMember member = new TeamMember(playerUUID, role);

        team.getMembers().add(member);
        this.playerTeams.put(playerUUID, team);

    }

    public void createTeam(String name, UUID leaderUUID) {
        createTeam(name, leaderUUID, TeamType.PLAYER);
    }

    public void createTeam(String name, UUID leaderUUID, TeamType type) {
        Team team = new Team(UUID.randomUUID(), name);

        team.setTeamType(type);

        if (leaderUUID != null) {
            team.addMember(new TeamMember(leaderUUID, TeamRole.LEADER));
        }

        team.getOnlinePlayers().forEach(CommonsPlugin.getInstance().getNametagHandler()::reloadPlayer);

        this.cacheTeam(team);
        this.saveTeam(team, true);
    }

    public void cacheTeam(Team team) {
        this.teamIds.put(team.getId(), team);
        this.teamNames.put(team.getName().toLowerCase(), team);
        team.getMembersByUUID().forEach(uuid -> this.playerTeams.put(uuid, team));
    }

    public void removeTeam(Team team) {
        this.teamIds.remove(team.getId());
        this.teamNames.remove(team.getName().toLowerCase());
        team.getMembersByUUID().forEach(this.playerTeams::remove);
        team.getClaims().forEach(claimHandler::removeClaim);

        for (Team ally : team.getAllies()) {
            ally.removeAlliance(team);
        }

        Arrays.stream(WaypointType.values()).forEach(team::removeWaypoint);

        team.getOnlinePlayers().forEach(CommonsPlugin.getInstance().getNametagHandler()::reloadPlayer);
        updateTopTeams();
    }

    public void disbandTeam(Team team, boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> disbandTeam(team, false));
            return;
        }

        this.collection.deleteOne(Filters.eq("id", team.getId().toString()));
        this.removeTeam(team);
    }

    public void saveTeam(Team team, boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> saveTeam(team, false));
            return;
        }

        this.collection.replaceOne(Filters.eq("id", team.getId().toString()), team.toDocument(), new ReplaceOptions().upsert(true));
    }

    public List<Team> getTeams() {
        return new ArrayList<>(this.teamIds.values());
    }

    public List<Team> getOnlineTeams() {
        return getPlayerTeams().stream().filter(team -> !team.getOnlineMembers().isEmpty()).collect(Collectors.toList());
    }

    public List<Team> getPlayerTeams() {
        return getTeams().stream().filter(team -> team.getTeamType() == TeamType.PLAYER).collect(Collectors.toList());
    }

    public List<Team> getSystemTeams() {
        return getTeams().stream().filter(team -> team.getTeamType() != TeamType.PLAYER).collect(Collectors.toList());
    }

    public void sendTeamInfo(Player sender, Team team) {
        if (team.getTeamType() == TeamType.DEATHBAN_ARENA || team.getTeamType() == TeamType.ROAD) return;

        if (team.getTeamType() == TeamType.GLOWSTONE_MOUNTAIN) {
            LegendBukkit.getInstance().getLanguage().getStringList("team.info.format.glowstone").forEach(s -> sender.sendMessage(CC.translate(team.applyPlaceholders(s, sender))));
            return;
        }

        if (team.getTeamType() == TeamType.SPAWN) {
            LegendBukkit.getInstance().getLanguage().getStringList("team.info.format.spawn").forEach(s -> sender.sendMessage(CC.translate(team.applyPlaceholders(s, sender))));
            return;
        }

        LegendBukkit.getInstance().getLanguage().getStringList("team.info.format.player").forEach(s -> sender.sendMessage(CC.translate(team.applyPlaceholders(s, sender))));
    }

    public void updateTopTeams() {
        this.topTeams.clear();
        List<Team> teams = LegendBukkit.getInstance().getTeamHandler().getPlayerTeams();
        int amount = LegendBukkit.getInstance().getLanguage().getInt("team.top.amount");

        for (TopType type : TopType.values()) {
            List<Team> sorted = teams.stream().sorted(Comparator.comparingInt(t -> type.getGetter().apply((Team) t)).reversed()).collect(Collectors.toList());
            Map<Integer, TopEntry> topTeams = new HashMap<>();

            sorted.forEach(t -> {
                int place = sorted.indexOf(t) + 1;
                TopEntry entry = new TopEntry(type, t.getId(), type.getGetter().apply(t), place);

                type.update(t, place);

                if (place > amount) return;
                topTeams.put(place, entry);
            });

            this.topTeams.put(type, topTeams);
        }
    }

}
