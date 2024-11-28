package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.timer.impl.HomeTimer;
import dev.lbuddyboy.legend.timer.impl.LogoutTimer;
import org.bukkit.entity.Player;

@CommandAlias("logout|lout")
public class LogoutCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        LogoutTimer timer = LegendBukkit.getInstance().getTimerHandler().getTimer(LogoutTimer.class);

        timer.start(sender);
    }

}
