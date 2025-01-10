package dev.lbuddyboy.legend.team;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.team.TeamModule;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.commons.api.data.impl.MongoDataStorage;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.command.impl.EndPortalCommand;
import dev.lbuddyboy.legend.team.config.TeamConfig;
import dev.lbuddyboy.legend.team.listener.*;
import dev.lbuddyboy.legend.team.model.*;
import dev.lbuddyboy.legend.team.model.generator.GeneratorLootTable;
import dev.lbuddyboy.legend.team.model.generator.GeneratorTier;
import dev.lbuddyboy.legend.team.model.generator.upgrades.AbstractGeneratorUpgrade;
import dev.lbuddyboy.legend.team.model.generator.upgrades.impl.EfficiencyUpgrade;
import dev.lbuddyboy.legend.team.model.generator.upgrades.impl.FortuneUpgrade;
import dev.lbuddyboy.legend.team.model.generator.upgrades.impl.XPFinderUpgrade;
import dev.lbuddyboy.legend.team.model.log.TeamLog;
import dev.lbuddyboy.legend.team.model.log.impl.TeamCreationLog;
import dev.lbuddyboy.legend.team.model.log.impl.TeamDTRChangeLog;
import dev.lbuddyboy.legend.team.thread.ClaimBorderThread;
import dev.lbuddyboy.legend.team.thread.DTRThread;
import dev.lbuddyboy.legend.team.thread.TeamSaveThread;
import dev.lbuddyboy.legend.team.thread.TeamTopThread;
import dev.lbuddyboy.legend.user.model.LegendUser;
import dev.lbuddyboy.legend.user.model.nametag.ScoreboardEntryType;
import dev.lbuddyboy.legend.util.BukkitUtil;
import dev.lbuddyboy.legend.util.NameTagUtil;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Getter
public class TeamHandler implements IModule {

    private final Map<UUID, Team> teamIds;
    private final Map<String, Team> teamNames;
    private final Map<UUID, Team> playerTeams;
    private final Map<TopType, Map<Integer, TopEntry>> topTeams;
    private final List<TeamLog> logsToSave;
    private final List<AbstractGeneratorUpgrade> generatorUpgrades;
    private final Map<String, GeneratorTier> tiers;

    private final ClaimHandler claimHandler;
    private final MovementHandler movementHandler;

    private MongoCollection<Document> collection, logCollection;
    private Config config;
    private GeneratorLootTable generatorLootTable;

    public TeamHandler() {
        this.teamIds = new ConcurrentHashMap<>();
        this.teamNames = new ConcurrentHashMap<>();
        this.playerTeams = new ConcurrentHashMap<>();
        this.topTeams = new ConcurrentHashMap<>();
        this.claimHandler = new ClaimHandler();
        this.movementHandler = new MovementHandler();
        this.logsToSave = new CopyOnWriteArrayList<>();
        this.generatorUpgrades = new ArrayList<>();
        this.tiers = new HashMap<>();
    }

    @Override
    public void load() {
        this.collection = LegendBukkit.getInstance().getMongoHandler().getDatabase().getCollection("Teams");
        this.logCollection = LegendBukkit.getInstance().getMongoHandler().getDatabase().getCollection("TeamLogs");

        this.loadListeners();
        this.loadTeams();
        this.claimHandler.load();

        new TeamSaveThread().start();
        new DTRThread().start();
        new TeamTopThread().start();
        new ClaimBorderThread().start();

        Bukkit.getScheduler().runTaskTimerAsynchronously(LegendBukkit.getInstance(), () -> {
            this.logsToSave.forEach(change -> this.saveTeamLog(change, true));
            this.logsToSave.clear();
        }, 20 * 60, 20 * 60);

        Bukkit.getScheduler().runTaskTimerAsynchronously(LegendBukkit.getInstance(), () -> {
            for (Team team : getOnlineTeams()) {
                if (team.getOnlinePlayers().size() <= 1) continue;

                team.refreshTeamView();
            }
        }, 10, 10);

        this.generatorLootTable = new GeneratorLootTable();
        this.generatorLootTable.register();
        this.generatorLootTable.registerCommandInfo(LegendBukkit.getInstance().getCommandHandler().getCommandManager());

        this.generatorUpgrades.addAll(Arrays.asList(
                new EfficiencyUpgrade(),
                new FortuneUpgrade(),
                new XPFinderUpgrade()
        ));

        this.updateTopTeams();

        reload();
    }

    @Override
    public void unload() {
        this.logsToSave.forEach(change -> this.saveTeamLog(change, false));
        this.teamIds.values().forEach(team -> saveTeam(team, false));
        this.logsToSave.clear();
    }

    @Override
    public void reload() {
        this.config = new Config(LegendBukkit.getInstance(), "team");
        for (TeamConfig value : TeamConfig.values()) value.loadDefault();

        this.loadTiers();
    }

    private void loadTiers() {
        this.tiers.clear();

        ConfigurationSection section = this.config.getConfigurationSection("generatorTiers");

        if (section == null) {
            LegendBukkit.getInstance().getLogger().warning("Trouble loading generator tiers: Please validate the YML!");
            return;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection tierSection = section.getConfigurationSection(key);

            if (tierSection == null) {
                LegendBukkit.getInstance().getLogger().warning("Trouble loading " + key + " tier: Please validate the YML!");
                continue;
            }

            GeneratorTier tier = new GeneratorTier(key, tierSection);

            this.tiers.put(key, tier);
            tier.getLootTable();
        }
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
        LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(new TeamLogListener(), LegendBukkit.getInstance());
        LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(new AutoRebuildListener(), LegendBukkit.getInstance());
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

    public List<String> getDisallowedNames() {
        return SettingsConfig.SETTINGS_DISALLOWED_TEAMS.getStringList();
    }

    public boolean isDisallowed(String name) {
        return getDisallowedNames().stream().anyMatch(s -> s.equalsIgnoreCase(name.toLowerCase()
                .replaceAll("0", "o")
                .replaceAll("1", "i")
                .replaceAll("3", "e")
        ));
    }

    public void saveTeamLog(TeamLog log, boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> saveTeamLog(log, false));
            return;
        }

