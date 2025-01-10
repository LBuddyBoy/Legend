package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.user.model.LegendUser;
import dev.lbuddyboy.legend.util.UUIDUtils;
import org.bukkit.entity.Player;

public class FocusCommand extends BaseCommand {

    @CommandAlias("focus")
    @CommandCompletion("@players")
    public void focus(Player sender, @Name("player") OnlinePlayer player) {
        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(sender.getUniqueId()).orElse(null);

        if (team == null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        player.getPlayer().sendMessage(CC.translate("&3" + team.getName() + " is focusing you."));

        team.setFocusedPlayer(player.getPlayer().getUniqueId());
        team.sendMessage(CC.translate("&d[Focused] &d" + player.getPlayer().getName() + "&a was focused by &d" + sender.getName()));
    }

    @CommandAlias("unfocus")
    public void unfocus(Player sender) {
        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(sender.getUniqueId()).orElse(null);

        if (team == null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        if (team.getFocusedPlayer() != null) {
            team.sendMessage(CC.translate("&d[Focused] &d" + UUIDUtils.name(team.getFocusedPlayer()) + "&a was unfocused by &d" + sender.getName()));
        }

        team.setFocusedPlayer(null);
    }

}
