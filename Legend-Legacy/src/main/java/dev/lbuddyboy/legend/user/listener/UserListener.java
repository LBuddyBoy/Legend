package dev.lbuddyboy.legend.user.listener;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.model.LegendUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class UserListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        String name = event.getName();

        try {
            LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(uuid);

            user.setName(name);
            user.setJoinedAt(System.currentTimeMillis());

            LegendBukkit.getInstance().getUserHandler().getUserCache().put(uuid, user);
        } catch (Exception e) {
            LegendBukkit.getInstance().getUserHandler().getUserCache().put(uuid, new LegendUser(uuid, name));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (!user.isPlayedBefore()) {
            user.setBalance(LegendBukkit.getInstance().getSettings().getDouble("server.starting-balance"));
            user.setPlayedBefore(true);
        }

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        user.pauseTimer("invincibility");
        user.setPlayTime(user.getPlayTime() + user.getActivePlayTime());
        user.setJoinedAt(-1L);
    }

}
