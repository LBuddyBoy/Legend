package dev.lbuddyboy.lifesteal.extras.recipe;

import co.aikar.commands.BaseCommand;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.lifesteal.timer.PlayerTimer;
import dev.lbuddyboy.lifesteal.util.ClassUtils;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
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

        for (Class<?> clazz : ClassUtils.findClasses("dev.lbuddyboy.lifesteal", AbstractRecipe.class)) {
            try {
                AbstractRecipe recipe = (AbstractRecipe) clazz.getDeclaredConstructor().newInstance();

                this.recipes.put(recipe.getId(), recipe);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

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
