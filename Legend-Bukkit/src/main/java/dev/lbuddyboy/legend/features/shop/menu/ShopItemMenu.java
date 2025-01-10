package dev.lbuddyboy.legend.features.shop.menu;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.IMenu;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.shop.model.ShopCategory;
import dev.lbuddyboy.legend.features.shop.model.ShopItem;
import dev.lbuddyboy.legend.user.model.LegendUser;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class ShopItemMenu extends IPagedMenu {

    private final ShopCategory category;

    @Override
    public String getPageTitle(Player player) {
        return this.category.getName();
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();

        for (ShopItem item : LegendBukkit.getInstance().getShopHandler().getShopItems()) {
            if (item.getCategory() != this.category) continue;

            buttons.add(new ItemButton(item));
        }

        return buttons;
    }

    @Override
    public IMenu fallbackMenu(Player player) {
        return new ShopMainMenu();
    }

    @AllArgsConstructor
    public class ItemButton extends IButton {

        private final ShopItem item;

        @Override
        public ItemStack getItem(Player player) {
            ItemStack item = this.item.getItem();
            List<String> lore = new ArrayList<>();

            if (this.item.getCategory() == ShopCategory.SELL) {
                lore.addAll(Arrays.asList(
                        "&fSell " + category.getSecondaryColor() + "16x " + ItemUtils.getName(item) + "&f for " + category.getSecondaryColor() + "$" + APIConstants.formatNumber(this.item.getPrice() * this.item.getAmount())
                ));
            } else {
                lore.addAll(Arrays.asList(
                        "&fBuy " + category.getSecondaryColor() + this.item.getAmount() + "x " + ItemUtils.getName(item) + "&f for " + category.getSecondaryColor() + "$" + APIConstants.formatNumber(this.item.getPrice() * this.item.getAmount())
                ));
            }

            return new ItemFactory(item)
                    .displayName(CC.blend(CC.stripColor(ItemUtils.getName(item)), category.getPrimaryColor(), category.getSecondaryColor()) + " &7(Click)")
                    .lore(lore)
                    .amount(this.item.getAmount())
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());
            boolean purchasing = category != ShopCategory.SELL;
            ItemStack item = this.item.getItem();
            int amount = this.item.getAmount();
            double price = this.item.getPrice() * amount;

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

            int amountOfItems = ItemUtils.countStackAmountMatching(player.getInventory().getStorageContents(), (i -> i.isSimilar(item)));

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

}
