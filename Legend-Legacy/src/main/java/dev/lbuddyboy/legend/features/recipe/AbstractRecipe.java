package dev.lbuddyboy.legend.features.recipe;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project LifeSteal
 * @file dev.lbuddyboy.lifesteal.extras.recipe
 * @since 1/7/2024
 */
public abstract class AbstractRecipe {

    public abstract String getId();
    public abstract ItemStack getDisplayItem();
    public abstract int getMenuSlot();
    public abstract Recipe getRecipe();
    public abstract ItemStack getItem();

}
