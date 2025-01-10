package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.util.CommandUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("oremountain|omountain|ore")
public class OreMountainCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        if (!LegendBukkit.getInstance().getOreMountainHandler().isSetup()) {
            return;
        }

        LegendBukkit.getInstance().getTeamHandler().sendTeamInfo(sender, LegendBukkit.getInstance().getOreMountainHandler().getTeam());
    }

    @HelpCommand
    @Subcommand("help")
    public static void defp(CommandSender sender, @Name("help") @Optional CommandHelp help) {
        CommandUtil.generateCommandHelp(help);
    }

    @Subcommand("scan")
    @CommandPermission("legend.command.oremountain")
    public void scan(CommandSender sender) {
        LegendBukkit.getInstance().getOreMountainHandler().scanLocations(sender);
    }

    @Subcommand("reset")
    @CommandPermission("legend.command.oremountain")
    public void reset(CommandSender sender) {
        LegendBukkit.getInstance().getOreMountainHandler().respawn();
    }

}
