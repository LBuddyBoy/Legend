package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.arrow.packet.GlobalMessagePacket;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.commons.util.LocationUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.model.LegendUser;
import dev.lbuddyboy.legend.util.CommandUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("deathban|db")
public class DeathbanCommand extends BaseCommand {

    @Default
    @HelpCommand
    @Subcommand("help")
    public static void defp(CommandSender sender, @Name("help") @Optional CommandHelp help) {
        CommandUtil.generateCommandHelp(help);
    }

    @Subcommand("applykit")
    public void applyKit(Player sender) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(sender.getUniqueId());

        if (!user.isDeathBanned()) {
            return;
        }

        sender.getInventory().setArmorContents(LegendBukkit.getInstance().getDeathbanHandler().getKitArmor());
        sender.getInventory().setContents(LegendBukkit.getInstance().getDeathbanHandler().getKitInventory());
    }

    @Subcommand("tryrevive")
    public void tryRevive(Player sender) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(sender.getUniqueId());

        if (!user.isDeathBanned()) {
            return;
        }

        if (user.getLives() <= 0) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("deathban.lives.no-lives")));
            return;
        }

        LegendBukkit.getInstance().getDeathbanHandler().handleRevive(sender);
        sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("deathban.revived.self")));
    }

    @Subcommand("revive")
    public void revive(CommandSender sender, @Name("target") OfflinePlayer player) {
        String name = sender instanceof Player ? ((Player)sender).getName() : "&4&lCONSOLE";
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

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

    @Subcommand("safezone clear")
    @CommandPermission("legend.command.deathban.safezone")
    public void safeZoneClear(Player sender) {
        LegendBukkit.getInstance().getDeathbanHandler().getConfig().set("safezone", null);
        LegendBukkit.getInstance().getDeathbanHandler().getConfig().save();
        sender.sendMessage(CC.translate("&a[Deathban Arena] Successfully setup point 'a' for the deathban arena."));
    }

    @Subcommand("kit setarmor")
    @CommandPermission("legend.command.deathban.kit")
    public void kitArmor(Player sender) {
        LegendBukkit.getInstance().getDeathbanHandler().getConfig().set("kit.armor", ItemUtils.itemStackArrayToBase64(sender.getInventory().getArmorContents()));
        LegendBukkit.getInstance().getDeathbanHandler().getConfig().save();
        sender.sendMessage(CC.translate("&a[Deathban Arena] Successfully set the kit armor contents."));
    }

    @Subcommand("kit setinventory")
    @CommandPermission("legend.command.deathban.kit")
    public void kitInventory(Player sender) {
        LegendBukkit.getInstance().getDeathbanHandler().getConfig().set("kit.inventory", ItemUtils.itemStackArrayToBase64(sender.getInventory().getContents()));
        LegendBukkit.getInstance().getDeathbanHandler().getConfig().save();
        sender.sendMessage(CC.translate("&a[Deathban Arena] Successfully set the kit inventory contents."));
    }

    @Subcommand("safezone seta")
    @CommandPermission("legend.command.deathban.safezone")
    public void safeZoneA(Player sender) {
        LegendBukkit.getInstance().getDeathbanHandler().getConfig().set("safezone.a", LocationUtils.serializeString(sender.getLocation()));
        LegendBukkit.getInstance().getDeathbanHandler().getConfig().save();
        sender.sendMessage(CC.translate("&a[Deathban Arena] Successfully setup point 'a' for the deathban arena."));
    }

    @Subcommand("safezone setb")
    @CommandPermission("legend.command.deathban.safezone")
    public void safeZoneB(Player sender) {
        LegendBukkit.getInstance().getDeathbanHandler().getConfig().set("safezone.b", LocationUtils.serializeString(sender.getLocation()));
        LegendBukkit.getInstance().getDeathbanHandler().getConfig().save();
        sender.sendMessage(CC.translate("&a[Deathban Arena] Successfully setup point 'b' for the deathban arena."));
    }

}
