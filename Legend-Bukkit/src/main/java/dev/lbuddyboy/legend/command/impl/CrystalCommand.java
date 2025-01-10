package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.model.LegendUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("crystal|crystals")
public class CrystalCommand extends BaseCommand {

    @Default
    public void crystal(Player sender) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(sender.getUniqueId());

        sender.sendMessage(CC.translate("&aYou have &b" + APIConstants.formatNumber(user.getCrystals()) + " crystals&a!"));
    }

    @Subcommand("set")
    @CommandCompletion("@players")
    @CommandPermission("legend.command.crystal")
    public void set(CommandSender sender, @Name("uuid") UUID target, @Name("amount") int amount) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(target);

        user.setCrystals(amount);
        sender.sendMessage(CC.translate("&aSet " + user.getName() + "'s crystals to " + amount + "."));
    }

    @Subcommand("add")
    @CommandCompletion("@players")
    @CommandPermission("legend.command.crystal")
    public void add(CommandSender sender, @Name("uuid") UUID target, @Name("amount") int amount) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(target);

        user.setCrystals(user.getCrystals() + amount);
        sender.sendMessage(CC.translate("&aAdded " + amount + "'s crystals to " + user.getName() + "'s account."));
    }

}
