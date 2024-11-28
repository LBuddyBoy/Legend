package dev.lbuddyboy.legend.features.recipe.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.lifesteal.extras.recipe.menu.RecipeMenu;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project LifeSteal
 * @file dev.lbuddyboy.lifesteal.extras.recipe.command
 * @since 1/7/2024
 */

@CommandAlias("recipes|recipe")
public class RecipeCommand extends BaseCommand {

    @Default
    public void recipes(Player sender) {
        new RecipeMenu().openMenu(sender);
    }

}
