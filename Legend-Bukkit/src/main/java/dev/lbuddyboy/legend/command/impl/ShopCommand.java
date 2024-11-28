package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.shop.ShopHandler;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("shop")
public class ShopCommand extends BaseCommand {

    private final ShopHandler shopHandler = LegendBukkit.getInstance().getShopHandler();

    @Subcommand("admin createitem")
    @CommandPermission("legend.command.shop.admin")
    public void createItem(Player sender, @Name("name") String name) {
        if (this.shopHandler.getCustomItems().containsKey(name.toLowerCase())) {
            sender.sendMessage(CC.translate("&cThat custom item already exists."));
            return;
        }

        if (sender.getInventory().getItemInMainHand() == null || sender.getInventory().getItemInMainHand().getType() == Material.AIR) {
            sender.sendMessage(CC.translate("&cYou need to have an item in your hand."));
            return;
        }

        this.shopHandler.getCustomItems().put(name.toLowerCase(), sender.getInventory().getItemInMainHand().clone());
        this.shopHandler.saveCustomItems();
        sender.sendMessage(CC.translate("&aCreated a new custom item!"));
    }

    @Subcommand("admin listitems")
    @CommandPermission("legend.command.shop.admin")
    public void list(Player sender) {
        sender.sendMessage(CC.translate("&6&lList of Custom Items&7: &f" + StringUtils.join(this.shopHandler.getCustomItems().keySet(), ", ")));
    }

    @Subcommand("admin setitem")
    @CommandPermission("legend.command.shop.admin")
    @CommandCompletion("@customItems")
    public void setitem(Player sender, @Name("name") String name) {
        if (!this.shopHandler.getCustomItems().containsKey(name.toLowerCase())) {
            sender.sendMessage(CC.translate("&cThat custom item doesn't exist."));
            return;
        }

        if (sender.getInventory().getItemInMainHand() == null || sender.getInventory().getItemInMainHand().getType() == Material.AIR) {
            sender.sendMessage(CC.translate("&cYou need to have an item in your hand."));
            return;
        }

        this.shopHandler.getCustomItems().put(name.toLowerCase(), sender.getInventory().getItemInMainHand().clone());
        this.shopHandler.saveCustomItems();
        sender.sendMessage(CC.translate("&aUpdated the '" + name + "' custom item!"));
    }

    @Subcommand("admin deleteitem")
    @CommandPermission("legend.command.shop.admin")
    @CommandCompletion("@customItems")
    public void deleteitem(Player sender, @Name("name") String name) {
        if (!this.shopHandler.getCustomItems().containsKey(name.toLowerCase())) {
            sender.sendMessage(CC.translate("&cThat custom item doesn't exist."));
            return;
        }

        this.shopHandler.getCustomItems().remove(name.toLowerCase());
        this.shopHandler.saveCustomItems();
        sender.sendMessage(CC.translate("&cDeleted the '" + name + "' custom item!"));
    }

}
