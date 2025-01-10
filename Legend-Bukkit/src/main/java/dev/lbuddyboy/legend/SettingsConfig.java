package dev.lbuddyboy.legend;

import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.menu.button.ButtonModel;
import dev.lbuddyboy.commons.menu.settings.GUISettings;
import dev.lbuddyboy.commons.menu.settings.PagedGUISettings;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.commons.util.LocationUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.*;

@AllArgsConstructor
public enum SettingsConfig {

    SETTINGS_SERVER_NAME("server.name", "Legend"),
    SETTINGS_PRIMARY_COLOR("server.primary-color", "&6"),
    SETTINGS_SECONDARY_COLOR("server.secondary-color", "&e"),
    SETTINGS_TEAM_SIZE("server.team-size", 5),
    SETTINGS_ALLIES("server.allies", 0),
    SETTINGS_STARTING_BALANCE("server.starting-balance", 250.0D),
    SETTINGS_ALLY_DAMAGE("server.ally-damage", false),
    SETTINGS_DEATHBANS("server.deathbans", true),
    SETTINGS_TAKE_MONEY_ON_DEATH("server.take-money-on-death", true),
    SETTINGS_FAST_BREW("server.fast-brew", true),
    SETTINGS_CHAT_GAMES("server.chat-games", true),
    SETTINGS_FAST_SMELT("server.fast-smelt", true),
    SETTINGS_DOORS("server.door-y-level", 50),
    SETTINGS_DEV_MODE("server.dev-mode", false),
    SETTINGS_UHC_MODE("server.uhc-mode", false),
    SETTINGS_LEGACY_KILL_MESSAGE("server.legacy-kill-message", false),
    SETTINGS_DISABLE_SWING_SOUND("server.disable-swing-sound", false),
    SETTINGS_DISABLE_END_PORTAL_CREATE_SOUND("server.disable-end-portal-create-sound", false),
    SETTINGS_DISABLE_SPRINT_FIX("server.disable-sprint-fix", false),
    SETTINGS_DISABLE_SWING_DELAY("server.disable-swing-delay", false),
    SETTINGS_DISABLE_MACES("server.disable-maces", true),
    SETTINGS_DISABLE_SHIELDS("server.disable-shields", true),
    SETTINGS_DISABLE_TEAM_BREW("server.disable-team-brew", false),
    SETTINGS_DISABLE_TEAM_GENERATOR("server.disable-team-generator", false),
    SETTINGS_DISABLE_MENU_SHOP("server.disable-menu-shop", true),
    SETTINGS_DISABLE_CHARGED_PROJECTILES("server.disable-charged-projectiles", true),
    SETTINGS_DISABLE_END_CRYSTALS("server.disable-end-crystals", true),
    SETTINGS_DISALLOWED_TEAMS("server.disallowed-teams", Arrays.asList(
            "glowstone",
            "spawn",
            "loothill",
            "deathban",
            "nigger",
            "nigga",
            "faggot",
            "bean",
            "beaner",
            "gook",
            "chink"
    )),
    SAND_BIOME("server.sandbiome", "world;0.0;58.0;0.0;0.0;0.0;"),
    TUTORIAL("server.tutorial", "world;0.0;58.0;0.0;0.0;0.0;"),
    DISALLOWED_COMBAT_COMMANDS("server.disallowed-combat-commands", Arrays.asList(
            "/ec",
            "/enderchest",
            "/enderchest",
            "/pv",
            "/claim"
    )),

    MODIFIERS_BOW_DAMAGE("modifiers.bow-damage", 0.5D),
    MODIFIERS_CRYSTALS("modifiers.crystals", 2.0D),
    MODIFIERS_TRIDENT_DAMAGE("modifiers.trident-damage", 2.0D),

    POINTS_KILL("points.kill", 5),
    POINTS_DEATH("points.death", -5),
    POINTS_KOTH("points.koth", 50),
    POINTS_CITADEL("points.citadel", 150),
    POINTS_DTC("points.dtc", 75),
    POINTS_MADE_RADIABLE("points.made-raidable", 25),
    POINTS_WENT_RAIDABLE("points.went-raidable", -25),

    KITMAP_ENABLED("kitmap.enabled", false),
    KITMAP_KILL_REWARDS_ENABLED("kitmap.kill-reward.enabled", true),
    KITMAP_KILL_STREAKS_ENABLED("kitmap.kill-streaks.enabled", true),

    SHOP_CUSTOM_ITEMS("shop.custom-items", new ArrayList<>()),

    END_ENTRANCE("end.entrance", "world_the_end;0.0;58.0;0.0;0.0;0.0;"),
    END_OVERWORLD_EXIT("end.exit", "world;0.0;58.0;0.0;0.0;0.0;"),
    END_EXIT_END("end.exit-end", "world_the_end;5.0;58.0;5.0;0.0;0.0;"),
    END_CREEPERS("end.creepers", Arrays.asList(
            "world_the_end;0.0;58.0;0.0;0.0;0.0;"
    )),
    END_PLAYERS_CAN_CLAIM("end.players-can-claim", false),

    NETHER_PORTALS_SPAWN_ON_ROAD("nether.portals-spawn-on-road", false),
    NETHER_PLAYERS_CAN_CLAIM("nether.players-can-claim", false),

    WILDERNESS_OVERWORLD("wilderness.overworld", 1000),
    WILDERNESS_NETHER("wilderness.nether", 0),
    WILDERNESS_END("wilderness.end", 0),

