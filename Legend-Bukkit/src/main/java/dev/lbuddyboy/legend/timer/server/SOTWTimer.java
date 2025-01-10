package dev.lbuddyboy.legend.timer.server;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.api.PlayerClaimChangeEvent;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.timer.ServerTimer;
import dev.lbuddyboy.legend.util.BukkitUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class SOTWTimer extends ServerTimer {

    private BukkitTask endTask;
    private final List<UUID> enabledPlayers = new ArrayList<>();

    @Override
    public String getId() {
        return "sotw";
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!isActive()) return;

        Player victim = (Player) event.getEntity();
        Player damager = BukkitUtil.getDamager(event);

        if (damager == null) {
            if (this.enabledPlayers.contains(victim.getUniqueId())) return;

            event.setCancelled(true);
            return;
        }

        if (this.enabledPlayers.contains(victim.getUniqueId()) && this.enabledPlayers.contains(damager.getUniqueId()))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLoss(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (!isActive()) return;
        if (this.enabledPlayers.contains(player.getUniqueId())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (!isActive()) return;
        if (!TeamType.SPAWN.appliesAt(player.getLocation())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onClaimChange(PlayerClaimChangeEvent event) {
        Team toTeam = event.getToTeam(), fromTeam = event.getFromTeam();
        Player player = event.getPlayer();

        if (!isActive()) return;

        if (fromTeam != null && (toTeam == null || toTeam.getTeamType() != TeamType.SPAWN)) {
            for (Player other : Bukkit.getOnlinePlayers()) {
                other.showPlayer(LegendBukkit.getInstance(), player);
            }
        }

        if (toTeam != null && toTeam.getTeamType() == TeamType.SPAWN) {
            for (Player other : Bukkit.getOnlinePlayers()) {
                other.hidePlayer(LegendBukkit.getInstance(), player);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!isActive()) return;

        for (Player other : Bukkit.getOnlinePlayers()) {
            if (TeamType.SPAWN.appliesAt(player.getLocation())) {
                other.hidePlayer(LegendBukkit.getInstance(), player);
                continue;
            }
            if (!TeamType.SPAWN.appliesAt(other.getLocation())) continue;

            player.hidePlayer(LegendBukkit.getInstance(), other);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

    }

    public boolean isEnabled(UUID playerUUID) {
        return this.enabledPlayers.contains(playerUUID);
    }

    public boolean isEnabled(Player player) {
        return isActive() && isEnabled(player.getUniqueId());
    }

    @Override
    public void start(long duration) {
        super.start(duration);

        if (this.endTask != null) {
            this.endTask.cancel();
            this.endTask = null;
        }

        this.enabledPlayers.clear();
        this.endTask = new BukkitRunnable() {

            @Override
            public void run() {
                if (isActive()) return;

                LegendBukkit.getInstance().getLanguage().getStringList("sotw.ended").forEach(s -> Bukkit.broadcastMessage(CC.translate(s)));
                end();
            }

        }.runTaskTimer(LegendBukkit.getInstance(), 20, 20L);
    }

    @Override
    public void end() {
        super.end();

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!TeamType.SPAWN.appliesAt(other.getLocation())) continue;

                player.showPlayer(LegendBukkit.getInstance(), other);
            }
        }

        if (this.endTask != null) {
            this.endTask.cancel();
            this.endTask = null;
        }
    }
}
