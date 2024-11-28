package dev.lbuddyboy.legend.timer;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.timer.impl.*;
import dev.lbuddyboy.legend.timer.server.SOTWTimer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TimerHandler implements IModule {

    private final List<PlayerTimer> timers;
    private final List<ServerTimer> serverTimers;
    private Config config, serverTimerConfig;

    public TimerHandler() {
        this.timers = new ArrayList<>();
        this.serverTimers = new ArrayList<>();
    }

    @Override
    public void load() {
        this.config = new Config(LegendBukkit.getInstance(), "timers");
        this.serverTimerConfig = new Config(LegendBukkit.getInstance(), "server-timers");

        this.timers.addAll(Arrays.asList(
                new CrappleTimer(),
                new GappleTimer(),
                new CombatTimer(),
                new EnderPearlTimer(),
                new HomeTimer(),
                new StuckTimer(),
                new ArcherTagTimer(),
                new InvincibilityTimer()
        ));
        this.serverTimers.addAll(Arrays.asList(
                new SOTWTimer()
        ));
        this.serverTimers.forEach(t -> LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(t, LegendBukkit.getInstance()));
        this.timers.forEach(t -> LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(t, LegendBukkit.getInstance()));
    }

    @Override
    public void unload() {

    }

    public List<PlayerTimer> getActionBarTimers() {
        return this.timers.stream().filter(PlayerTimer::isActionbar).collect(Collectors.toList());
    }

    public List<PlayerTimer> getScoreboardTimers() {
        return this.timers.stream().filter(PlayerTimer::isScoreboard).collect(Collectors.toList());
    }

    public List<ServerTimer> getActionBarServerTimers() {
        return this.serverTimers.stream().filter(ServerTimer::isActionbar).collect(Collectors.toList());
    }

    public List<ServerTimer> getScoreboardServerTimers() {
        return this.serverTimers.stream().filter(ServerTimer::isScoreboard).collect(Collectors.toList());
    }

    public <T extends PlayerTimer> T getTimer(Class<T> clazz) {
        for (PlayerTimer timer : this.timers) {
            if (clazz.isInstance(timer)) return clazz.cast(timer);
        }
        return null;
    }

    public <T extends ServerTimer> T getServerTimer(Class<T> clazz) {
        for (ServerTimer timer : this.serverTimers) {
            if (clazz.isInstance(timer)) return clazz.cast(timer);
        }
        return null;
    }

}
