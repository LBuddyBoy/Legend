package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.LocationUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.timer.impl.CombatTimer;
import dev.lbuddyboy.legend.timer.impl.SpawnTimer;
import dev.lbuddyboy.legend.timer.server.SOTWTimer;
import org.bukkit.entity.Player;

@CommandAlias("tutorial|tut")
public class TutorialCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        if (!TeamType.SPAWN.appliesAt(sender.getLocation())) {

            return;
        }

        sender.teleport(SettingsConfig.TUTORIAL.getLocation());
    }

    @Subcommand("set")
    @CommandPermission("legend.command.tutorial")
    public void set(Player sender) {
        SettingsConfig.TUTORIAL.update(LocationUtils.serializeString(sender.getLocation()));
        sender.sendMessage(CC.translate("&aTutorial Location updated!"));
    }

}
