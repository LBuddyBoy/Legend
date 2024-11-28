package dev.lbuddyboy.legend.tab;

import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.tab.model.TabColumn;
import dev.lbuddyboy.commons.tab.model.TabElement;
import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.commons.util.skin.Skin;
import dev.lbuddyboy.legend.LegendBukkit;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
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

    private final List<TabElement> defaultElements, teamElements;
    private Config defaultConfig, teamConfig;
    private List<String> header, footer;
    private File tabDirectory;

    public LegendTabHandler() {
        this.defaultElements = new ArrayList<>();
        this.teamElements = new ArrayList<>();
    }

    @Override
    public void load() {
        reload();


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

        CommonsPlugin.getInstance().getTabHandler().registerProvider(new LegendTabProvider());
    }

    private void createElements(List<TabElement> entries, TabColumn column, String sectionKey, Config config) {
        ConfigurationSection section = config.getConfigurationSection(sectionKey);
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            String text = section.getString(key + ".text");
            int slot = section.getInt(key + ".slot");
            int ping = section.getInt(key + ".ping");
            String skinTexture = section.getString(key + ".skin.texture");
            String skinSignature = section.getString(key + ".skin.signature");
            boolean skin = skinSignature != null && skinTexture != null && (!skinTexture.isEmpty() && !skinSignature.isEmpty());

            entries.add(new TabElement(text, slot, column, ping, skin ? new Skin(skinSignature, skinTexture) : Skin.DEFAULT_SKIN));
        }
    }

}
