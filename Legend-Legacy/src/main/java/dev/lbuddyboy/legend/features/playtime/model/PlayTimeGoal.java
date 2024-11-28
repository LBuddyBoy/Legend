package dev.lbuddyboy.legend.features.playtime.model;

import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.model.PersistentTimer;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlayTimeGoal {

    private final Config config;

    private String id, reward;
    private long startedAt, duration, playTimeGoal;
    private List<String> commands = new ArrayList<>();
    private long progress, notReachedAt;
    private MaterialData materialData;

    public PlayTimeGoal(Config config) {
        this.config = config;
        this.id = config.getFileName().replaceAll(".yml", "");
        this.reward = config.getString("reward");
        this.startedAt = config.getLong("startedAt");
        this.duration = config.getLong("duration");
        this.playTimeGoal = config.getLong("playTimeGoal");
        this.progress = config.getLong("progress");
        this.commands.addAll(config.getStringList("commands"));
        this.materialData = new MaterialData(Material.getMaterial(config.getString("display-material.material")), (byte) config.getInt("display-material.data"));
    }

    public PlayTimeGoal(String id, String reward, long duration, long playTimeGoal) {
        this.id = id;
        this.reward = reward;
        this.duration = duration;
        this.playTimeGoal = playTimeGoal;
        this.startedAt = System.currentTimeMillis();
        this.config = new Config(LegendBukkit.getInstance(), id.toLowerCase(), LegendBukkit.getInstance().getPlayTimeGoalHandler().getGoalDirectory());
        this.materialData = new MaterialData(Material.WATCH, (byte) 0);
    }

    public void save() {
        this.config.set("reward", this.reward);
        this.config.set("startedAt", this.startedAt);
        this.config.set("duration", this.duration);
        this.config.set("playTimeGoal", this.playTimeGoal);
        this.config.set("commands", this.commands);
        this.config.set("progress", this.progress);
        this.config.set("display-material.material", this.materialData.getItemType().name());
        this.config.set("display-material.data", this.materialData.getData());
        this.config.save();
    }

    public boolean isCompleted() {
        return this.progress >= this.playTimeGoal;
    }

    public boolean isExpired() {
        return getRemaining() <= 0;
    }

    public long getRemaining() {
        return (this.duration + this.startedAt) - System.currentTimeMillis();
    }

    public String applyPlaceholders(String s) {
        return s

                .replaceAll("%goal%", this.reward);
    }

}
