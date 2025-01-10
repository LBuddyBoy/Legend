package dev.lbuddyboy.legend.features.shop.menu;

import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.IMenu;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.legend.features.shop.model.ShopCategory;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ShopMainMenu extends IMenu {

    @Override
    public String getTitle(Player player) {
        return "Server Shop";
    }

    @Override
    public int getSize(Player player) {
        return 27;
    }

    @Override
    public Map<Integer, IButton> getButtons(Player player) {
        Map<Integer, IButton> buttons = new HashMap<>();

        for (ShopCategory category : ShopCategory.values()) {
            buttons.put(category.getMenuSlot(), new CategoryButton(category));
        }

        return buttons;
    }

    @Override
    public boolean autoFills(Player player) {
        return true;
    }

    @AllArgsConstructor
    public class CategoryButton extends IButton {

        private final ShopCategory category;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(this.category.getHeadTexture())
                    .displayName(this.category.getColoredName() + " &7(Click)")
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            new ShopItemMenu(this.category).openMenu(player);
        }
    }

}
