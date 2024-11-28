package dev.lbuddyboy.legend;

import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.api.mongo.MongoHandler;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.legend.actionbar.LegendActionBar;
import dev.lbuddyboy.legend.command.CommandHandler;
import dev.lbuddyboy.legend.listener.BufferListener;
import dev.lbuddyboy.legend.listener.ChatListener;
import dev.lbuddyboy.legend.nametag.LegendNametagProvider;
import dev.lbuddyboy.legend.scoreboard.LegendScoreboard;
import dev.lbuddyboy.legend.tab.LegendTabHandler;
import dev.lbuddyboy.legend.team.TeamHandler;
import dev.lbuddyboy.legend.timer.TimerHandler;
import dev.lbuddyboy.legend.user.UserHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public final class LegendBukkit extends JavaPlugin {

    @Getter private static LegendBukkit instance;

    private Config language, settings;
    private List<IModule> modules;

    private CommandHandler commandHandler;
    private MongoHandler mongoHandler;
    private TeamHandler teamHandler;
    private UserHandler userHandler;
    private TimerHandler timerHandler;
    private LegendTabHandler tabHandler;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.language = new Config(this, "lang");
        this.settings = new Config(this, "settings");
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
                this.tabHandler = new LegendTabHandler()
        ));

        CommonsPlugin.getInstance().getScoreboardHandler().getScoreboards().clear();
        CommonsPlugin.getInstance().getActionBarHandler().getProviders().clear();
        CommonsPlugin.getInstance().getNameTagHandler().getImpls().clear();
        CommonsPlugin.getInstance().getScoreboardHandler().registerProvider(new LegendScoreboard());
        CommonsPlugin.getInstance().getActionBarHandler().registerProvider(new LegendActionBar());
        CommonsPlugin.getInstance().getNameTagHandler().registerProvider(new LegendNametagProvider());

        this.modules.forEach(IModule::load);
    }

    private void loadListeners() {
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new BufferListener(), this);
    }

    private void loadWorlds() {
        Bukkit.getWorlds().forEach(world -> {
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            world.setGameRule(GameRule.DO_INSOMNIA, false);
            world.setGameRule(GameRule.DISABLE_RAIDS, true);
            world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
            world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        });
    }

}
