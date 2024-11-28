package dev.lbuddyboy.legend.features.recipe.menu;

import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.IMenu;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.recipe.AbstractRecipe;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        for (AbstractRecipe recipe : LegendBukkit.getInstance().getRecipeHandler().getRecipes().values().stream().sorted(Comparator.comparingInt(AbstractRecipe::getMenuSlot)).toList()) {
            if (slots.size() <= i) continue;
            int slot = slots.get(i++);

            buttons.put(slot, new RecipeButton(recipe));
        }

        return buttons;
    }

    @Override
    public boolean autoFills(Player player) {
        return true;
    }

    @AllArgsConstructor
    public class RecipeButton extends IButton {

        private final AbstractRecipe recipe;

        @Override
        public ItemStack getItem(Player player) {
            ItemFactory factory = new ItemFactory(this.recipe.getDisplayItem());

            if (player.hasPermission("legend.command.recipe.admin")) {
                factory.addToLore("<blend:&4;&c>[Admin Only] Right Click to obtain this item.</>");
            }

            return factory.build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            if (clickType == ClickType.RIGHT) {
                player.getInventory().addItem(this.recipe.getItem().clone());
                return;
            }

            new RecipeViewMenu(recipe).openMenu(player);
        }
    }

}
