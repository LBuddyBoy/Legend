package dev.lbuddyboy.legend;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.lbuddyboy.api.leaderboard.model.LeaderboardDataEntry;
import dev.lbuddyboy.arrow.Arrow;
import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.api.mongo.MongoHandler;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.legend.actionbar.LegendActionBar;
import dev.lbuddyboy.legend.api.providers.*;
import dev.lbuddyboy.legend.classes.ClassHandler;
import dev.lbuddyboy.legend.command.CommandHandler;
import dev.lbuddyboy.legend.features.chatgames.ChatGameHandler;
import dev.lbuddyboy.legend.features.crystals.CrystalHandler;
import dev.lbuddyboy.legend.features.deathban.DeathbanHandler;
import dev.lbuddyboy.legend.features.enderdragon.EnderDragonHandler;
import dev.lbuddyboy.legend.features.gateways.GatewayHandler;
import dev.lbuddyboy.legend.features.glowstone.GlowstoneHandler;
import dev.lbuddyboy.legend.features.kitmap.KitMapHandler;
import dev.lbuddyboy.legend.features.leaderboard.LeaderBoardHandler;
import dev.lbuddyboy.legend.features.limiter.LimiterHandler;
import dev.lbuddyboy.legend.features.loothill.LootHillHandler;
import dev.lbuddyboy.legend.features.oremountain.OreMountainHandler;
import dev.lbuddyboy.legend.features.playtime.PlayTimeGoalHandler;
import dev.lbuddyboy.legend.features.recipe.RecipeHandler;
import dev.lbuddyboy.legend.features.schedule.ScheduleHandler;
import dev.lbuddyboy.legend.features.shop.ShopHandler;
import dev.lbuddyboy.legend.listener.*;
import dev.lbuddyboy.legend.nametag.LegendNametagProvider;
import dev.lbuddyboy.legend.packet.PacketHandler;
import dev.lbuddyboy.legend.scoreboard.LegendScoreboard;
import dev.lbuddyboy.legend.tab.LegendTabHandler;
import dev.lbuddyboy.legend.task.ClearLagTask;
import dev.lbuddyboy.legend.team.TeamHandler;
import dev.lbuddyboy.legend.timer.TimerHandler;
import dev.lbuddyboy.legend.user.UserHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Getter
public final class LegendBukkit extends JavaPlugin {

