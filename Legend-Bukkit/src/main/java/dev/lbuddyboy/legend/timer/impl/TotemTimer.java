package dev.lbuddyboy.legend.timer.impl;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.timer.PlayerTimer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class TotemTimer extends PlayerTimer {

    @Override
    public String getId() {
        return "totem";
    }

    @EventHandler
    public void onConsume(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (!isActive(player.getUniqueId())) {
            apply(player);
            return;
        }

        event.setCancelled(true);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("totem-cooldown")
                .replaceAll("%cooldown%", getRemainingSeconds(player.getUniqueId()))
        ));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        remove(event.getEntity().getUniqueId());
    }

}
