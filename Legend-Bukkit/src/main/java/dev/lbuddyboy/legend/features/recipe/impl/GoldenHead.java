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
public class GoldenHead extends AbstractRecipe {

    private final ItemStack goldenHeadItem = new ItemFactory("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTkyZWFhY2QyOTBlYWQzN2ViMWEyMDJhYzczNjdmMzJiZTc0Y2Y0YWM3NzIzZTA2N2M0NjU4YmY2MmMzZGJkNiJ9fX0=")
            .displayName("&6Golden Head")
            .build();

    @Override
    public String getId() {
        return "Golden Head";
    }

    @Override
    public ItemStack getDisplayItem() {
        return new ItemFactory("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTkyZWFhY2QyOTBlYWQzN2ViMWEyMDJhYzczNjdmMzJiZTc0Y2Y0YWM3NzIzZTA2N2M0NjU4YmY2MmMzZGJkNiJ9fX0=")
                .displayName("&6Golden Head")
                .lore("&7Click to view the golden head recipe!")
                .build();
    }

    @Override
    public int getMenuSlot() {
        return 5;
    }

    @Override
    public ShapedRecipe getRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(getKey(), getItem());

        recipe.shape(
                "AAA",
                "ABA",
                "AAA"
        );
        recipe.setIngredient('A', Material.GOLD_INGOT);
        recipe.setIngredient('B', Material.PLAYER_HEAD);

        return recipe;
    }

    @Override
    public ItemStack getItem() {
        NBTItem nbtItem = new NBTItem(this.goldenHeadItem.clone());

        nbtItem.setBoolean("golden-head", true);

        return nbtItem.getItem();
    }
}