    public static final Executor POOL = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactoryBuilder().setDaemon(true).setNameFormat("%d - Prisons").build());

    @Getter
    private static LegendBukkit instance;
    @Getter
    private static boolean ENABLED;

    private Config language, settings, scoreboard;
    private List<IModule> modules;

    private MiniGamesProvider miniGamesProvider;

    private CommandHandler commandHandler;
    private MongoHandler mongoHandler;
    private TeamHandler teamHandler;
    private UserHandler userHandler;
    private TimerHandler timerHandler;
    private CrystalHandler crystalHandler;
    private ChatGameHandler chatGameHandler;
    private LegendTabHandler tabHandler;
    private GatewayHandler gatewayHandler;
    private DeathbanHandler deathbanHandler;
    private GlowstoneHandler glowstoneHandler;
    private LootHillHandler lootHillHandler;
    private OreMountainHandler oreMountainHandler;
    private ClassHandler classHandler;
    private PlayTimeGoalHandler playTimeGoalHandler;
    private RecipeHandler recipeHandler;
    private LeaderBoardHandler leaderBoardHandler;
    private KitMapHandler kitMapHandler;
    private LimiterHandler limiterHandler;
    private ShopHandler shopHandler;
    private ScheduleHandler scheduleHandler;
    private PacketHandler packetHandler;
    private EnderDragonHandler enderDragonHandler;

    @Override
    public void onEnable() {
        instance = this;
        ENABLED = true;

        this.saveDefaultConfig();
        this.loadConfigs();

        this.loadModules();
        this.loadListeners();
        this.loadWorlds();
        this.loadTasks();
    }

    @Override
    public void onDisable() {
        ENABLED = false;
        this.modules.forEach(IModule::unload);
        this.modules.forEach(IModule::save);
        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);
    }

    private void loadModules() {
        this.modules = new ArrayList<>(Arrays.asList(
                this.mongoHandler = new MongoHandler(
                        getConfig().getString("mongo.host"),
                        getConfig().getInt("mongo.port"),
                        getConfig().getBoolean("mongo.auth.enabled"),
                        getConfig().getString("mongo.auth.username"),
                        getConfig().getString("mongo.auth.password"),
                        getConfig().getString("mongo.auth.database"),
                        getConfig().getString("mongo.database")
                ),
                this.userHandler = new UserHandler(),
                this.teamHandler = new TeamHandler(),
                this.timerHandler = new TimerHandler(),
/*
                this.tabHandler = new LegendTabHandler(),
*/
                this.chatGameHandler = new ChatGameHandler(),
                this.crystalHandler = new CrystalHandler(),
                this.gatewayHandler = new GatewayHandler(),
                this.deathbanHandler = new DeathbanHandler(),
                this.glowstoneHandler = new GlowstoneHandler(),
                this.lootHillHandler = new LootHillHandler(),
                this.oreMountainHandler = new OreMountainHandler(),
                this.classHandler = new ClassHandler(),
                this.playTimeGoalHandler = new PlayTimeGoalHandler(),
                this.recipeHandler = new RecipeHandler(),
                this.leaderBoardHandler = new LeaderBoardHandler(),
                this.kitMapHandler = new KitMapHandler(),
                this.limiterHandler = new LimiterHandler(),
                this.shopHandler = new ShopHandler(),
                this.scheduleHandler = new ScheduleHandler(),
                this.packetHandler = new PacketHandler(),
                this.commandHandler = new CommandHandler(),
                this.enderDragonHandler = new EnderDragonHandler()
        ));

        this.loadProviders();

        this.modules.forEach(IModule::load);
    }

    private void loadListeners() {
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new DiamondListener(), this);
        this.getServer().getPluginManager().registerEvents(new BufferListener(), this);
        this.getServer().getPluginManager().registerEvents(new PreventionListener(), this);
        this.getServer().getPluginManager().registerEvents(new EndListener(), this);
        this.getServer().getPluginManager().registerEvents(new NetherListener(), this);
        this.getServer().getPluginManager().registerEvents(new ElevatorListener(), this);
        this.getServer().getPluginManager().registerEvents(new GeneralListener(), this);
        this.getServer().getPluginManager().registerEvents(new SpawnListener(), this);
        this.getServer().getPluginManager().registerEvents(new ModifierListener(), this);
        this.getServer().getPluginManager().registerEvents(new StatTrackListener(), this);

        if (this.settings.getBoolean("server.fast-smelt", true)) {
            this.getServer().getPluginManager().registerEvents(new FastListener(), this);
        }

        if (SettingsConfig.SETTINGS_UHC_MODE.getBoolean()) {
            this.getServer().getPluginManager().registerEvents(new UHCListener(), this);
        }
    }

    private void loadWorlds() {
        Bukkit.getWorlds().forEach(world -> {
            world.setGameRuleValue("logAdminCommands", "true");
            world.setGameRuleValue("doMobSpawning", "false");
            world.setGameRuleValue("doMobLoot", "true");
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("showDeathMessages", "false");
            world.setGameRuleValue("doInsomnia", "false");
            world.setGameRuleValue("disableRaids", "true");
            world.setGameRuleValue("doPatrolSpawning", "false");
            world.setGameRuleValue("doTraderSpawning", "false");
            world.setGameRuleValue("freezeDamage", "true");
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            world.setGameRule(GameRule.SPAWN_CHUNK_RADIUS, 2);
        });
    }

    public void loadConfigs() {
        this.language = new Config(this, "lang");
        this.settings = new Config(this, "settings");
        this.scoreboard = new Config(this, "scoreboard");

        for (LangConfig value : LangConfig.values()) value.loadDefault();
        for (SettingsConfig value : SettingsConfig.values()) value.loadDefault();
    }

    public Location getSpawnLocation() {
        return Bukkit.getWorlds().getFirst().getSpawnLocation();
    }

    private void loadProviders() {
        String serverName = SettingsConfig.SETTINGS_SERVER_NAME.getString();

        if (Bukkit.getPluginManager().getPlugin("Rollback") != null) new RollbackProvider();
        if (Bukkit.getPluginManager().getPlugin("CombatLogger") != null) new CombatLoggerProvider();
        if (Bukkit.getPluginManager().getPlugin("Events") != null) new EventProvider();
        if (Bukkit.getPluginManager().getPlugin("Crates") != null) new CrateProvider();
        if (Bukkit.getPluginManager().getPlugin("AuctionHouse") != null) new AuctionHouseProvider();
        if (Bukkit.getPluginManager().getPlugin("Claim") != null) new ClaimHook();
        if (Bukkit.getPluginManager().getPlugin("Warps") != null) new WarpHook();
        if (Bukkit.getPluginManager().getPlugin("Duels") != null) new DuelProvider();
        if (Bukkit.getPluginManager().getPlugin("AbilityItems") != null) new AbilityProvider();
        if (Bukkit.getPluginManager().getPlugin("MiniGames") != null) this.miniGamesProvider = new MiniGamesProvider();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) new PlaceholderAPIHook().register();

        CommonsPlugin.getInstance().getDeathMessageHandler().setProvider(new DeathProvider());
        CommonsPlugin.getInstance().getScoreboardHandler().registerProvider(new LegendScoreboard());
        CommonsPlugin.getInstance().getActionBarHandler().registerProvider(new LegendActionBar());
        CommonsPlugin.getInstance().getNameTagHandler().setNameTagImpl(new LegendNametagProvider());

        Arrow.getInstance().getLeaderboardHandler().createAndSaveEntry(serverName, "Kill", "Kills");
        Arrow.getInstance().getLeaderboardHandler().createAndSaveEntry(serverName, "Death", "Deaths");
        Arrow.getInstance().getLeaderboardHandler().createAndSaveEntry(serverName, "HighestKillStreak", "Highest KillStreak");
        Arrow.getInstance().getLeaderboardHandler().createAndSaveEntry(serverName, "Money", "Money");
        Arrow.getInstance().getLeaderboardHandler().createAndSaveEntry(serverName, "PlayTime", "Play Time");

    }

    private void loadTasks() {
        new ClearLagTask().runTaskTimer(this, 20 * 60, 20 * 60);
    }

    public LeaderboardDataEntry getDataEntry(String name) {
        String serverName = SettingsConfig.SETTINGS_SERVER_NAME.getString();

        return Arrow.getInstance().getLeaderboardHandler().getDataEntry(serverName, name);
    }

}
