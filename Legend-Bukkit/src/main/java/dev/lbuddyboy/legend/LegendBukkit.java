package dev.lbuddyboy.legend;

import dev.lbuddyboy.api.leaderboard.model.LeaderboardDataEntry;
import dev.lbuddyboy.arrow.Arrow;
import dev.lbuddyboy.auctionhouse.AuctionHouse;
import dev.lbuddyboy.combatlogger.CombatLoggerPlugin;
import dev.lbuddyboy.commons.Commons;
import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.api.mongo.MongoHandler;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.crates.Crates;
import dev.lbuddyboy.events.Events;
import dev.lbuddyboy.legend.actionbar.LegendActionBar;
import dev.lbuddyboy.legend.api.providers.*;
import dev.lbuddyboy.legend.classes.ClassHandler;
import dev.lbuddyboy.legend.command.CommandHandler;
import dev.lbuddyboy.legend.features.deathban.DeathbanHandler;
import dev.lbuddyboy.legend.features.glowstone.GlowstoneHandler;
import dev.lbuddyboy.legend.features.kitmap.KitMapHandler;
import dev.lbuddyboy.legend.features.leaderboard.LeaderBoardHandler;
import dev.lbuddyboy.legend.features.limiter.LimiterHandler;
import dev.lbuddyboy.legend.features.limiter.listener.PotionLimitListener;
import dev.lbuddyboy.legend.features.playtime.PlayTimeGoalHandler;
import dev.lbuddyboy.legend.features.recipe.RecipeHandler;
import dev.lbuddyboy.legend.features.schedule.ScheduleHandler;
import dev.lbuddyboy.legend.features.shop.ShopHandler;
import dev.lbuddyboy.legend.listener.*;
import dev.lbuddyboy.legend.nametag.LegendNametagProvider;
import dev.lbuddyboy.legend.scoreboard.LegendScoreboard;
import dev.lbuddyboy.legend.tab.LegendTabHandler;
import dev.lbuddyboy.legend.team.TeamHandler;
import dev.lbuddyboy.legend.timer.TimerHandler;
import dev.lbuddyboy.legend.user.UserHandler;
import dev.lbuddyboy.rollback.Rollback;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public final class LegendBukkit extends JavaPlugin {

    @Getter private static LegendBukkit instance;
    @Getter private static boolean ENABLED;

    private Config language, settings, scoreboard;
    private List<IModule> modules;

    private CommandHandler commandHandler;
    private MongoHandler mongoHandler;
    private TeamHandler teamHandler;
    private UserHandler userHandler;
    private TimerHandler timerHandler;
    private LegendTabHandler tabHandler;
    private DeathbanHandler deathbanHandler;
    private GlowstoneHandler glowstoneHandler;
    private ClassHandler classHandler;
    private PlayTimeGoalHandler playTimeGoalHandler;
    private RecipeHandler recipeHandler;
    private LeaderBoardHandler leaderBoardHandler;
    private KitMapHandler kitMapHandler;
    private LimiterHandler limiterHandler;
    private ShopHandler shopHandler;
    private ScheduleHandler scheduleHandler;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.language = new Config(this, "lang");
        this.settings = new Config(this, "settings");
        this.scoreboard = new Config(this, "scoreboard");
        instance = this;

        this.loadModules();
        this.loadListeners();
        this.loadWorlds();
    }

    @Override
    public void onDisable() {
        ENABLED = false;
        this.modules.forEach(IModule::unload);
        this.modules.forEach(IModule::save);
        HandlerList.unregisterAll(this);
    }

    private void loadModules() {
        this.modules = new ArrayList<>(Arrays.asList(
                this.commandHandler = new CommandHandler(),
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
                this.tabHandler = new LegendTabHandler(),
                this.deathbanHandler = new DeathbanHandler(),
                this.glowstoneHandler = new GlowstoneHandler(),
                this.classHandler = new ClassHandler(),
                this.playTimeGoalHandler = new PlayTimeGoalHandler(),
                this.recipeHandler = new RecipeHandler(),
                this.leaderBoardHandler = new LeaderBoardHandler(),
                this.kitMapHandler = new KitMapHandler(),
                this.limiterHandler = new LimiterHandler(),
                this.shopHandler = new ShopHandler(),
                this.scheduleHandler = new ScheduleHandler()
        ));

        this.loadProviders();

        this.modules.forEach(IModule::load);
        this.loadPlaceholders();

        ENABLED = true;
    }

    private void loadListeners() {
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new BufferListener(), this);
        this.getServer().getPluginManager().registerEvents(new PreventionListener(), this);
        this.getServer().getPluginManager().registerEvents(new EndListener(), this);
        this.getServer().getPluginManager().registerEvents(new NetherListener(), this);
        this.getServer().getPluginManager().registerEvents(new ElevatorListener(), this);
        this.getServer().getPluginManager().registerEvents(new GeneralListener(), this);
        this.getServer().getPluginManager().registerEvents(new SpawnListener(), this);

        if (this.settings.getBoolean("server.fast-smelt", true)) {
            this.getServer().getPluginManager().registerEvents(new FastListener(), this);
        }
        if (this.settings.getBoolean("server.uhc-mode", false)) {
            this.getServer().getPluginManager().registerEvents(new UHCListener(), this);
        }
    }

    private void loadWorlds() {
        Bukkit.getWorlds().forEach(world -> {
            world.setGameRuleValue("logAdminCommands", "true");
            world.setGameRuleValue("doMobSpawning", "true");
            world.setGameRuleValue("doMobLoot", "true");
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("showDeathMessages", "false");
            world.setGameRuleValue("doInsomnia", "false");
            world.setGameRuleValue("disableRaids", "true");
            world.setGameRuleValue("doPatrolSpawning", "false");
            world.setGameRuleValue("doTraderSpawning", "false");
            world.setGameRuleValue("doWeatherCycle", "false");
            world.setGameRuleValue("freezeDamage", "true");
        });
    }

    public Location getSpawnLocation() {
        return Bukkit.getWorlds().get(0).getSpawnLocation();
    }

    public void loadPlaceholders() {
        int allySize = this.getSettings().getInt("server.allies");
        String serverName = this.getSettings().getString("server.name").toLowerCase();

        Commons.getInstance().getRedisHandler().updateGlobalPlaceholder("%" + serverName + "-team-size%", String.valueOf(this.getSettings().getInt("server.team-size")));
        Commons.getInstance().getRedisHandler().updateGlobalPlaceholder("%" + serverName  + "-allies%", String.valueOf(allySize == 0 ? "&c&lDISABLED" : allySize));
        Commons.getInstance().getRedisHandler().updateGlobalPlaceholder("%" + serverName  + "-map-kit%", "Prot II Sharp II"); // TODO integrate w enchant limiter
        Commons.getInstance().getRedisHandler().updateGlobalPlaceholder("%" + serverName  + "-map-length%", this.getSettings().getString("server.map-length", "30d"));
        Commons.getInstance().getRedisHandler().updateGlobalPlaceholder("%" + serverName  + "-release%", String.valueOf(this.getSettings().getLong("server.released-at", -1L)));

    }

    private void loadProviders() {
        String serverName = this.getSettings().getString("server.name");

        if (Bukkit.getPluginManager().getPlugin("Rollback") != null) new RollbackProvider();
        if (Bukkit.getPluginManager().getPlugin("CombatLogger") != null) new CombatLoggerProvider();
        if (Bukkit.getPluginManager().getPlugin("Events") != null) new EventProvider();
        if (Bukkit.getPluginManager().getPlugin("Crates") != null) new CrateProvider();
        if (Bukkit.getPluginManager().getPlugin("AuctionHouse") != null) new AuctionHouseProvider();

        CommonsPlugin.getInstance().getDeathMessageHandler().setProvider(new DeathProvider());
        CommonsPlugin.getInstance().getScoreboardHandler().registerProvider(new LegendScoreboard());
        CommonsPlugin.getInstance().getActionBarHandler().registerProvider(new LegendActionBar());
        CommonsPlugin.getInstance().getNameTagHandler().registerProvider(new LegendNametagProvider());

        Arrow.getInstance().getLeaderboardHandler().createAndSaveEntry(serverName, "Kill", "Kills");
        Arrow.getInstance().getLeaderboardHandler().createAndSaveEntry(serverName, "Death", "Deaths");
        Arrow.getInstance().getLeaderboardHandler().createAndSaveEntry(serverName, "HighestKillStreak", "Highest KillStreak");
        Arrow.getInstance().getLeaderboardHandler().createAndSaveEntry(serverName, "Money", "Money");
        Arrow.getInstance().getLeaderboardHandler().createAndSaveEntry(serverName, "PlayTime", "Play Time");
    }

    public LeaderboardDataEntry getDataEntry(String name) {
        String serverName = this.getSettings().getString("server.name");

        return Arrow.getInstance().getLeaderboardHandler().getDataEntry(serverName, name);
    }

}
