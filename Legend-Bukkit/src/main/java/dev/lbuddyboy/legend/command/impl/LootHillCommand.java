package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.annotation.HelpCommand;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.util.CommandUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("loothill|lhill")
public class LootHillCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        if (!LegendBukkit.getInstance().getLootHillHandler().isSetup()) {
            return;
        }

        LegendBukkit.getInstance().getTeamHandler().sendTeamInfo(sender, LegendBukkit.getInstance().getLootHillHandler().getTeam());
    }

    @HelpCommand
    @Subcommand("help")
    public static void defp(CommandSender sender, @Name("help") @Optional CommandHelp help) {
        CommandUtil.generateCommandHelp(help);
    }

    @Subcommand("scan")
    @CommandPermission("legend.command.loothill")
    public void scan(CommandSender sender) {
        LegendBukkit.getInstance().getLootHillHandler().scanLocations(sender);
    }

    @Subcommand("reset")
    @CommandPermission("legend.command.loothill")
    public void reset(CommandSender sender) {
        LegendBukkit.getInstance().getLootHillHandler().respawn();
    }

}
