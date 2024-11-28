package dev.lbuddyboy.legend.features.kitmap.listener;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.model.LegendUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillStreakListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity(), killer = victim.getKiller();

        if (killer == null) return;

        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(killer.getUniqueId());

        LegendBukkit.getInstance().getKitMapHandler().getKillStreaks()
                .stream()
                .filter(k -> k.getNeededKills() == user.getCurrentKillStreak())
                .forEach(killStreak -> {
                    String message = LegendBukkit.getInstance().getLanguage().getString("killstreak.broadcast")
                            .replaceAll("%player%", killer.getName())
                            .replaceAll("%killstreak%", killStreak.getName())
                            .replaceAll("%kills%", String.valueOf(killStreak.getNeededKills()));

                    killStreak.getCommands().forEach(s -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s
                            .replaceAll("%player%", killer.getName())
                    ));
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(CC.translate(message));
                    }
                });
    }

}
