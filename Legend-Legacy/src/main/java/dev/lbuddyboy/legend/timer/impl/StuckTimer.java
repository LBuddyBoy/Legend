package dev.lbuddyboy.legend.timer.impl;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.team.model.claim.Claim;
import dev.lbuddyboy.legend.timer.PlayerTimer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StuckTimer extends PlayerTimer {

    private final Map<UUID, BukkitTask> tasks = new ConcurrentHashMap<>();
    private final Map<UUID, Location> locations = new ConcurrentHashMap<>();

    @Override
    public String getId() {
        return "stuck";
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo(), from = event.getFrom();

        if (to == null) return;
        if (!isActive(player.getUniqueId())) return;
        if (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ()) return;
        if (!this.locations.containsKey(player.getUniqueId())) return;
        if (to.distance(this.locations.get(player.getUniqueId())) < 5) return;

        cancel(player, LegendBukkit.getInstance().getLanguage().getString("team.stuck.warp-error.moved"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        if (!isActive(player.getUniqueId())) return;

        cancel(player, LegendBukkit.getInstance().getLanguage().getString("team.stuck.warp-error.damaged"));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cancel(event.getPlayer(), "");
    }

    public void start(Player player) {
        CombatTimer combatTimer = (CombatTimer) LegendBukkit.getInstance().getTimerHandler().getTimer(CombatTimer.class);
        int duration = this.getDuration();

        if (this.tasks.containsKey(player.getUniqueId())) {
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.stuck.warp-error.already-warping")));
            return;
        }

        Claim claim = LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getClaim(player.getLocation());

        if (claim == null) {
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.stuck.warp-error.no-claim")));
            return;
        }

        this.tasks.put(player.getUniqueId(), new BukkitRunnable() {

            @Override
            public void run() {
                Block corner = player.getWorld().getHighestBlockAt(claim.getBounds().fourCorners()[0].getLocation());
                List<BlockFace> faces = Arrays.asList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);

                for (BlockFace face : faces) {
                    if (LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getClaim(corner.getRelative(face).getLocation()) == null) {
                        tasks.remove(player.getUniqueId());
                        player.teleport(corner.getRelative(face).getLocation());
                        return;
                    }
                }

                player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.stuck.warp-error.failed")));
            }
        }.runTaskLater(LegendBukkit.getInstance(), duration * 20L));
        this.locations.put(player.getUniqueId(), player.getLocation().clone());
        apply(player);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.stuck.warping")
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
        this.locations.remove(player.getUniqueId());
    }

}
