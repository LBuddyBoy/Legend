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
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LogoutTimer extends PlayerTimer {

    private final Map<UUID, BukkitTask> tasks = new ConcurrentHashMap<>();

    @Override
    public String getId() {
        return "logout";
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo(), from = event.getFrom();

        if (to == null) return;
        if (!isActive(player.getUniqueId())) return;
        if (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ()) return;

        cancel(player, LegendBukkit.getInstance().getLanguage().getString("logout.warp-error.moved"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        if (!isActive(player.getUniqueId())) return;

        cancel(player, LegendBukkit.getInstance().getLanguage().getString("logout.warp-error.damaged"));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cancel(event.getPlayer(), "");
    }

    public void start(Player player) {
        int duration = this.getDuration(player);

        if (this.tasks.containsKey(player.getUniqueId())) {
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("logout.warp-error.already-warping")));
            return;
        }

        this.tasks.put(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                player.setMetadata("safe_disconnect", new FixedMetadataValue(LegendBukkit.getInstance(), true));
                player.kickPlayer(CC.translate("&cYou have been safely disconnected."));
                tasks.remove(player.getUniqueId());
            }
        }.runTaskLater(LegendBukkit.getInstance(), duration * 20L));
        apply(player);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("logout.warping")
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
