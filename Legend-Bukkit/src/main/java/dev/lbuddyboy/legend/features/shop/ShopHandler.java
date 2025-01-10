package dev.lbuddyboy.legend.features.shop;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.LegendConstants;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.features.shop.model.ShopCategory;
import dev.lbuddyboy.legend.features.shop.model.ShopItem;
import dev.lbuddyboy.legend.user.model.LegendUser;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
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
import java.util.*;

@Getter
public class ShopHandler implements IModule, Listener {

    private final Map<String, ItemStack> customItems;
    private final List<ShopItem> shopItems;

    public ShopHandler() {
        this.customItems = new HashMap<>();
        this.shopItems = new ArrayList<>();
    }

    @Override
    public void load() {
        LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(this, LegendBukkit.getInstance());

        for (String key : SettingsConfig.SHOP_CUSTOM_ITEMS.getStringList()) {
            String[] parts = key.split(";");
            String id = parts[0];
            ItemStack item = ItemUtils.itemStackFromBase64(parts[1]);

            if (item == null) continue;

            this.customItems.put(id.toLowerCase(), item.clone());
        }

        for (Material material : Material.values()) {
            if (material.name().endsWith("_CONCRETE")) {
                this.shopItems.add(new ShopItem(ShopCategory.CONCRETE, 1.25D, 16, new ItemFactory(material)));
            } else if (material.name().endsWith("_WOOL")) {
                this.shopItems.add(new ShopItem(ShopCategory.WOOL, 1.25D, 16, new ItemFactory(material)));
            } else if (material.name().endsWith("_STAINED_GLASS")) {
                this.shopItems.add(new ShopItem(ShopCategory.GLASS, 1.25D, 16, new ItemFactory(material)));
            }
            if (material.name().endsWith("_WOOD")) {
                this.shopItems.add(new ShopItem(ShopCategory.EXTRA, 18.25D, 16, new ItemFactory(material)));
            }
        }

        this.shopItems.add(new ShopItem(ShopCategory.BUY, 78.125D, 16, new ItemFactory(Material.MELON_SLICE)));
        this.shopItems.add(new ShopItem(ShopCategory.BUY, 187.5D, 16, new ItemFactory(Material.RABBIT_FOOT)));
        this.shopItems.add(new ShopItem(ShopCategory.BUY, 187.5D, 4, new ItemFactory(Material.GHAST_TEAR)));
        this.shopItems.add(new ShopItem(ShopCategory.BUY, 187.5D, 4, new ItemFactory(Material.MAGMA_CREAM)));
        this.shopItems.add(new ShopItem(ShopCategory.BUY, 250.0D, 4, new ItemFactory(Material.COW_SPAWN_EGG)));
        this.shopItems.add(new ShopItem(ShopCategory.BUY, 12.25D, 16, new ItemFactory(Material.SAND)));
        this.shopItems.add(new ShopItem(ShopCategory.BUY, 18.25D, 16, new ItemFactory(Material.OAK_WOOD)));
        this.shopItems.add(new ShopItem(ShopCategory.BUY, 14.25D, 16, new ItemFactory(Material.NETHER_WART)));
        this.shopItems.add(new ShopItem(ShopCategory.BUY, 14.25D, 16, new ItemFactory(Material.SUGAR_CANE)));
        this.shopItems.add(new ShopItem(ShopCategory.BUY, 53.125D, 16, new ItemFactory(Material.GLOWSTONE_DUST)));
        this.shopItems.add(new ShopItem(ShopCategory.BUY, 50.0D, 16, new ItemFactory(Material.GUNPOWDER)));
        this.shopItems.add(new ShopItem(ShopCategory.BUY, 200.0D, 4, new ItemFactory(Material.BLAZE_ROD)));
        this.shopItems.add(new ShopItem(ShopCategory.BUY, 100.0D, 4, new ItemFactory(Material.COBWEB)));
        this.shopItems.add(new ShopItem(ShopCategory.BUY, 6000.0D, 1, new ItemFactory(Material.END_PORTAL_FRAME)));
        this.shopItems.add(new ShopItem(ShopCategory.BUY, 11_500.0D, 1, new ItemFactory(Material.SPAWNER).spawner(EntityType.ZOMBIE).displayName("&aZombie Spawner")));
        this.shopItems.add(new ShopItem(ShopCategory.BUY, 14_000.0D, 1, new ItemFactory(Material.SPAWNER).spawner(EntityType.SPIDER).displayName("&aSpider Spawner")));
        this.shopItems.add(new ShopItem(ShopCategory.BUY, 16_500.0D, 1, new ItemFactory(Material.SPAWNER).spawner(EntityType.CAVE_SPIDER).displayName("&aCave Spider Spawner")));
        this.shopItems.add(new ShopItem(ShopCategory.BUY, 20_000.0D, 1, new ItemFactory(Material.SPAWNER).spawner(EntityType.SKELETON).displayName("&aSkeleton Spawner")));

        this.shopItems.add(new ShopItem(ShopCategory.SELL, 50.0D, 16, new ItemFactory(Material.COPPER_BLOCK)));
        this.shopItems.add(new ShopItem(ShopCategory.SELL, 62.5D, 16, new ItemFactory(Material.COAL_BLOCK)));
        this.shopItems.add(new ShopItem(ShopCategory.SELL, 78.125D, 16, new ItemFactory(Material.LAPIS_BLOCK)));
        this.shopItems.add(new ShopItem(ShopCategory.SELL, 87.5D, 16, new ItemFactory(Material.REDSTONE_BLOCK)));
        this.shopItems.add(new ShopItem(ShopCategory.SELL, 100.0D, 16, new ItemFactory(Material.AMETHYST_BLOCK)));
        this.shopItems.add(new ShopItem(ShopCategory.SELL, 109.375D, 16, new ItemFactory(Material.IRON_BLOCK)));
        this.shopItems.add(new ShopItem(ShopCategory.SELL, 125.0D, 16, new ItemFactory(Material.GOLD_BLOCK)));
        this.shopItems.add(new ShopItem(ShopCategory.SELL, 156.25D, 16, new ItemFactory(Material.DIAMOND_BLOCK)));
        this.shopItems.add(new ShopItem(ShopCategory.SELL, 234.375D, 16, new ItemFactory(Material.EMERALD_BLOCK)));
        this.shopItems.add(new ShopItem(ShopCategory.SELL, 312.5D, 16, new ItemFactory(Material.NETHERITE_BLOCK)));

        this.shopItems.add(new ShopItem(ShopCategory.EXTRA, 8.75D, 16, new ItemFactory(Material.NETHER_BRICK)));
        this.shopItems.add(new ShopItem(ShopCategory.EXTRA, 8.75D, 16, new ItemFactory(Material.PURPUR_BLOCK)));
        this.shopItems.add(new ShopItem(ShopCategory.EXTRA, 8.75D, 16, new ItemFactory(Material.PURPUR_PILLAR)));
        this.shopItems.add(new ShopItem(ShopCategory.EXTRA, 8.75D, 16, new ItemFactory(Material.CHAIN)));
        this.shopItems.add(new ShopItem(ShopCategory.EXTRA, 8.75D, 16, new ItemFactory(Material.QUARTZ_BLOCK)));
        this.shopItems.add(new ShopItem(ShopCategory.EXTRA, 8.75D, 16, new ItemFactory(Material.SMOOTH_QUARTZ)));
        this.shopItems.add(new ShopItem(ShopCategory.EXTRA, 8.75D, 16, new ItemFactory(Material.DEEPSLATE_BRICKS)));
        this.shopItems.add(new ShopItem(ShopCategory.EXTRA, 8.75D, 16, new ItemFactory(Material.BAMBOO)));
        this.shopItems.add(new ShopItem(ShopCategory.EXTRA, 31.25D, 16, new ItemFactory(Material.BAMBOO_BLOCK)));
        this.shopItems.add(new ShopItem(ShopCategory.EXTRA, 8.75D, 16, new ItemFactory(Material.PRISMARINE_BRICKS)));
        this.shopItems.add(new ShopItem(ShopCategory.EXTRA, 8.75D, 16, new ItemFactory(Material.DARK_PRISMARINE)));
        this.shopItems.add(new ShopItem(ShopCategory.EXTRA, 8.75D, 16, new ItemFactory(Material.SEA_LANTERN)));
    }

    @Override
    public void unload() {

    }

    public void saveCustomItems() {
        SettingsConfig.SHOP_CUSTOM_ITEMS.update(this.customItems.entrySet().stream().map(e -> e.getKey() + ";" + ItemUtils.itemStackToBase64(e.getValue())).toList());
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
