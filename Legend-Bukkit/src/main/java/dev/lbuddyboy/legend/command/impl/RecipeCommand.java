package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.features.recipe.AbstractRecipe;
import dev.lbuddyboy.legend.features.recipe.menu.RecipeMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

    @Subcommand("admin giveitem")
    @CommandPermission("legend.command.recipe.admin")
    @CommandCompletion("@players @recipes")
    public void giveItem(CommandSender sender, @Name("target") OnlinePlayer player, @Name("recipe") AbstractRecipe recipe, @Name("amount") @Optional Integer amount) {
        if (amount == null) amount = 1;

        ItemStack stack = recipe.getItem().clone();
        stack.setAmount(amount);

        ItemUtils.tryFit(player.getPlayer(), stack, false);
        sender.sendMessage(CC.translate("<blend:&6;&e>" + player.getPlayer().getName() + " has just received " + amount + "x of the " + recipe.getId() + " recipe!</>"));
    }

}
