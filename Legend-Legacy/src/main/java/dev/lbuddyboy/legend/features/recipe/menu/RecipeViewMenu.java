package dev.lbuddyboy.lifesteal.extras.recipe.menu;

import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.IMenu;
import dev.lbuddyboy.commons.menu.button.FillButton;
import dev.lbuddyboy.lifesteal.extras.recipe.AbstractRecipe;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project LifeSteal
 * @file dev.lbuddyboy.lifesteal.extras.recipe.menu
 * @since 1/7/2024
 */

@AllArgsConstructor
public class RecipeViewMenu extends IMenu {

    private static final int[] SLOTS = new int[] {
            4, 5, 6,
            13, 14, 15,
            22, 23, 24
    };
    private static final int RESULT = 17;

    private final AbstractRecipe recipe;

    @Override
    public String getTitle(Player player) {
        return "Recipe View: " + this.recipe.getId();
    }

    @Override
    public int getSize(Player player) {
        return 27;
    }

    @Override
    public Map<Integer, IButton> getButtons(Player player) {
        Map<Integer, IButton> buttons = new HashMap<>();
        ShapedRecipe recipe = this.recipe.getRecipe();
        Map<Character, ItemStack> ingredients = recipe.getIngredientMap();

        int i = -1;
        for (String s : recipe.getShape()) {
            for (int j = 0; j < 3; j++) {
                try {
                    i++;
                    char character = s.charAt(j);
                    ItemStack item = ingredients.get(character);

                    buttons.put(SLOTS[i], new FillButton('i', item == null || item.getType() == Material.AIR ? new ItemStack(Material.AIR) : item));
                } catch (IndexOutOfBoundsException e) {
                    buttons.put(SLOTS[i], new FillButton('i', new ItemStack(Material.AIR)));

                }
            }
        }

        buttons.put(RESULT, new FillButton('r', recipe.getResult()));

        return buttons;
    }

    @Override
    public boolean autoFills(Player player) {
        return true;
    }

    @Override
    public IMenu fallbackMenu(Player player) {
        return new RecipeMenu();
    }
}
