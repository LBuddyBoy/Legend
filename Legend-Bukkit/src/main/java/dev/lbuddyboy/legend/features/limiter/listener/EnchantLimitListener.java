package dev.lbuddyboy.legend.features.limiter.listener;

import dev.lbuddyboy.legend.LegendBukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.InventoryView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantLimitListener implements Listener {

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        Player player = event.getEnchanter();

        InventoryView view = player.openEnchanting(player.getLocation(), true);

    }

    @EventHandler
    public void onPreEnchant(PrepareItemEnchantEvent event) {

    }

    public Map<Enchantment, Integer> getEnchantLimits() {
        ConfigurationSection section = LegendBukkit.getInstance().getLimiterHandler().getConfig().getConfigurationSection("enchant-limits");
        Map<Enchantment, Integer> enchants = new HashMap<>();

        for (String key : section.getKeys(false)) {
            Enchantment enchantment = Enchantment.getByName(key);
            if (enchantment == null) continue;

            enchants.put(enchantment, section.getInt(key));
        }

        return enchants;
    }

}
