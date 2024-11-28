package dev.lbuddyboy.legend.features.shop;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.model.LegendUser;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class ShopHandler implements IModule, Listener {

    @Override
    public void load() {

    }

    @Override
    public void unload() {

    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (event.getLines().length < 4) return;
        String header = event.getLine(0);
        if (!event.getPlayer().isOp()) return;
        if (!CC.stripColor(header).contains("- Buy -") && !CC.stripColor(header).contains("- Sell -")) return;

        event.setLine(0, CC.translate(header));
        event.getPlayer().sendMessage(CC.translate("&aShop sign created!"));
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null) return;
        if (!(block.getState() instanceof Sign)) return;

        Sign sign = (Sign) block.getState();
        if (sign.getLines().length < 4) return;

        String header = sign.getLine(0);

        if (!CC.stripColor(header).equalsIgnoreCase("- Buy -") && !CC.stripColor(header).equalsIgnoreCase("- Sell -")) return;

        boolean purchasing = CC.stripColor(header).equalsIgnoreCase("- Buy -");

        String materialLine = sign.getLine(1);
        String amountLine = sign.getLine(2);
        String priceLine = sign.getLine(3).replaceAll("\\$", "").replaceAll(",", "");

        ItemStack item = null;
        int amount = 0;
        double price = 0D;
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        try {
            amount = Integer.parseInt(amountLine);
        } catch (NumberFormatException ignored) {
            player.sendMessage(CC.translate("&cError reading the amount, contact an admin for further information."));
            return;
        }

        if (materialLine.contains(":")) {
            item = new ItemStack(Material.getMaterial(materialLine.split(":")[0]), amount);
            item.setDurability((short) Integer.parseInt(materialLine.split(":")[1]));
        } else {
            try {
                materialLine = materialLine.replaceAll(" ", "_").toUpperCase();
                item = new ItemStack(Material.getMaterial(materialLine), amount);
            } catch (Exception ignored) {
                player.sendMessage(CC.translate("&cError reading the material, contact an admin for further information."));
                return;
            }
        }

        try {
            price = NumberFormat.getInstance(Locale.ENGLISH).parse(priceLine).doubleValue();
        } catch (ParseException e) {
            player.sendMessage(CC.translate("&cError reading the price, contact an admin for further information."));
            return;
        }

        if (purchasing) {
            ItemStack toFit = ItemUtils.tryFit(player, item);

            if (toFit != null) {
                player.sendMessage(CC.translate("&cYou do not have enough inventory space."));
                return;
            }

            if (user.getBalance() < price) {
                player.sendMessage(CC.translate("&cInsufficient funds."));
                return;
            }

            player.getInventory().addItem(item);
            user.setBalance(user.getBalance() - price);
            player.sendMessage(CC.translate("&aSuccessfully purchased x" + amount + " " + ItemUtils.getName(item) + " for $" + APIConstants.formatNumber(price) + "!"));
            return;
        }

        ItemStack finalItem = item;
        int amountOfItems = ItemUtils.countStackAmountMatching(player.getInventory().getContents(), (i -> i.isSimilar(finalItem)));

        if (amountOfItems <= 0) {
            player.sendMessage(CC.translate("&cYou do not have any " + ItemUtils.getName(item) + " in your inventory."));
            return;
        }

        int newAmount = Math.min(amountOfItems, amount);
        double newPrice = (price / amount) * newAmount;

        user.setBalance(user.getBalance() + newPrice);
        ItemUtils.removeAmount(player.getInventory(), item, newAmount);
        player.sendMessage(CC.translate("&aSuccessfully sold x" + newAmount + " " + ItemUtils.getName(item) + " for $" + APIConstants.formatNumber(newPrice) + "!"));
    }

}
