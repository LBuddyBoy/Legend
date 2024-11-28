package dev.lbuddyboy.lifesteal.extras.recipe.menu;

import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.IMenu;
import dev.lbuddyboy.lifesteal.LifeSteal;
import dev.lbuddyboy.lifesteal.extras.adminitems.AdminItem;
import dev.lbuddyboy.lifesteal.extras.recipe.AbstractRecipe;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project LifeSteal
 * @file dev.lbuddyboy.lifesteal.extras.recipe.menu
 * @since 1/7/2024
 */

public class RecipeMenu extends IMenu {

    @Override
    public String getTitle(Player player) {
        return "Custom Recipes";
    }

    @Override
    public int getSize(Player player) {
        return 27;
    }

    @Override
    public Map<Integer, IButton> getButtons(Player player) {
        Map<Integer, IButton> buttons = new HashMap<>();
        List<Integer> slots = getCenteredSlots(player);
        int i = 0;

        for (AbstractRecipe recipe : LifeSteal.getInstance().getRecipeHandler().getRecipes().values()) {
            if (slots.size() <= i) continue;
            int slot = slots.get(i++);

            buttons.put(slot, new RecipeButton(recipe));
        }

        return buttons;
    }

    @AllArgsConstructor
    public class RecipeButton extends IButton {

        private final AbstractRecipe recipe;

        @Override
        public ItemStack getItem(Player player) {
            return this.recipe.getDisplayItem();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            new RecipeViewMenu(recipe).openMenu(player);
        }
    }

}
