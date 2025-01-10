package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import org.bukkit.entity.Player;

@CommandAlias("hq|home")
public class HomeCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        TeamCommand.home(sender);
    }

}
