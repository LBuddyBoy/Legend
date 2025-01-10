package dev.lbuddyboy.legend.listener;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class ModifierListener implements Listener {

    @EventHandler
    public void onDamage(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getItem() != null) return;
        if (ThreadLocalRandom.current().nextInt(0, 100) <= 50) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(PlayerItemDamageEvent event) {
        if (ThreadLocalRandom.current().nextInt(0, 100) <= 50) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBow(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getDamager() instanceof Arrow arrow)) return;

        event.setDamage(event.getDamage() * SettingsConfig.MODIFIERS_BOW_DAMAGE.getDouble());
    }

}
