package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.timer.impl.CombatTimer;
import net.citizensnpcs.api.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("trash|dispose")
public class TrashCommand extends BaseCommand {

    @Default
    public static void def(Player sender) {
        if (LegendBukkit.getInstance().getTimerHandler().getTimer(CombatTimer.class).isActive(sender.getUniqueId())) {
            return;
        }
        if (!TeamType.SPAWN.appliesAt(sender.getLocation())) {
            return;
        }

        sender.openInventory(Bukkit.createInventory(null, 54, "Trash Can"));
    }

}
