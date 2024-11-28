package dev.lbuddyboy.legend.features.shop;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.LegendConstants;
import dev.lbuddyboy.legend.user.model.LegendUser;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Getter
public class ShopHandler implements IModule, Listener {

    private final Map<String, ItemStack> customItems;

    public ShopHandler() {
        this.customItems = new HashMap<>();
    }

    @Override
    public void load() {
        LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(this, LegendBukkit.getInstance());
        for (String key : LegendBukkit.getInstance().getSettings().getStringList("shop.custom-items")) {
            String[] parts = key.split(";");
            String id = parts[0];
            ItemStack item = ItemUtils.itemStackFromBase64(parts[1]);

            if (item == null) continue;

            this.customItems.put(id.toLowerCase(), item.clone());
        }
    }

    @Override
    public void unload() {

    }

    public void saveCustomItems() {
        LegendBukkit.getInstance().getSettings().set("shop.custom-items", this.customItems.entrySet().stream().map(e -> e.getKey() + ";" + ItemUtils.itemStackToBase64(e.getValue())).toList());
        LegendBukkit.getInstance().getSettings().save();
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
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Sign sign = (Sign) block.getState();
        if (sign.getLines().length < 4) return;

        String header = sign.getLine(0);

        if (!CC.stripColor(header).equalsIgnoreCase("- Buy -") && !CC.stripColor(header).equalsIgnoreCase("- Sell -")) return;

        if (!player.isOp() && !LegendConstants.isAdminBypass(player) && !player.isSneaking()) {
            event.setCancelled(true);
        }

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
                item = new ItemStack(Material.getMaterial(materialLine.replaceAll(" ", "_").toUpperCase()), amount);
            } catch (Exception ignored) {
                if (!this.customItems.containsKey(materialLine.toLowerCase())) {
                    player.sendMessage(CC.translate("&cError reading the material, contact an admin for further information."));
                    return;
                }

                item = this.customItems.get(materialLine.toLowerCase()).clone();
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
            player.sendMessage(CC.translate("&aSuccessfully purchased x" + amount + " " + ItemUtils.getName(item) + "&a for $" + APIConstants.formatNumber(price) + "!"));
            return;
        }

        ItemStack finalItem = item;
        int amountOfItems = ItemUtils.countStackAmountMatching(player.getInventory().getStorageContents(), (i -> i.isSimilar(finalItem)));

        if (amountOfItems <= 0) {
            player.sendMessage(CC.translate("&cYou do not have any " + ItemUtils.getName(item) + "&c in your inventory."));
            return;
        }

        int newAmount = Math.min(amountOfItems, amount);
        double newPrice = (price / amount) * newAmount;

        user.setBalance(user.getBalance() + newPrice);
        ItemUtils.removeAmount(player.getInventory(), item, newAmount);
        player.sendMessage(CC.translate("&aSuccessfully sold x" + newAmount + " " + ItemUtils.getName(item) + "&a for $" + APIConstants.formatNumber(newPrice) + "!"));
    }

}
