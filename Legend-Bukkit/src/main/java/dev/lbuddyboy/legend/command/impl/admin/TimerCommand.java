package dev.lbuddyboy.legend.command.impl.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.model.LegendUser;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

@CommandAlias("timer")
@CommandPermission("legend.command.timer")
public class TimerCommand extends BaseCommand {

    @Subcommand("activate|start")
    @CommandCompletion("@players @playerTimers ")
    public void activate(CommandSender sender, @Name("player") OfflinePlayer target, @Name("id") String id, @Name("duration") TimeDuration duration) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(target.getUniqueId());

        user.applyTimer(id, duration.transform());
        sender.sendMessage(CC.translate("&aApplied the " + id + " timer to " + target.getName() + " for " + duration.fancy()));
    }

    @Subcommand("deactivate|remove")
    @CommandCompletion("@players @playerTimers")
    public void remove(CommandSender sender, @Name("player") OfflinePlayer target, @Name("id") String id) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(target.getUniqueId());

        user.removeTimer(id);
        if (id.equalsIgnoreCase("enderpearl")) return;
        sender.sendMessage(CC.translate("&aRemoved the " + id + " timer from " + target.getName()));
    }

}
