package dev.lbuddyboy.legend.listener;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.model.ChatMode;
import dev.lbuddyboy.legend.user.model.LegendUser;
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
    }

}
