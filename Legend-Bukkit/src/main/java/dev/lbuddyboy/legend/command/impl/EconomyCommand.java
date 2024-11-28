package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ShortPrice;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.model.LegendUser;
import dev.lbuddyboy.legend.util.CommandUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("economy|eco|bal|balance|money")
public class EconomyCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public void def(Player sender, @Name("player") @Optional OfflinePlayer player) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(sender.getUniqueId());

        if (player != null) {
            user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("economy.balance.other")
                    .replaceAll("%player%", player.getName())
                    .replaceAll("%balance%", APIConstants.formatNumber(user.getBalance()))
            ));
            return;
        }

        sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("economy.balance.self")
                .replaceAll("%balance%", APIConstants.formatNumber(user.getBalance()))
        ));
    }

    @HelpCommand
    @Subcommand("help")
    public static void defp(CommandSender sender, @Name("help") @Optional CommandHelp help) {
        CommandUtil.generateCommandHelp(help);
    }

    @Subcommand("set")
    @CommandPermission("legend.command.economy")
    @CommandCompletion("@players")
    public void set(CommandSender sender, @Name("player") OfflinePlayer player, @Name("amount") ShortPrice amount) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        user.setBalance(amount.convert());

        sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("economy.set.sender")
                .replaceAll("%target%", player.getName())
                .replaceAll("%amount%", amount.convertFancy())
        ));

        if (player.isOnline() && player.getPlayer() != null) {
            player.getPlayer().sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("economy.set.target")
                    .replaceAll("%sender%", (sender instanceof Player ? sender.getName() : "&4&lCONSOLE"))
                    .replaceAll("%amount%", amount.convertFancy())
            ));
        }
    }

    @Subcommand("add")
    @CommandPermission("legend.command.economy")
    @CommandCompletion("@players")
    public void add(CommandSender sender, @Name("player") OfflinePlayer player, @Name("amount") ShortPrice amount) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        user.setBalance(user.getBalance() + amount.convert());

        sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("economy.add.sender")
                .replaceAll("%target%", player.getName())
                .replaceAll("%amount%", amount.convertFancy())
        ));

        if (player.isOnline() && player.getPlayer() != null) {
            player.getPlayer().sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("economy.add.target")
                    .replaceAll("%sender%", (sender instanceof Player ? sender.getName() : "&4&lCONSOLE"))
                    .replaceAll("%amount%", amount.convertFancy())
            ));
        }
    }

    @Subcommand("subtract")
    @CommandPermission("legend.command.economy")
    @CommandCompletion("@players")
    public void subtract(CommandSender sender, @Name("player") OfflinePlayer player, @Name("amount") ShortPrice amount) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        user.setBalance(user.getBalance() - amount.convert());

        sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("economy.subtract.sender")
                .replaceAll("%target%", player.getName())
                .replaceAll("%amount%", amount.convertFancy())
        ));

        if (player.isOnline() && player.getPlayer() != null) {
            player.getPlayer().sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("economy.subtract.target")
                    .replaceAll("%sender%", (sender instanceof Player ? sender.getName() : "&4&lCONSOLE"))
                    .replaceAll("%amount%", amount.convertFancy())
            ));
        }
    }

}
