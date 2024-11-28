package dev.lbuddyboy.legend.features.recipe.impl;

import de.tr7zw.nbtapi.NBTItem;
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
public class PlayerHead extends AbstractRecipe {

    @Override
    public String getId() {
        return "Player Head";
    }

    @Override
    public ItemStack getDisplayItem() {
        return new ItemFactory(Material.PLAYER_HEAD)
                .displayName("&ePlayer Head")
                .lore("&7Click to view the player head recipe!")
                .build();
    }

    @Override
    public int getMenuSlot() {
        return 6;
    }

    @Override
    public ShapedRecipe getRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(getKey(), getItem());

        recipe.shape(
                "ACA",
                "CBC",
                "ACA"
        );
        recipe.setIngredient('A', Material.GOLD_NUGGET);
        recipe.setIngredient('B', Material.SKELETON_SKULL);
        recipe.setIngredient('C', Material.GOLD_INGOT);

        return recipe;
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Material.PLAYER_HEAD);
    }
}
