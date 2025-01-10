package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.google.common.collect.Sets;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.user.model.ChatMode;
import dev.lbuddyboy.legend.util.BukkitUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

@CommandAlias("teamlocation|tl|tloc|tlocation|teamloc|teaml")
public class TeamLocationCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(sender).orElse(null);

        if (team == null) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        List<Player> recipients = ChatMode.TEAM.getRecipientFunction().apply(sender);
        Location location = sender.getLocation();
        String locationString = location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + " " + BukkitUtil.getWorldName(sender.getWorld());

        for (Player recipient : recipients) {
            recipient.sendMessage(ChatMode.TEAM.getFormatFunction().apply(new AsyncPlayerChatEvent(true, sender, locationString, Sets.newConcurrentHashSet()), recipient));
        }

    }

}
