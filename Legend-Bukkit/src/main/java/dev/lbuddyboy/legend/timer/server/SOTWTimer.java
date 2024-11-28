package dev.lbuddyboy.legend.timer.server;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
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

        if (this.enabledPlayers.contains(victim.getUniqueId()) && this.enabledPlayers.contains(damager.getUniqueId())) return;

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

    public boolean isEnabled(UUID playerUUID) {
        return this.enabledPlayers.contains(playerUUID);
    }

    public boolean isEnabled(Player player) {
        return isEnabled(player.getUniqueId());
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
                LegendBukkit.getInstance().getLanguage().getStringList("sotw.ended").forEach(s -> Bukkit.broadcastMessage(CC.translate(s)));
            }

        }.runTaskLater(LegendBukkit.getInstance(), 20 * (duration / 1000L));
    }

    @Override
    public void end() {
        super.end();

        if (this.endTask != null) {
            this.endTask.cancel();
            this.endTask = null;
        }
    }
}
