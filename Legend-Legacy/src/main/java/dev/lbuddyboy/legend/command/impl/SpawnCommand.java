package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import org.bukkit.entity.Player;

@CommandAlias("spawn")
public class SpawnCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        boolean permission = sender.hasPermission("legend.command.spawn");

        if (permission) {
            sender.teleport(LegendBukkit.getInstance().getSpawnLocation());
            return;
        }

        sender.sendMessage(CC.translate("&cNo permission."));
    }

}
