package dev.lbuddyboy.legend.timer.impl;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.timer.PlayerTimer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HomeTimer extends PlayerTimer {

    private final Map<UUID, BukkitTask> tasks = new ConcurrentHashMap<>();

    @Override
    public String getId() {
        return "home";
    }

    @Override
    public int getDuration(Player player) {
        Team teamAt = LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getTeam(player.getLocation()).orElse(null);
        if (teamAt != null) {
            if (teamAt.getTeamType() == TeamType.SPAWN) return 0;
            if (teamAt.isMember(player.getUniqueId())) return getDuration();

            return getEnemyDuration();
        }

        return getDuration();
    }

    public int getEnemyDuration() {
        return LegendBukkit.getInstance().getTimerHandler().getConfig().getInt(getId() + ".duration-enemy");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo(), from = event.getFrom();

        if (to == null) return;
        if (!isActive(player.getUniqueId())) return;
        if (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ()) return;

        cancel(player, LegendBukkit.getInstance().getLanguage().getString("team.home.warp-error.moved"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        if (!isActive(player.getUniqueId())) return;

        cancel(player, LegendBukkit.getInstance().getLanguage().getString("team.home.warp-error.damaged"));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cancel(event.getPlayer(), "");
    }

    public void start(Player player) {
        CombatTimer combatTimer = LegendBukkit.getInstance().getTimerHandler().getTimer(CombatTimer.class);
        InvincibilityTimer invincibilityTimer = LegendBukkit.getInstance().getTimerHandler().getTimer(InvincibilityTimer.class);
        Location home = LegendBukkit.getInstance().getTeamHandler().getTeam(player).map(Team::getHome).orElse(null);
        int duration = this.getDuration(player);

        if (this.tasks.containsKey(player.getUniqueId())) {
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.home.warp-error.already-warping")));
            return;
        }

        if (!LegendBukkit.getInstance().getTeamHandler().getTeam(player).isPresent()) {
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        if (combatTimer.isActive(player.getUniqueId())) {
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.home.warp-error.combat-tagged")));
            return;
        }

        if (invincibilityTimer.isActive(player.getUniqueId())) {
            player.sendMessage(CC.translate("<blend:&4;&c>&lWARNING!</>&c If you are caught trapping anyone in your claim with Invincibility you will be punished."));
        }

        if (home == null) {
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.home.none")));
            return;
        }

        this.tasks.put(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                Optional<Team> teamOpt = LegendBukkit.getInstance().getTeamHandler().getTeam(player);

                if (teamOpt.isPresent()) {
                    player.teleport(teamOpt.get().getHome());
                    tasks.remove(player.getUniqueId());
                    return;
                }

                player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.home.warp-error.no-team")));
            }
        }.runTaskLater(LegendBukkit.getInstance(), duration * 20L));
        apply(player);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.home.warping")
                .replaceAll("%duration%", APIConstants.formatNumber(duration))
        ));
    }

    public void cancel(Player player, String message) {
        if (this.tasks.containsKey(player.getUniqueId())) {
            this.tasks.get(player.getUniqueId()).cancel();
        }
        this.tasks.remove(player.getUniqueId());
        if (!message.isEmpty()) player.sendMessage(CC.translate(message));
        remove(player.getUniqueId());
    }

}
