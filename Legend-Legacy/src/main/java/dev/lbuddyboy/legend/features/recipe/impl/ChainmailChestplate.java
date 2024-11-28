package dev.lbuddyboy.legend.features.recipe.impl;

import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.legend.features.recipe.AbstractRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project LifeSteal
 * @file dev.lbuddyboy.lifesteal.extras.recipe.impl
 * @since 1/7/2024
 */
public class ChainmailChestplate extends AbstractRecipe {

    @Override
    public String getId() {
        return "Chainmail Chestplate";
    }

    @Override
    public ItemStack getDisplayItem() {
        return new ItemFactory(Material.CHAINMAIL_CHESTPLATE)
                .displayName("&7Rogue Chestplate")
                .lore("&7Click to view the rogue chestplate recipe!")
                .build();
    }

    @Override
    public int getMenuSlot() {
        return 2;
    }

    @Override
    public ShapedRecipe getRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(getItem());

        recipe.shape(
                "A A",
                "AAA",
                "AAA"
        );
        recipe.setIngredient('A', Material.QUARTZ);

        return recipe;
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Material.CHAINMAIL_CHESTPLATE);
    }
}
