package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

@CommandAlias("build|bypass")
@CommandPermission("legend.command.build")
public class BuildCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        if (sender.hasMetadata("BUILD_MODE")) {
            sender.removeMetadata("BUILD_MODE", LegendBukkit.getInstance());
            sender.sendMessage(CC.translate("&cBuild mode disabled."));
            return;
        }

        sender.sendMessage(CC.translate("&aBuild mode enabled."));
        sender.setMetadata("BUILD_MODE", new FixedMetadataValue(LegendBukkit.getInstance(), true));
    }

}