        this.logCollection.replaceOne(Filters.eq("id", log.getId().toString()), log.toDocument(), new ReplaceOptions().upsert(true));
    }

    public void renameTeam(Team team, String name) {
        this.teamNames.remove(team.getName());

        team.setName(name);
        team.flagSave();
        this.teamNames.put(name.toLowerCase(), team);
        NameTagUtil.updateTargetsForViewers(team.getOnlinePlayers(), BukkitUtil.getOnlinePlayers());
    }

    public void removePlayerFromTeam(Team team, UUID playerUUID) {
        TeamModule teamModule = Apollo.getModuleManager().getModule(TeamModule.class);

        for (Player player : team.getOnlinePlayers()) {
            Apollo.getPlayerManager().getPlayer(player.getUniqueId())
                    .ifPresent(teamModule::resetTeamMembers);
        }

        team.onHistoricalMemberLeave(playerUUID);
        team.updateNameTags();
        team.removeMember(playerUUID);
        this.playerTeams.remove(playerUUID);

        if (!team.isDTRFrozen()) {
            double previousDTR = team.getDeathsUntilRaidable();

            team.setDeathsUntilRaidable(team.getMaxDTR() - 1.0D);
            team.createTeamLog(new TeamDTRChangeLog(team.getId(), previousDTR, team.getDeathsUntilRaidable(), playerUUID, TeamDTRChangeLog.ChangeCause.MEMBER_LEFT));
        }

        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            Arrays.stream(WaypointType.values()).forEach(type -> team.removeWaypoint(type.getWaypointName(), player));

            NameTagUtil.updateTargetsForViewers(team.getOnlinePlayers(), BukkitUtil.getOnlinePlayers());
            player.closeInventory();
        }

    }

    public void addPlayerToTeam(Team team, UUID playerUUID, TeamRole role) {
        TeamMember member = new TeamMember(playerUUID, role);
        LegendUser memberUser = LegendBukkit.getInstance().getUserHandler().getUser(playerUUID);

        team.onHistoricalMemberJoin(playerUUID);
        team.getMembers().add(member);
        this.playerTeams.put(playerUUID, team);
    }

    public Team createTeam(String name, UUID leaderUUID) {
        return createTeam(name, leaderUUID, TeamType.PLAYER);
    }

    public Team createTeam(String name, UUID leaderUUID, TeamType type) {
        Team team = new Team(UUID.randomUUID(), name);

        team.setTeamType(type);

        if (leaderUUID != null) {
            team.addMember(new TeamMember(leaderUUID, TeamRole.LEADER));
            team.createTeamLog(new TeamCreationLog(team.getId(), leaderUUID));
        }

        NameTagUtil.updateTargetsForViewers(team.getOnlinePlayers(), BukkitUtil.getOnlinePlayers());

        this.cacheTeam(team);
        this.saveTeam(team, true);
        return team;
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

        for (Player player : team.getOnlinePlayers()) {
            player.closeInventory();
        }

        Arrays.stream(WaypointType.values()).forEach(team::removeWaypoint);

        NameTagUtil.updateTargetsForViewers(team.getOnlinePlayers(), BukkitUtil.getOnlinePlayers());
        updateTopTeams();
    }

    public void disbandTeam(Team team, boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> disbandTeam(team, false)).exceptionally(throwable -> {
                if (throwable != null) throwable.printStackTrace();

                return null;
            });
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

    public List<GeneratorTier> getSortedTiers() {
        return this.tiers.values().stream().sorted(Comparator.comparingInt(GeneratorTier::getTierRequired)).toList();
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

        if (team.getTeamType() == TeamType.LOOTHILL) {
            LegendBukkit.getInstance().getLanguage().getStringList("team.info.format.loothill").forEach(s -> sender.sendMessage(CC.translate(team.applyPlaceholders(s, sender))));
            return;
        }

        if (team.getTeamType() == TeamType.ENDPORTAL) {
            EndPortalCommand.def(sender);
            return;
        }

        if (team.getTeamType() == TeamType.GLOWSTONE_MOUNTAIN) {
            LegendBukkit.getInstance().getLanguage().getStringList("team.info.format.glowstone").forEach(s -> sender.sendMessage(CC.translate(team.applyPlaceholders(s, sender))));
            return;
        }

        if (team.getTeamType() == TeamType.ORE_MOUNTAIN) {
            LegendBukkit.getInstance().getLanguage().getStringList("team.info.format.oremountain").forEach(s -> sender.sendMessage(CC.translate(team.applyPlaceholders(s, sender))));
            return;
        }

        if (team.getTeamType() == TeamType.SPAWN) {
            LegendBukkit.getInstance().getLanguage().getStringList("team.info.format.spawn").forEach(s -> sender.sendMessage(CC.translate(team.applyPlaceholders(s, sender))));
            return;
        }

        if (team.getTeamType() == TeamType.KOTH) {
            LegendBukkit.getInstance().getLanguage().getStringList("team.info.format.koth").forEach(s -> sender.sendMessage(CC.translate(team.applyPlaceholders(s, sender))));
            return;
        }

        if (team.getTeamType() == TeamType.CITADEL) {
            LegendBukkit.getInstance().getLanguage().getStringList("team.info.format.citadel").forEach(s -> sender.sendMessage(CC.translate(team.applyPlaceholders(s, sender))));
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
