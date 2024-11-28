package dev.lbuddyboy.legend.timer;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.timer.impl.CombatTimer;
import dev.lbuddyboy.legend.timer.impl.EnderPearlTimer;
import dev.lbuddyboy.legend.timer.impl.HomeTimer;
import dev.lbuddyboy.legend.timer.impl.InvincibilityTimer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class TimerHandler implements IModule {

    private final List<PlayerTimer> timers;
    private Config config;

    public TimerHandler() {
        this.timers = new ArrayList<>();
    }

    @Override
    public void load() {
        this.config = new Config(LegendBukkit.getInstance(), "timers");

        this.timers.addAll(Arrays.asList(
                new CombatTimer(),
                new EnderPearlTimer(),
                new HomeTimer(),
                new InvincibilityTimer()
        ));
        this.timers.forEach(t -> LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(t, LegendBukkit.getInstance()));
    }

    @Override
    public void unload() {

    }

    public List<PlayerTimer> getActionBarTimers() {
        return this.timers.stream().filter(PlayerTimer::isActionbar).toList();
    }

    public List<PlayerTimer> getScoreboardTimers() {
        return this.timers.stream().filter(PlayerTimer::isScoreboard).toList();
    }

    public <T extends PlayerTimer> PlayerTimer getTimer(Class<T> clazz) {
        for (PlayerTimer timer : this.timers) {
            if (clazz.isInstance(timer)) return timer;
        }
        return null;
    }

}
