package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.LocationUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

@CommandAlias("desert|desertbiome|sandbiome|sand")
public class DesertCommand extends BaseCommand {

    @Default
    public static void def(Player sender) {
        sender.sendMessage(CC.translate("&e&lSand Biome&7: &f" + LocationUtils.toString(SettingsConfig.SAND_BIOME.getLocation())));
    }

    @Subcommand("set")
    @CommandPermission("legend.command.desert")
    public void set(Player sender) {
        SettingsConfig.SAND_BIOME.update(LocationUtils.serializeString(sender.getLocation()));
        sender.sendMessage(CC.translate("&aUpdated the sand biome location."));
    }

}