    BUFFER_OVERWORLD("buffer.overworld", 300),
    BUFFER_NETHER("buffer.nether", 150),
    BUFFER_END("buffer.end", 250),

    TEAM_MAX_DTR("team.maximum-dtr", 6.0),
    TEAM_GENERATORS_INITIAL_EXPERIENCE_LEVEL_UP("team.generator.initial-experience-level-up", 250.0D),
    TEAM_GENERATORS_EXPERIENCE_LEVEL_UP_FACTOR("team.generator.experience-level-up-factor", 1.35D),
    TEAM_GENERATORS_EXPERIENCE_PER_GENERATION("team.generator.experience-per-generation", 0.10),
    TEAM_GENERATORS_MINIMUM_ITEMS("team.generator.minimum-items", 1),
    TEAM_GENERATORS_MAXIMUM_ITEMS("team.generator.maximum-items", 2),
    TEAM_GENERATORS_MAX_TIER("team.generator.max-tier", 3),
    TEAM_REGEN_COOLDOWN("team.regeneration.cooldown", "30m"),
    TEAM_DTR_LOSS("team.regeneration.loss", 1.0D),
    TEAM_REGEN_SPEED("team.regeneration.speed", 60),
    TEAM_REGEN_DTR("team.regeneration.dtr", 0.1D),
    TEAM_CLAIM_BUFFER_SIZE("team.claim.buffer-size", 2),
    TEAM_MAXIMUM_CLAIMS("team.claim.maximum-claims", 2),
    TEAM_PRICE_PER_BLOCK("team.claim.price-per-block", 0.15D),
    TEAM_MINIMUM_SIZE_X("team.claim.minimum-size.x", 5),
    TEAM_MINIMUM_SIZE_Z("team.claim.minimum-size.z", 5),
    TEAM_MAXIMUM_SIZE_X("team.claim.maximum-size.x", 50),
    TEAM_MAXIMUM_SIZE_Z("team.claim.maximum-size.z", 50),
    TEAM_WAND("team.claim.wand", new ButtonModel(
            -1,
            "BRUSH",
            "<blend:&3;&b>Claim Wand</>",
            Arrays.asList(
                    "",
                    "<blend:&3;&b;false>Left Click a Block</>",
                    "&f- &bSelects claim corner #1",
                    "",
                    "<blend:&3;&b;false>Right Click a Block</>",
                    "&f- &dSelects claim corner #2",
                    " ",
                    "<blend:&3;&b;false>Right Click Air</>",
                    "&f- &cCancels current claim process",
                    "",
                    "<blend:&3;&b;false>Crouch + Left Click</>",
                    "&f- &aConfirms the claim purchase",
                    ""
            ),
            List.of(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES),
            new HashMap<>() {{
                put(Enchantment.UNBREAKING, 10);
            }}
    )),

    ;

    final String path;
    final Object value;

    public String getString() {
        if (LegendBukkit.getInstance().getSettings().contains(this.path))
            return LegendBukkit.getInstance().getSettings().getString(this.path);

        loadDefault();

        return String.valueOf(this.value);
    }

    public boolean getBoolean() {
        if (LegendBukkit.getInstance().getSettings().contains(this.path))
            return LegendBukkit.getInstance().getSettings().getBoolean(this.path);

        loadDefault();

        return Boolean.parseBoolean(String.valueOf(this.value));
    }

    public long getLong() {
        if (LegendBukkit.getInstance().getSettings().contains(this.path))
            return LegendBukkit.getInstance().getSettings().getLong(this.path);

        loadDefault();

        return Long.parseLong(String.valueOf(this.value));
    }

    public Material getMaterial() {
        return Material.getMaterial(getString());
    }

    public int getInt() {
        if (LegendBukkit.getInstance().getSettings().contains(this.path))
            return LegendBukkit.getInstance().getSettings().getInt(this.path);

        loadDefault();

        return Integer.parseInt(String.valueOf(this.value));
    }

    public double getDouble() {
        if (LegendBukkit.getInstance().getSettings().contains(this.path))
            return LegendBukkit.getInstance().getSettings().getDouble(this.path);

        loadDefault();

        return Double.parseDouble(String.valueOf(this.value));
    }

    public Location getLocation() {
        return LocationUtils.deserializeString(getString());
    }

    public List<String> getStringList() {
        if (LegendBukkit.getInstance().getSettings().contains(this.path))
            return LegendBukkit.getInstance().getSettings().getStringList(this.path);

        loadDefault();

        return (List<String>) this.value;
    }

    public TimeDuration getTimeDuration() {
        return new TimeDuration(getString());
    }

    public void update(Object value) {
        LegendBukkit.getInstance().getSettings().set(this.path, value);
        LegendBukkit.getInstance().getSettings().save();
    }

    public void loadDefault() {
        if (LegendBukkit.getInstance().getSettings().contains(this.path)) return;

        if (this.value instanceof ButtonModel model) {
            model.save(this.path, LegendBukkit.getInstance().getSettings());
        } else if (this.value instanceof GUISettings settings) {
            settings.save(LegendBukkit.getInstance().getSettings());
        } else if (this.value instanceof PagedGUISettings settings) {
            settings.save(LegendBukkit.getInstance().getSettings());
        } else {
            LegendBukkit.getInstance().getSettings().set(this.path, this.value);
        }

        LegendBukkit.getInstance().getSettings().set(this.path, this.value);
        LegendBukkit.getInstance().getSettings().save();
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
