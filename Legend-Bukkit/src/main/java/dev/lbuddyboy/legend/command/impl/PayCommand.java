package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ShortPrice;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.model.LegendUser;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("pay|p2p")
public class PayCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public void def(Player sender, @Name("player") OfflinePlayer target, @Name("amount") ShortPrice amount) {
        LegendUser senderUser = LegendBukkit.getInstance().getUserHandler().getUser(sender.getUniqueId());
        LegendUser targetUser = LegendBukkit.getInstance().getUserHandler().getUser(target.getUniqueId());

        if (senderUser.getBalance() < amount.convert()) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("economy.insufficient")
                    .replaceAll("%amount%", amount.convertFancy())
            ));
            return;
        }

        senderUser.setBalance(senderUser.getBalance() - amount.convert());
        targetUser.setBalance(targetUser.getBalance() + amount.convert());

        sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("economy.add.sender")
                .replaceAll("%target%", target.getName())
                .replaceAll("%amount%", amount.convertFancy())
        ));

        if (target.isOnline() && target.getPlayer() != null) {
            target.getPlayer().sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("economy.add.target")
                    .replaceAll("%sender%", sender.getName())
                    .replaceAll("%amount%", amount.convertFancy())
            ));
        }
    }

}
