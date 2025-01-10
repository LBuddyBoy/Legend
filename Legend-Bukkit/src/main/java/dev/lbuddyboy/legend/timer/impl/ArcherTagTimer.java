package dev.lbuddyboy.legend.timer.impl;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.classes.impl.ArcherClass;
import dev.lbuddyboy.legend.timer.PlayerTimer;
import dev.lbuddyboy.legend.util.BukkitUtil;
import dev.lbuddyboy.legend.util.NameTagUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

public class ArcherTagTimer extends PlayerTimer {

    @Override
    public String getId() {
        return "archer-tag";
    }

    @Override
    public void apply(UUID playerUUID, int seconds) {
        super.apply(playerUUID, seconds);

        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

    }

    @Override
    public void apply(Player player) {
        super.apply(player);

    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player victim = (Player) event.getEntity();
        Player shooter = BukkitUtil.getDamager(event);

        if (shooter == null) return;
        if (!isActive(victim.getUniqueId())) return;

        ArcherClass archerClass = LegendBukkit.getInstance().getClassHandler().getClass(ArcherClass.class);
        if (archerClass == null) return;

        event.setDamage(event.getDamage() * archerClass.getConfig().getDouble("settings.archer-tag-multiplier"));
    }

}
