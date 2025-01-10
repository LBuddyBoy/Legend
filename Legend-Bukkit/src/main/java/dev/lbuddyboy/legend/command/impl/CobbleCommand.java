package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.settings.Setting;
import org.bukkit.entity.Player;

@CommandAlias("cobble|cobblestone")
public class CobbleCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        Setting.COBBLESTONE.toggle(sender.getUniqueId());

        if (Setting.COBBLESTONE.isToggled(sender)) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("cobblestone.enabled")));
            return;
        }

        sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("cobblestone.disabled")));
    }

}
