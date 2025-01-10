package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

@CommandAlias("revive")
public class ReviveCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public void def(CommandSender sender, @Name("player") OfflinePlayer player) {
        LivesCommand.revive(sender, player);
    }

}
