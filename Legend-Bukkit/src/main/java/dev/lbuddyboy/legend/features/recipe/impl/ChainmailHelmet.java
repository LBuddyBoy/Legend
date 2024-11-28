package dev.lbuddyboy.legend.features.recipe.impl;

import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.recipe.AbstractRecipe;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project LifeSteal
 * @file dev.lbuddyboy.lifesteal.extras.recipe.impl
 * @since 1/7/2024
 */
public class ChainmailHelmet extends AbstractRecipe {

    @Override
    public String getId() {
        return "Chainmail Helmet";
    }

    @Override
    public ItemStack getDisplayItem() {
        return new ItemFactory(Material.CHAINMAIL_HELMET)
                .displayName("&7Rogue Helmet")
                .lore("&7Click to view the rogue helmet recipe!")
                .build();
    }

    @Override
    public int getMenuSlot() {
        return 1;
    }

    @Override
    public ShapedRecipe getRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(getKey(), getItem());

        recipe.shape(
                "AAA",
                "A A",
                "   "
        );
        recipe.setIngredient('A', Material.QUARTZ);

        return recipe;
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Material.CHAINMAIL_HELMET);
    }
}
