package dev.lbuddyboy.legend.features.limiter.listener;

import dev.lbuddyboy.legend.LegendBukkit;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.inventory.view.CraftEnchantmentView;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.EnchantmentView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class EnchantLimitListener implements Listener {

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        Map<Enchantment, Integer> enchants = new HashMap<>();

        event.getEnchantsToAdd().forEach((k, v) -> {
            if (isDisabled(k)) {
                return;
            }
            if (v > getLimit(k)) {
                enchants.put(k, getLimit(k));
                return;
            }

            enchants.put(k, v);
        });

        event.getEnchantsToAdd().clear();
        event.getEnchantsToAdd().putAll(enchants);
    }

    @EventHandler
    public void onAnvil(PrepareAnvilEvent event) {
        ItemStack result = event.getResult();
        if (result == null) return;

        Map<Enchantment, Integer> enchants = new HashMap<>();

        result.getEnchantments().forEach((k, v) -> {
            if (isDisabled(k)) {
                return;
            }
            if (v > getLimit(k)) {
                enchants.put(k, getLimit(k));
                return;
            }

            enchants.put(k, v);
        });

        result.removeEnchantments();
        result.addUnsafeEnchantments(enchants);
    }

    @EventHandler
    public void onPreEnchant(PrepareItemEnchantEvent event) {
        EnchantmentOffer[] offers = event.getOffers();

        for (int index = 0; index < offers.length; index++) {
            EnchantmentOffer offer = offers[index];

            if (offer == null) {
                offers[index] = new EnchantmentOffer(Enchantment.UNBREAKING, index + 1, index + 1);
                continue;
            }

            if (isDisabled(offer.getEnchantment())) {
                EnchantmentOffer newOffer = this.roll(event.getItem(), offer.getCost());

                if (newOffer == null) {
                    LegendBukkit.getInstance().getLogger().warning("[Enchant Limiter] Couldn't find a replacement for " + offer.getEnchantment().getKey().getNamespace() + " " + offer.getEnchantmentLevel() + " (Cost: " + offer.getCost() + ")");
                    offers[index] = new EnchantmentOffer(Enchantment.UNBREAKING, index + 1, index + 1);
                    continue;
                }

                offers[index] = newOffer;
                continue;
            }

            if (offer.getEnchantmentLevel() > getLimit(offer.getEnchantment())) {
                offer.setEnchantmentLevel(getLimit(offer.getEnchantment()));
            }
        }
    }

    public EnchantmentOffer roll(ItemStack stack, int cost) {
        List<Enchantment> enchantments = getEnchantLimits().keySet().stream().toList().stream().filter(enchantment -> enchantment.canEnchantItem(stack) && getLimit(enchantment) <= cost).toList();

        if (enchantments.isEmpty()) {
            return null;
        }

        Enchantment enchantment = enchantments.get(ThreadLocalRandom.current().nextInt(enchantments.size()));

        return new EnchantmentOffer(
                enchantment,
                Math.min(cost, getLimit(enchantment)),
                cost
        );
    }

    public boolean isDisabled(Enchantment enchantment) {
        return getLimit(enchantment) == -1;
    }

    public int getLimit(Enchantment enchantment) {
        return getEnchantLimits().getOrDefault(enchantment, -1);
    }

    public Map<Enchantment, Integer> getEnchantLimits() {
        ConfigurationSection section = LegendBukkit.getInstance().getLimiterHandler().getConfig().getConfigurationSection("enchant-limits");
        Map<Enchantment, Integer> enchants = new HashMap<>();

        for (String key : section.getKeys(false)) {
            Enchantment enchantment = Registry.ENCHANTMENT.match(key);
            if (enchantment == null) continue;

            enchants.put(enchantment, section.getInt(key));
        }

        return enchants;
    }

}
