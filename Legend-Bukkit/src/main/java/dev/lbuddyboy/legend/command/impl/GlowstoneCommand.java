package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.annotation.HelpCommand;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.util.CommandUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("glowstonemountain|gmountain")
public class GlowstoneCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        if (!LegendBukkit.getInstance().getGlowstoneHandler().isSetup()) {
            return;
        }

        LegendBukkit.getInstance().getTeamHandler().sendTeamInfo(sender, LegendBukkit.getInstance().getGlowstoneHandler().getTeam());
    }

    @HelpCommand
    @Subcommand("help")
    public static void defp(CommandSender sender, @Name("help") @Optional CommandHelp help) {
        CommandUtil.generateCommandHelp(help);
    }

    @Subcommand("scan")
    @CommandPermission("legend.command.glowstonemountain")
    public void scan(CommandSender sender) {
        LegendBukkit.getInstance().getGlowstoneHandler().scanLocations(sender);
    }

    @Subcommand("reset")
    @CommandPermission("legend.command.glowstonemountain")
    public void reset(CommandSender sender) {
        LegendBukkit.getInstance().getGlowstoneHandler().respawn();
    }

}
