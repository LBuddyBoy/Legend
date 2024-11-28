package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Optional;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayTimeCommand extends BaseCommand {

    @CommandAlias("playtime|ptime|pt")
    public void def(Player sender, @Name("player") @Optional UUID target) {
        if (target == null) target = sender.getUniqueId();

        LegendBukkit.getInstance().getUserHandler().loadAsync(target).thenAcceptAsync(user -> {
            long playTime = user.getTotalPlayTime();

            sender.sendMessage(CC.translate(
                    LegendBukkit.getInstance().getLanguage().getString("playtime")
                            .replaceAll("%playtime%", TimeUtils.formatIntoDetailedString(playTime))
                            .replaceAll("%player%", user.getName())
            ));
        });
    }

}
