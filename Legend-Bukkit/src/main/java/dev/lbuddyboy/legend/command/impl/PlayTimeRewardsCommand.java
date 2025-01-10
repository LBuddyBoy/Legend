package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.legend.features.playtime.menu.PlayTimeRewardsMenu;
import org.bukkit.entity.Player;

@CommandAlias("ptrewards|playtimerewards|ptreward")
public class PlayTimeRewardsCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        new PlayTimeRewardsMenu().openMenu(sender);
    }

    @Subcommand("rewards|reward")
    public void rewards(Player sender) {
        new PlayTimeRewardsMenu().openMenu(sender);
    }

}
