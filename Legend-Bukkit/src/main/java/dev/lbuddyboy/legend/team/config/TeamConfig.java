package dev.lbuddyboy.legend.team.config;

import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.menu.button.ButtonModel;
import dev.lbuddyboy.commons.menu.settings.GUISettings;
import dev.lbuddyboy.commons.menu.settings.PagedGUISettings;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.commons.util.LocationUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
public enum TeamConfig {
    
    TEAM_BREW_MENU_SETTINGS("team.brew-menu", new GUISettings(
            "team.brew-menu",
            true,
            new ItemFactory(Material.LIGHT_GRAY_STAINED_GLASS_PANE).displayName(" ").build(),
            " ",
            54,
            new ArrayList<>()
    )),

    TIERS_MENU_SETTINGS("tiers-menu", new GUISettings(
            "tiers-menu",
            true,
            new ItemFactory(Material.GRAY_STAINED_GLASS_PANE).displayName(" ").build(),
            "<blend:&6;&e>&lGENERATORS</>&7 » &fViewing Tiers",
            27,
            Arrays.asList(
                    "YELLOW_STAINED_GLASS_PANE;1; ;true;true",
                    "ORANGE_STAINED_GLASS_PANE;2; ;true;true",
                    "ORANGE_STAINED_GLASS_PANE;8; ;true;true",
                    "YELLOW_STAINED_GLASS_PANE;9; ;true;true",
                    "ORANGE_STAINED_GLASS_PANE;10; ;true;true",
                    "YELLOW_STAINED_GLASS_PANE;18; ;true;true",
                    "YELLOW_STAINED_GLASS_PANE;19; ;true;true",
                    "ORANGE_STAINED_GLASS_PANE;20; ;true;true",
                    "ORANGE_STAINED_GLASS_PANE;26; ;true;true",
                    "YELLOW_STAINED_GLASS_PANE;27; ;true;true"
            )
    )),

    TIER_BUTTON("tiers-menu.button", new ButtonModel(
            -1,
            "PLAYER_HEAD",
            "%tier_colored_name%",
            Arrays.asList(
                    "%tier_primary_color%│ &fRequired Tier&7: &a%tier_required_tier%",
                    " ",
                    "&7&o(( Click to view the loot for this tier ))"
            ),
            List.of(ItemFlag.HIDE_ENCHANTS),
            new HashMap<>() {{
                put(Enchantment.UNBREAKING, 10);
            }}
    ).base64("%tier_material%")),

    ;

    final String path;
    final Object value;

    public String getString() {
        if (LegendBukkit.getInstance().getTeamHandler().getConfig().contains(this.path))
            return LegendBukkit.getInstance().getTeamHandler().getConfig().getString(this.path);

        loadDefault();

        return String.valueOf(this.value);
    }

    public boolean getBoolean() {
        if (LegendBukkit.getInstance().getTeamHandler().getConfig().contains(this.path))
            return LegendBukkit.getInstance().getTeamHandler().getConfig().getBoolean(this.path);

        loadDefault();

        return Boolean.parseBoolean(String.valueOf(this.value));
    }

    public long getLong() {
        if (LegendBukkit.getInstance().getTeamHandler().getConfig().contains(this.path))
            return LegendBukkit.getInstance().getTeamHandler().getConfig().getLong(this.path);

        loadDefault();

        return Long.parseLong(String.valueOf(this.value));
    }

    public Material getMaterial() {
        return Material.getMaterial(getString());
    }

    public int getInt() {
        if (LegendBukkit.getInstance().getTeamHandler().getConfig().contains(this.path))
            return LegendBukkit.getInstance().getTeamHandler().getConfig().getInt(this.path);

        loadDefault();

        return Integer.parseInt(String.valueOf(this.value));
    }

    public double getDouble() {
        if (LegendBukkit.getInstance().getTeamHandler().getConfig().contains(this.path))
            return LegendBukkit.getInstance().getTeamHandler().getConfig().getDouble(this.path);

        loadDefault();

        return Double.parseDouble(String.valueOf(this.value));
    }

    public Location getLocation() {
        return LocationUtils.deserializeString(getString());
    }

    public List<String> getStringList() {
        if (LegendBukkit.getInstance().getTeamHandler().getConfig().contains(this.path))
            return LegendBukkit.getInstance().getTeamHandler().getConfig().getStringList(this.path);

        loadDefault();

        return (List<String>) this.value;
    }


    public GUISettings getMenuSettings() {
        if (LegendBukkit.getInstance().getTeamHandler().getConfig().contains(this.path)) {
            return new GUISettings(LegendBukkit.getInstance().getTeamHandler().getConfig(), this.path);
        }

        loadDefault();
        return (GUISettings) this.value;
    }

    public PagedGUISettings getPagedSettings() {
        if (LegendBukkit.getInstance().getTeamHandler().getConfig().contains(this.path)) {
            return new PagedGUISettings(LegendBukkit.getInstance().getTeamHandler().getConfig(), this.path);
        }

        loadDefault();
        return (PagedGUISettings) this.value;
    }

    public ButtonModel getButtonModel() {
        if (LegendBukkit.getInstance().getTeamHandler().getConfig().contains(this.path)) {
            return new ButtonModel(this.path, LegendBukkit.getInstance().getTeamHandler().getConfig());
        }

        loadDefault();
        return (ButtonModel) this.value;
    }

    public TimeDuration getTimeDuration() {
        return new TimeDuration(getString());
    }

    public void update(Object value) {
        LegendBukkit.getInstance().getTeamHandler().getConfig().set(this.path, value);
        LegendBukkit.getInstance().getTeamHandler().getConfig().save();
    }

    public void loadDefault() {
        if (LegendBukkit.getInstance().getTeamHandler().getConfig().contains(this.path)) return;

        if (this.value instanceof ButtonModel model) {
            model.save(this.path, LegendBukkit.getInstance().getTeamHandler().getConfig());
        } else if (this.value instanceof GUISettings settings) {
            settings.save(LegendBukkit.getInstance().getTeamHandler().getConfig());
        } else if (this.value instanceof PagedGUISettings settings) {
            settings.save(LegendBukkit.getInstance().getTeamHandler().getConfig());
        } else {
            LegendBukkit.getInstance().getTeamHandler().getConfig().set(this.path, this.value);
        }

        LegendBukkit.getInstance().getTeamHandler().getConfig().save();
    }

    public void sendMessage(Player player) {
        player.sendMessage(CC.translate(getString()));
    }

    public void sendMessage(Player player, Object... objects) {
        player.sendMessage(CC.translate(getString(), objects));
    }

    public void sendListMessage(Player player) {
        getStringList().forEach(s -> player.sendMessage(CC.translate(s)));
    }

    public void sendListMessage(Player player, Object... objects) {
        getStringList().forEach(s -> player.sendMessage(CC.translate(s, objects)));
    }

}
