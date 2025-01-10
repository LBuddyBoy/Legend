package dev.lbuddyboy.legend.command.impl.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import org.bukkit.command.CommandSender;

@CommandAlias("chatgame|chatgames")
@CommandPermission("legend.command.chatgame")
public class ChatGameCommand extends BaseCommand {

    @Subcommand("startrandom")
    public void startRandom(CommandSender sender) {
        LegendBukkit.getInstance().getChatGameHandler().startRandom();
        sender.sendMessage(CC.translate("&aStarted a random chat game!"));
    }

}
