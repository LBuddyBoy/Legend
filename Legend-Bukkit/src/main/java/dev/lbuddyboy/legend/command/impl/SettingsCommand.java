package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import dev.lbuddyboy.legend.features.settings.SettingsMenu;
import org.bukkit.entity.Player;

public class SettingsCommand extends BaseCommand {

    @CommandAlias("settings")
    public void settings(Player sender) {
        new SettingsMenu().openMenu(sender);
    }

}
