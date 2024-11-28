package dev.lbuddyboy.legend.user.listener;

import dev.lbuddyboy.events.util.PlayerUtil;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.user.model.LegendUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (!user.isPlayedBefore()) {
            user.applyTimer("invincibility", 60_000L * 60);
            user.setBalance(LegendBukkit.getInstance().getSettings().getDouble("server.starting-balance"));
            user.setPlayedBefore(true);
        }

        if (user.isTimerActive("invincibility")) {
            user.resumeTimer("invincibility");
            if (TeamType.SPAWN.appliesAt(player.getLocation())) {
                user.pauseTimer("invincibility");
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (user.isTimerActive("invincibility")) {
            user.pauseTimer("invincibility");
        }

        user.setPlayTime(user.getPlayTime() + user.getActivePlayTime());
        user.setJoinedAt(-1L);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (LegendBukkit.getInstance().getSettings().getBoolean("server.deathbans", true)) return;
        if (LegendBukkit.getInstance().getSettings().getBoolean("kitmap.enabled", true)) return;

        user.applyTimer("invincibility", 60_000L * 30);
        if (TeamType.SPAWN.appliesAt(event.getRespawnLocation())) {
            user.pauseTimer("invincibility");
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        LegendUser victimUser = LegendBukkit.getInstance().getUserHandler().getUser(victim.getUniqueId());

        victimUser.setDeaths(victimUser.getDeaths() + 1);
        victimUser.setKillStreak(0);
        victimUser.removeTimer("invincibility");

        if (victim.getKiller() == null) return;

        LegendUser killerUser = LegendBukkit.getInstance().getUserHandler().getUser(victim.getKiller().getUniqueId());

        killerUser.setKillStreak(killerUser.getCurrentKillStreak() + 1);
        killerUser.setKills(killerUser.getKills() + 1);
        if (killerUser.getCurrentKillStreak() > killerUser.getHighestKillStreak()) killerUser.setHighestKillStreak(killerUser.getCurrentKillStreak());

    }

}
