package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.util.CommandUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("ping|latency")
public class PingCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        String name = "&a" + sender.getPing();

        if (sender.getPing() >= 100) {
            name = "&e" + sender.getPing();
        }

        if (sender.getPing() >= 200) {
            name = "&c" + sender.getPing();
        }

        if (sender.getPing() >= 300) {
            name = "&4" + sender.getPing();
        }

        sender.sendMessage(CC.translate("&fYour Ping: " + name + "ms"));
    }

}
