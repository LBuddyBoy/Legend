package dev.lbuddyboy.legend.features.recipe.impl;

import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.lifesteal.LifeSteal;
import dev.lbuddyboy.lifesteal.extras.recipe.AbstractRecipe;
import dev.lbuddyboy.lifesteal.team.TeamConstants;
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
public class ChunkTNTRecipe extends AbstractRecipe {

    @Override
    public String getId() {
        return "Chunk-TNT";
    }

    @Override
    public ItemStack getDisplayItem() {
        return new ItemFactory(Material.TNT)
                .displayName("&4&lChunk TNT &7(Right Click)")
                .lore("&7Click to view the chunk tnt recipe!")
                .build();
    }

    @Override
    public int getMenuSlot() {
        return 0;
    }

    @Override
    public ShapedRecipe getRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(LifeSteal.getInstance(), getId()), getItem());

        recipe.shape(
                "ABA",
                "BAB",
                "ABA"
        );
        recipe.setIngredient('A', Material.WITHER_SKELETON_SKULL);
        recipe.setIngredient('B', Material.ENCHANTED_GOLDEN_APPLE);

        return recipe;
    }

    @Override
    public ItemStack getItem() {
        return TeamConstants.CHUNK_TNT;
    }
}
