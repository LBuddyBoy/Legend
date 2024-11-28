package dev.lbuddyboy.legend.features.recipe;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.legend.features.recipe.impl.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project LifeSteal
 * @file dev.lbuddyboy.lifesteal.extras.recipe
 * @since 1/7/2024
 */

@Getter
public class RecipeHandler implements IModule {

    private final Map<String, AbstractRecipe> recipes;

    public RecipeHandler() {
        this.recipes = new HashMap<>();
    }

    @Override
    public void load() {

        Iterator<Recipe> recipeIterator = Bukkit.getServer().recipeIterator();

        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            ItemStack item = recipe.getResult();

            if (item == null) continue;
            if (item.getType() == Material.SPECKLED_MELON) recipeIterator.remove();
            if (item.getType() == Material.GOLDEN_APPLE && item.getDurability() == 1) recipeIterator.remove();
            if (item.getType() == Material.TNT) recipeIterator.remove();
            if (item.getType() == Material.BOAT) recipeIterator.remove();
            if (item.getType() == Material.LEASH) recipeIterator.remove();

        }

        this.recipes.put("Chainmail Helmet", new ChainmailHelmet());
        this.recipes.put("Chainmail Chestplate", new ChainmailChestplate());
        this.recipes.put("Chainmail Leggings", new ChainmailLeggings());
        this.recipes.put("Chainmail Boots", new ChainmailBoots());
        this.recipes.put("Glistering Melon", new GlisteringMelon());

        this.recipes.values().forEach(recipe -> {
            Bukkit.addRecipe(recipe.getRecipe());
        });

    }

    @Override
    public void unload() {

    }

    public AbstractRecipe getRecipe(Class<? extends AbstractRecipe> clazz) {
        return this.recipes.values().stream().filter(clazz::isInstance).findFirst().get();
    }

}
