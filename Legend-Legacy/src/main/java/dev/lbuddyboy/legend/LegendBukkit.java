package dev.lbuddyboy.legend;

import dev.lbuddyboy.commons.Commons;
import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.api.mongo.MongoHandler;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.crates.lCrates;
import dev.lbuddyboy.events.Events;
import dev.lbuddyboy.legend.actionbar.LegendActionBar;
import dev.lbuddyboy.legend.api.providers.CrateProvider;
import dev.lbuddyboy.legend.api.providers.DeathProvider;
import dev.lbuddyboy.legend.api.providers.EventProvider;
import dev.lbuddyboy.legend.api.providers.RollbackProvider;
import dev.lbuddyboy.legend.classes.ClassHandler;
import dev.lbuddyboy.legend.command.CommandHandler;
import dev.lbuddyboy.legend.features.deathban.DeathbanHandler;
import dev.lbuddyboy.legend.features.glowstone.GlowstoneHandler;
import dev.lbuddyboy.legend.features.leaderboard.LeaderBoardHandler;
import dev.lbuddyboy.legend.features.limiter.listener.PotionLimitListener;
import dev.lbuddyboy.legend.features.playtime.PlayTimeGoalHandler;
import dev.lbuddyboy.legend.features.recipe.RecipeHandler;
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
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public final class LegendBukkit extends JavaPlugin {

    @Getter private static LegendBukkit instance;

    private Config language, settings, limiter, scoreboard;
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

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.language = new Config(this, "lang");
        this.settings = new Config(this, "settings");
        this.limiter = new Config(this, "limiter");
        this.scoreboard = new Config(this, "scoreboard");
        instance = this;

        this.loadModules();
        this.loadListeners();
        this.loadWorlds();
    }

    @Override
    public void onDisable() {
        this.modules.forEach(IModule::unload);
        this.modules.forEach(IModule::save);
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
                this.leaderBoardHandler = new LeaderBoardHandler()
        ));

        this.loadProviders();

        this.modules.forEach(IModule::load);
        this.loadPlaceholders();
    }

    private void loadListeners() {
        this.getServer().getPluginManager().registerEvents(new ShopHandler(), this);
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new BufferListener(), this);
        this.getServer().getPluginManager().registerEvents(new PreventionListener(), this);
        this.getServer().getPluginManager().registerEvents(new EndListener(), this);
        this.getServer().getPluginManager().registerEvents(new NetherListener(), this);
        this.getServer().getPluginManager().registerEvents(new ElevatorListener(), this);
        this.getServer().getPluginManager().registerEvents(new FastListener(), this);
    }

    private void loadWorlds() {
        Bukkit.getWorlds().forEach(world -> {
            world.setGameRuleValue("logAdminCommands", "true");
            world.setGameRuleValue("doMobSpawning", "true");
            world.setGameRuleValue("doMobLoot", "true");
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("showDeathMessages", "false");
        });
    }

    public Location getSpawnLocation() {
        return Bukkit.getWorlds().get(0).getSpawnLocation();
    }

    private void loadPlaceholders() {
        int allySize = this.getSettings().getInt("server.allies");
        String serverName = this.getSettings().getString("server.name").toLowerCase();

        Commons.getInstance().getRedisHandler().updateGlobalPlaceholder("%" + serverName + "-team-size%", String.valueOf(this.getSettings().getInt("server.team-size")));
        Commons.getInstance().getRedisHandler().updateGlobalPlaceholder("%" + serverName  + "-allies%", String.valueOf(allySize == 0 ? "&c&lDISABLED" : allySize));
        Commons.getInstance().getRedisHandler().updateGlobalPlaceholder("%" + serverName  + "-map-kit%", "Prot II Sharp II"); // TODO integrate w enchant limiter

    }

    private void loadProviders() {
        if (Bukkit.getPluginManager().getPlugin("Rollback") != null) Rollback.getInstance().setRollbackAPI(new RollbackProvider());
        // if (Bukkit.getPluginManager().getPlugin("CombatLogger") != null) CombatLoggerPlugin.getInstance().registerAPI(new CombatLoggerProvider());
        if (Bukkit.getPluginManager().getPlugin("Events") != null) Events.registerAPI(new EventProvider());
        if (Bukkit.getPluginManager().getPlugin("lCrates") != null) lCrates.getInstance().registerAPI(new CrateProvider());

        CommonsPlugin.getInstance().getDeathMessageHandler().setProvider(new DeathProvider());
        CommonsPlugin.getInstance().getScoreboardHandler().registerProvider(new LegendScoreboard());
        CommonsPlugin.getInstance().getActionBarHandler().registerProvider(new LegendActionBar());
        CommonsPlugin.getInstance().getNametagHandler().registerProvider(new LegendNametagProvider());
    }

}
