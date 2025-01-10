package dev.lbuddyboy.legend.features.kitmap;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.commons.util.LocationUtils;
import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.features.kitmap.kit.Kit;
import dev.lbuddyboy.legend.features.kitmap.listener.*;
import dev.lbuddyboy.legend.features.kitmap.model.ResetBlock;
import dev.lbuddyboy.legend.features.kitmap.streak.KillStreak;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class KitMapHandler implements IModule {

    private final Map<String, Kit> kits;
    private final List<KillStreak> killStreaks;
    private File kitsDirectory;
    private List<ResetBlock> resetBlocks;

    public KitMapHandler() {
        this.kits = new HashMap<>();
        this.killStreaks = new ArrayList<>();
        this.resetBlocks = new ArrayList<>();
    }

    @Override
    public void load() {
        this.kitsDirectory = new File(LegendBukkit.getInstance().getDataFolder(), "kits");
        if (!this.kitsDirectory.exists()) this.kitsDirectory.mkdir();

        for (String nameYML : this.kitsDirectory.list()) {
            Config config = new Config(LegendBukkit.getInstance(), nameYML.replaceAll(".yml", ""), this.kitsDirectory);
            Kit kit = new Kit(config);

            this.kits.put(kit.getName().toLowerCase(), kit);
        }

        if (SettingsConfig.KITMAP_KILL_STREAKS_ENABLED.getBoolean()) {
            ConfigurationSection section = LegendBukkit.getInstance().getSettings().getConfigurationSection("kitmap.kill-streaks.goals");

            for (String key : section.getKeys(false)) {
                int neededKills = Integer.parseInt(key);
                String name = section.getString(key + ".name");
                List<String> commands = section.getStringList(key + ".commands");

                this.killStreaks.add(new KillStreak(neededKills, name, commands));
            }

            LegendBukkit.getInstance().getLogger().info("Loaded " + this.killStreaks.size() + " kill streaks!");
        }

        Bukkit.getPluginManager().registerEvents(new KitSignListener(), LegendBukkit.getInstance());
        Bukkit.getPluginManager().registerEvents(new KillStreakListener(), LegendBukkit.getInstance());
        Bukkit.getPluginManager().registerEvents(new BlockBoostListener(), LegendBukkit.getInstance());
        Bukkit.getPluginManager().registerEvents(new KitmapGeneralListener(), LegendBukkit.getInstance());
        Bukkit.getPluginManager().registerEvents(new TrashSignListener(), LegendBukkit.getInstance());

        Tasks.runTimer(this::checkResetBlocks, 20, 20);
    }

    @Override
    public void unload() {
        this.resetBlocks.forEach(ResetBlock::reset);
        this.resetBlocks.clear();
    }

    public void checkResetBlocks() {
        this.resetBlocks.removeIf(ResetBlock::shouldReset);
    }

    public void cacheResetBlock(Location location, BlockData data, long duration) {
        ResetBlock resetBlock = new ResetBlock(
                location.clone(),
                data.clone(),
                System.currentTimeMillis(),
                duration
        );

        this.resetBlocks.add(resetBlock);
        LegendBukkit.getInstance().getLogger().info("[Reset Block] New reset block " + LocationUtils.toString(location) + " for " + TimeUtils.formatIntoDetailedString(duration) + " [" + data.getAsString() + "]");
    }

    public boolean isResetBlock(Location location) {
        return this.resetBlocks.stream().anyMatch(b -> b.getLocation().equals(location));
    }

    public void removeResetBlock(Location location) {
        this.resetBlocks.removeIf(b -> b.getLocation().equals(location));
    }

}
