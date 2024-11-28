package dev.lbuddyboy.legend.features.playtime;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.playtime.model.PlayTimeGoal;
import dev.lbuddyboy.legend.user.model.LegendUser;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class PlayTimeGoalHandler implements IModule {

    private Map<String, PlayTimeGoal> playTimeGoals;
    private File goalDirectory;

    @Override
    public void load() {
        reload();

        Tasks.runTimer(() -> {
            long combinedTotals = Bukkit.getOnlinePlayers().size() * 1000L;

            for (PlayTimeGoal playTimeGoal : this.playTimeGoals.values()) {
                if (playTimeGoal.isExpired() && playTimeGoal.getNotReachedAt() <= 0) {
                    playTimeGoal.setNotReachedAt(System.currentTimeMillis());

                    LegendBukkit.getInstance().getLanguage().getStringList("playtime.goal.incomplete").forEach(s -> Bukkit.broadcastMessage(CC.translate(playTimeGoal.applyPlaceholders(s))));
                }
                if (playTimeGoal.getNotReachedAt() > 0 && System.currentTimeMillis() - playTimeGoal.getNotReachedAt() > 15_000) {
                    deletePlayTimeGoal(playTimeGoal);
                }
            }

            getActiveGoals().forEach(playTimeGoal -> {
                playTimeGoal.setProgress(playTimeGoal.getProgress() + combinedTotals);

                if (playTimeGoal.isCompleted()) {
                    playTimeGoal.setNotReachedAt(System.currentTimeMillis());
                    LegendBukkit.getInstance().getLanguage().getStringList("playtime.goal.completed").forEach(s -> Bukkit.broadcastMessage(CC.translate(playTimeGoal.applyPlaceholders(s))));
                    playTimeGoal.getCommands().forEach(s -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s));
                }
            });
        }, 20, 20);
    }

    @Override
    public void unload() {
        this.playTimeGoals.values().forEach(PlayTimeGoal::save);
    }

    @Override
    public void reload() {
        this.playTimeGoals = new HashMap<>();
        this.goalDirectory = new File(LegendBukkit.getInstance().getDataFolder(), "playtime-goals");
        if (!this.goalDirectory.exists()) this.goalDirectory.mkdir();

        for (String string : this.goalDirectory.list()) {
            Config config = new Config(LegendBukkit.getInstance(), string.replaceAll(".yml", ""), this.goalDirectory);
            PlayTimeGoal playTimeGoal = new PlayTimeGoal(config);

            this.registerPlayTimeGoal(playTimeGoal);
        }

        LegendBukkit.getInstance().getLogger().info("Loaded " + this.playTimeGoals.size() + " playtime goals!");
    }

    public List<PlayTimeGoal> getActiveGoals() {
        return this.playTimeGoals.values().stream().filter(pt -> !pt.isExpired() && !pt.isCompleted()).collect(Collectors.toList());
    }

    public void registerPlayTimeGoal(PlayTimeGoal goal) {
        this.playTimeGoals.put(goal.getId().toLowerCase(), goal);
    }

    public void deletePlayTimeGoal(PlayTimeGoal goal) {
        this.playTimeGoals.remove(goal.getId().toLowerCase());
        if (goal.getConfig().getFile().exists()) goal.getConfig().getFile().delete();
    }

}
