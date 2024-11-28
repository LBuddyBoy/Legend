package dev.lbuddyboy.legend.tab;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.legend.LegendBukkit;
import io.github.nosequel.tab.shared.TabHandler;
import io.github.nosequel.tab.shared.entry.TabEntry;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project LBuddyBoy Development
 * @file dev.lbuddyboy.hub.tab
 * @since 2/8/2024
 */

@Getter
public class LegendTabHandler implements IModule {

    private final Map<Integer, TabEntry> defaultElements, teamElements;
    private Config defaultConfig, teamConfig;
    private List<String> header, footer;
    private File tabDirectory;

    public LegendTabHandler() {
        this.defaultElements = new HashMap<>();
        this.teamElements = new HashMap<>();
    }

    @Override
    public void load() {
        reload();

        new TabHandler(new LegendTabProvider(), LegendBukkit.getInstance(), 20L);
    }

    @Override
    public void unload() {

    }

    @Override
    public void reload() {
        this.tabDirectory = new File(LegendBukkit.getInstance().getDataFolder(), "tab");
        if (!this.tabDirectory.exists()) this.tabDirectory.mkdir();

        this.defaultElements.clear();
        this.defaultConfig = new Config(LegendBukkit.getInstance(), "default", this.tabDirectory);
        this.teamConfig = new Config(LegendBukkit.getInstance(), "team", this.tabDirectory);

        if (!this.defaultConfig.getBoolean("enabled")) {
            return;
        }

        this.header = this.defaultConfig.getStringList("header");
        this.footer = this.defaultConfig.getStringList("footer");

        createElements(this.defaultElements, TabColumn.LEFT, "left", defaultConfig);
        createElements(this.defaultElements, TabColumn.MIDDLE, "middle", defaultConfig);
        createElements(this.defaultElements, TabColumn.RIGHT, "right", defaultConfig);
        createElements(this.defaultElements, TabColumn.EXTRA, "far-right", defaultConfig);

        createElements(this.teamElements, TabColumn.LEFT, "left", teamConfig);
        createElements(this.teamElements, TabColumn.MIDDLE, "middle", teamConfig);
        createElements(this.teamElements, TabColumn.RIGHT, "right", teamConfig);
        createElements(this.teamElements, TabColumn.EXTRA, "far-right", teamConfig);
    }

    private void createElements(Map<Integer, TabEntry> entries, TabColumn column, String sectionKey, Config config) {
        ConfigurationSection section = config.getConfigurationSection(sectionKey);
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            String text = section.getString(key + ".text");
            int slot = section.getInt(key + ".slot");
            int ping = section.getInt(key + ".ping");
            String skinTexture = section.getString(key + ".skin.texture");
            String skinSignature = section.getString(key + ".skin.signature");
            boolean skin = skinSignature != null && skinTexture != null && (!skinTexture.isEmpty() && !skinSignature.isEmpty());
            TabEntry entry = new TabEntry(column == TabColumn.LEFT ? 0 : column.getStart() / 20, slot - 1, text, ping);

            if (skin) entry.setSkinData(new String[]{skinTexture, skinSignature});

            entries.put(column.getStart() + slot, entry);
        }
    }

}
