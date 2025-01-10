package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.annotation.HelpCommand;
import dev.lbuddyboy.arrow.packet.GlobalMessagePacket;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ShortPrice;
import dev.lbuddyboy.legend.LangConfig;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.model.LegendUser;
import dev.lbuddyboy.legend.util.CommandUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("lives")
public class LivesCommand extends BaseCommand {

    @Default
    public void def(Player sender, @Name("player") @Optional OfflinePlayer target) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(sender.getUniqueId());

        if (target != null) {
            user = LegendBukkit.getInstance().getUserHandler().getUser(target.getUniqueId());

            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("lives.amount.other")
                    .replaceAll("%player%", target.getName())
                    .replaceAll("%lives%", APIConstants.formatNumber(user.getLives()))
            ));
            return;
        }

        sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("lives.amount.self")
                .replaceAll("%lives%", APIConstants.formatNumber(user.getLives()))
        ));
    }

    @HelpCommand
    @Subcommand("help")
    public static void defp(CommandSender sender, @Name("help") @Optional CommandHelp help) {
        CommandUtil.generateCommandHelp(help);
    }

    @Subcommand("send|give")
    @CommandCompletion("@players")
    public void send(Player sender, @Name("player") OfflinePlayer target, @Name("amount") int amount) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(sender.getUniqueId());
        LegendUser targetUser = LegendBukkit.getInstance().getUserHandler().getUser(target.getUniqueId());

        if (user.getLives() < amount) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("lives.error.insufficient")
                    .replaceAll("%target%", target.getName())
            ));
            return;
        }

        targetUser.setLives(targetUser.getLives() + amount);
        user.setLives(user.getLives() - amount);

        sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("lives.add.sender")
                .replaceAll("%target%", target.getName())
                .replaceAll("%amount%", APIConstants.formatNumber(amount))
        ));

        if (target.isOnline() && target.getPlayer() != null) {
            target.getPlayer().sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("lives.add.target")
                    .replaceAll("%sender%", sender.getName())
                    .replaceAll("%amount%", APIConstants.formatNumber(amount))
            ));
        }
    }

    @Subcommand("revive")
    @CommandCompletion("@players")
    public static void revive(CommandSender sender, @Name("target") OfflinePlayer player) {
        String name = sender instanceof Player ? sender.getName() : "&4&lCONSOLE";
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (!user.isDeathBanned()) {
            LangConfig.LIVES_ERROR_NOT_DEATHBANNED.sendMessage(sender);
            return;
        }

        if (sender instanceof Player senderPlayer) {
            LegendUser senderUser = LegendBukkit.getInstance().getUserHandler().getUser(senderPlayer.getUniqueId());

            if (senderUser.getLives() <= 0 && !sender.hasPermission("legend.command.revive")) {
                sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("lives.error.insufficient")
                        .replaceAll("%target%", player.getName())
                ));
                return;
            }

            senderUser.setLives(senderUser.getLives() - 1);
            senderUser.save(true);
        }

        user.removeTimer("deathban");
        user.save(true);

        new GlobalMessagePacket(player.getUniqueId(), CC.translate(LegendBukkit.getInstance().getLanguage().getString("deathban.revived.other")
                .replaceAll("%sender%", name)
        )).send();

        if (player.isOnline() && player.getPlayer() != null) {
            LegendBukkit.getInstance().getDeathbanHandler().handleRevive(player.getPlayer());
        }

        sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("deathban.revived.sender")
                .replaceAll("%target%", player.getName())
        ));
    }

    @Subcommand("admin set")
    @CommandPermission("legend.command.lives.admin")
    @CommandCompletion("@players")
    public void set(CommandSender sender, @Name("player") OfflinePlayer player, @Name("amount") int amount) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        user.setLives(amount);

        sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("lives.set.sender")
                .replaceAll("%target%", player.getName())
                .replaceAll("%amount%", APIConstants.formatNumber(amount))
        ));

        if (player.isOnline() && player.getPlayer() != null) {
            player.getPlayer().sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("lives.set.target")
                    .replaceAll("%sender%", (sender instanceof Player ? sender.getName() : "&4&lCONSOLE"))
                    .replaceAll("%amount%", APIConstants.formatNumber(amount))
            ));
        }
    }

    @Subcommand("admin add")
    @CommandPermission("legend.command.lives.admin")
    @CommandCompletion("@players")
    public void add(CommandSender sender, @Name("player") OfflinePlayer player, @Name("amount") int amount) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        user.setLives(user.getLives() + amount);

        sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("lives.add.sender")
                .replaceAll("%target%", player.getName())
                .replaceAll("%amount%", APIConstants.formatNumber(amount))
        ));

        if (player.isOnline() && player.getPlayer() != null) {
            player.getPlayer().sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("lives.add.target")
                    .replaceAll("%sender%", (sender instanceof Player ? sender.getName() : "&4&lCONSOLE"))
                    .replaceAll("%amount%", APIConstants.formatNumber(amount))
            ));
        }
    }

    @Subcommand("admin subtract")
    @CommandPermission("legend.command.lives.admin")
    @CommandCompletion("@players")
    public void subtract(CommandSender sender, @Name("player") OfflinePlayer player, @Name("amount") int amount) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        user.setLives(user.getLives() - amount);

        sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("lives.subtract.sender")
                .replaceAll("%target%", player.getName())
                .replaceAll("%amount%", APIConstants.formatNumber(amount))
        ));

        if (player.isOnline() && player.getPlayer() != null) {
            player.getPlayer().sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("lives.subtract.target")
                    .replaceAll("%sender%", (sender instanceof Player ? sender.getName() : "&4&lCONSOLE"))
                    .replaceAll("%amount%", APIConstants.formatNumber(amount))
            ));
        }
    }

}
