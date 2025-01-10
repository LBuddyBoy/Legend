package dev.lbuddyboy.legend.features.shop.model;

import dev.lbuddyboy.commons.util.ItemFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
public class ShopItem {

    private final ShopCategory category;
    private final double price;
    private final int amount;
    private final ItemFactory factory;

    public ItemStack getItem() {
        return this.factory.build();
    }

}
