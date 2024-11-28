package dev.lbuddyboy.legend.command.context;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.recipe.AbstractRecipe;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project lPractice
 * @file dev.lbuddyboy.practice.command.context
 * @since 5/3/2024
 */
public class AbstractRecipeContext implements ContextResolver<AbstractRecipe, BukkitCommandExecutionContext> {

    @Override
    public AbstractRecipe getContext(BukkitCommandExecutionContext arg) throws InvalidCommandArgument {
        String source = arg.popFirstArg();
        AbstractRecipe recipe = LegendBukkit.getInstance().getRecipeHandler().getRecipes().get(source.toLowerCase());

        if (recipe != null) {
            return recipe;
        }

        throw new InvalidCommandArgument(CC.translate("&cThat recipe does not exist."));
    }
}
