package dev.lbuddyboy.legend.features.recipe.impl;

import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.legend.features.recipe.AbstractRecipe;
import net.minecraft.server.v1_8_R3.ShapelessRecipes;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project LifeSteal
 * @file dev.lbuddyboy.lifesteal.extras.recipe.impl
 * @since 1/7/2024
 */
public class GlisteringMelon extends AbstractRecipe {

    @Override
    public String getId() {
        return "Glistering Melon";
    }

    @Override
    public ItemStack getDisplayItem() {
        return new ItemFactory(Material.SPECKLED_MELON)
                .displayName("&cGlistering Melon")
                .lore("&7Click to view the glistering melon recipe!")
                .build();
    }

    @Override
    public int getMenuSlot() {
        return 5;
    }

    @Override
    public ShapelessRecipe getRecipe() {
        ShapelessRecipe recipe = new ShapelessRecipe(getItem());

        recipe.addIngredient(Material.GOLD_NUGGET).addIngredient(Material.MELON);

        return recipe;
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Material.SPECKLED_MELON);
    }
}
