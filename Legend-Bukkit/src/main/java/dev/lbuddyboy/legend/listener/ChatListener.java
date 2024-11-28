package dev.lbuddyboy.legend.listener;

import dev.lbuddyboy.commons.component.FancyBuilder;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.user.model.ChatMode;
import dev.lbuddyboy.legend.user.model.LegendUser;
import dev.lbuddyboy.legend.util.BukkitUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        String message = event.getMessage();
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());
        ChatMode chatMode = user.getChatMode();

        if (chatMode == ChatMode.TEAM || chatMode == ChatMode.ALLY) {
            if (!LegendBukkit.getInstance().getTeamHandler().getTeam(player).isPresent()) {
                user.setChatMode(ChatMode.PUBLIC);
                chatMode = ChatMode.PUBLIC;
            }
        }

        event.setCancelled(true);

        for (ChatMode mode : ChatMode.values()) {
            if (message.charAt(0) == mode.getIdentifier()) chatMode = mode;
        }

        List<Player> recipients = chatMode.getRecipientFunction().apply(player);

        for (Player recipient : recipients) {
            recipient.sendMessage(chatMode.getFormatFunction().apply(event, recipient));
        }

        if (chatMode != ChatMode.PUBLIC) {
            Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(player).orElse(null);
            if (team == null) return;

            String msg = "<blend:&6;&e>&lTEAM SPY</> &7[" + team.getName() + "] &e" + player.getName() + " &7» &f" + message;

            BukkitUtil.getStaffPlayers().forEach(p -> {
                LegendUser staffUser = LegendBukkit.getInstance().getUserHandler().getUser(p.getUniqueId());
                if (!staffUser.isTeamSpy()) return;

                p.sendMessage(CC.translate(msg
                ));
            });
        }


    }

}
