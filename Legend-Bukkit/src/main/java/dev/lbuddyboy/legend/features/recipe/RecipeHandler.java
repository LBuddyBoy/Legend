package dev.lbuddyboy.legend.features.recipe;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.legend.LegendBukkit;
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

            if (item == null || item.getType() == Material.AIR) continue;

            if (!LegendBukkit.getInstance().getSettings().getBoolean("server.uhc-mode", false)) {
                if (item.getType() == Material.GLISTERING_MELON_SLICE) recipeIterator.remove();
            }

            if (item.getType() == Material.ENCHANTED_GOLDEN_APPLE) recipeIterator.remove();
            if (item.getType() == Material.TNT) recipeIterator.remove();
            if (item.getType().name().endsWith("_BOAT")) recipeIterator.remove();
            if (item.getType() == Material.LEAD) recipeIterator.remove();

        }

        if (LegendBukkit.getInstance().getSettings().getBoolean("classes.rogue.enabled", true)) {
            this.recipes.put("chainmail_helmet", new ChainmailHelmet());
            this.recipes.put("chainmail_chestplate", new ChainmailChestplate());
            this.recipes.put("chainmail_leggings", new ChainmailLeggings());
            this.recipes.put("chainmail_boots", new ChainmailBoots());
        }

        if (LegendBukkit.getInstance().getSettings().getBoolean("server.uhc-mode", false)) {
            this.recipes.put("golden_head", new GoldenHead());
            this.recipes.put("player_head", new PlayerHead());
        } else {
            this.recipes.put("glistering_melon", new GlisteringMelon());
        }

        this.recipes.values().forEach(recipe -> Bukkit.addRecipe(recipe.getRecipe()));

    }

    @Override
    public void unload() {
        this.recipes.values().forEach(recipe -> Bukkit.removeRecipe(recipe.getKey()));
    }

}
