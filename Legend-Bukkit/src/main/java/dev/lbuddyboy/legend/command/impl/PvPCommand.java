package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.annotation.HelpCommand;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.timer.impl.InvincibilityTimer;
import dev.lbuddyboy.legend.util.CommandUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("pvp")
public class PvPCommand extends BaseCommand {

    @Default
    @HelpCommand
    @Subcommand("help")
    public static void defp(CommandSender sender, @Name("help") @Optional CommandHelp help) {
        CommandUtil.generateCommandHelp(help);
    }

    @Subcommand("enable")
    public void enable(Player sender) {
        InvincibilityTimer timer = LegendBukkit.getInstance().getTimerHandler().getTimer(InvincibilityTimer.class);

        if (!timer.isActive(sender.getUniqueId())) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("invincibility.error.inactive")));
            return;
        }

        timer.remove(sender.getUniqueId());
        sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("invincibility.enabled")));
    }

}
