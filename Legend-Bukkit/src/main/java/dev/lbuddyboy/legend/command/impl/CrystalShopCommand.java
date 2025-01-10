package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.crystals.menu.CrystalShopMenu;
import dev.lbuddyboy.legend.features.crystals.model.CrystalShopItem;
import dev.lbuddyboy.legend.features.crystals.model.CrystalShopLootTable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("crystalshop")
public class CrystalShopCommand extends BaseCommand {

    @Default
    public void crystalshop(Player sender) {
        new CrystalShopMenu().openMenu(sender);
    }

    @Subcommand("rotate")
    @CommandPermission("loottables.admin")
    public void rotate(CommandSender sender) {
        LegendBukkit.getInstance().getCrystalHandler().rotate();
    }

    @Subcommand("setprice")
    @CommandCompletion("@crystalShopItems")
    @CommandPermission("loottables.admin")
    public void chance(Player sender, @Name("item") CrystalShopItem item, @Name("price") double chance) {
        CrystalShopLootTable lootTable = LegendBukkit.getInstance().getCrystalHandler().getLootTable();

        item.setPrice(chance);
        lootTable.save();

        sender.sendMessage(CC.translate("&aThe " + item.getDisplayName() + "&a price has been set to " + chance + "."));
    }

    @Subcommand("setslot")
    @CommandCompletion("@crystalShopItems")
    @CommandPermission("loottables.admin")
    public void setpriority(Player sender, @Name("item") CrystalShopItem item, @Name("slot") int slot) {
        CrystalShopLootTable lootTable = LegendBukkit.getInstance().getCrystalHandler().getLootTable();

        item.setSlot(slot);
        lootTable.save();

        sender.sendMessage(CC.translate("&aThe " + item.getDisplayName() + "&a slot has been set to " + slot + "."));
    }

    @Subcommand("setrotates")
    @CommandCompletion("@crystalShopItems")
    @CommandPermission("loottables.admin")
    public void setrotates(Player sender, @Name("item") CrystalShopItem item, @Name("rotates") boolean toggle) {
        CrystalShopLootTable lootTable = LegendBukkit.getInstance().getCrystalHandler().getLootTable();

        item.setRotation(toggle);
        lootTable.save();

        sender.sendMessage(CC.translate("&aThe " + item.getDisplayName() + "&a rotation status has been set to " + (toggle ? "true" : "false") + "."));
    }

}
