package dev.lbuddyboy.legend;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.util.PlaceholderUtil;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public enum LangConfig {

    HELP_ITEMS_AMOUNT_PER_PAGE("help.amount-per-page", 6),

    HELP_FORMAT("help.format", Arrays.asList(
            " ",
            "<blend:&6;&e>&lLegend Help</> &7[Page %page%/%max-pages%]",
            "%items%",
            " ",
            "<blend:&6;&e>&lServer Information</>",
            "&7» &fStore: &astore.example.net",
            "&7» &fDiscord: &9discord.example.net",
            "&7» &fServer IP: &eplay.example.net",
            " ",
            "&7/help <page> for more help.",
            " "
    )),
    HELP_ITEMS("help.items", Arrays.asList(
            "&7» &f/lives - check your lives",
            "&7» &f/pay - pay a player money",
            "&7» &f/bal - check your balance",
            "&7» &f/logout - logout safely without a logger",
            "&7» &f/pvp enable - enable your pvp timer",
            "&7» &f/koth - enable your pvp timer"
    )),

    DOORS_DISABLED("doors_disabled", "<blend:&4;&c>Doors cannot be placed past y level 70.</>"),

    STAT_TRACKER_HEADER("stat-tracker.header", "<blend:&4;&c>&lITEM STATS</>"),
    DEATH_MESSAGE_PREFIX("death_messages.prefix", "<blend:&4;&c>&lDEATHS</> &7» "),

    ORE_MOUNTAIN_RESETS_SOON("ore-mountain.resets-soon", "<blend:&2;&a>&lORE MOUNTAIN</> &7» &aOre Mountain will be resetting in &2%time%&e!"),
    ORE_MOUNTAIN_RESET("ore-mountain.reset-announcement", "<blend:&2;&a>&lORE MOUNTAIN</> &7» &aOre Mountain has been reset, all ores have been respawned&e!"),

    LIVES_ERROR_NOT_DEATHBANNED("lives.error.not_deathbanned", "<blend:&4;&c>[Deathban Error] That player is not deathbanned.</>"),

    TEAM_ALREADY_ON_TEAM("team.already_on_team", "<blend:&4;&c>[Team Error] You are already on a team.</>"),
    TEAM_BREW_NO_MATERIALS_TEAM("team.brew.no_materials_team", "<blend:&4;&c>[Team Brew] Your team doesn't have any of that material to withdraw.</>"),
    TEAM_BREW_NO_MATERIALS_PLAYER("team.brew.no_materials_player", "<blend:&4;&c>[Team Brew] You don't have any of that material to deposit.</>"),
    TEAM_BREW_ALREADY_BREWING("team.brew.already_brewing", "<blend:&4;&c>[Team Brew] Your team is already brewing that potion.</>"),
    TEAM_BREW_NOT_BREWING("team.brew.not_brewing", "<blend:&4;&c>[Team Brew] Your team is not brewing that potion.</>"),
    TEAM_BREW_DEPOSITED("team.brew.deposited", "<blend:&2;&a>[Team Brew] %player% deposited x%amount% %material% into the team brew.</>"),
    TEAM_BREW_WITHDRAW("team.brew.withdraw", "<blend:&2;&a>[Team Brew] %player% withdrew x%amount% %material% from the team brew.</>"),

    LEADERBOARDS_UPDATED("leaderboards-updated", "<blend:&6;&e>LEADERBOARDS</> &7» &fLeaderboards have been updated! Next update in 5 minutes.");

    final String path;
    final Object value;

    public String getString() {
        if (LegendBukkit.getInstance().getLanguage().contains(this.path))
            return LegendBukkit.getInstance().getLanguage().getString(this.path);

        loadDefault();

        return String.valueOf(this.value);
    }

    public boolean getBoolean() {
        if (LegendBukkit.getInstance().getLanguage().contains(this.path))
            return LegendBukkit.getInstance().getLanguage().getBoolean(this.path);

        loadDefault();

        return Boolean.parseBoolean(String.valueOf(this.value));
    }

    public long getLong() {
        if (LegendBukkit.getInstance().getLanguage().contains(this.path))
            return LegendBukkit.getInstance().getLanguage().getLong(this.path);

        loadDefault();

        return Long.parseLong(String.valueOf(this.value));
    }

    public Material getMaterial() {
        return Material.getMaterial(getString());
    }

    public int getInt() {
        if (LegendBukkit.getInstance().getLanguage().contains(this.path))
            return LegendBukkit.getInstance().getLanguage().getInt(this.path);

        loadDefault();

        return Integer.parseInt(String.valueOf(this.value));
    }

    public List<String> getStringList() {
        if (LegendBukkit.getInstance().getLanguage().contains(this.path))
            return LegendBukkit.getInstance().getLanguage().getStringList(this.path);

        loadDefault();

        return (List<String>) this.value;
    }

    public void update(Object value) {
        LegendBukkit.getInstance().getLanguage().set(this.path, value);
        LegendBukkit.getInstance().getLanguage().save();
    }

    public void loadDefault() {
        if (LegendBukkit.getInstance().getLanguage().contains(this.path)) return;

        LegendBukkit.getInstance().getLanguage().set(this.path, this.value);
        LegendBukkit.getInstance().getLanguage().save();
    }

    public void sendMessage(CommandSender player) {
        player.sendMessage(CC.translate(getString()));
    }

    public void sendMessage(Player player) {
        player.sendMessage(CC.translate(PlaceholderUtil.applyAllPlaceholders(getString(), player)));
    }

    public void sendMessage(Player player, Object... objects) {
        player.sendMessage(CC.translate(getString(), objects));
    }

    public void sendListMessage(Player player) {
        getStringList().forEach(s -> player.sendMessage(CC.translate(PlaceholderUtil.applyAllPlaceholders(s, player))));
    }

    public void sendListMessage(Player player, Object... objects) {
        getStringList().forEach(s -> player.sendMessage(CC.translate(s, objects)));
    }

}
