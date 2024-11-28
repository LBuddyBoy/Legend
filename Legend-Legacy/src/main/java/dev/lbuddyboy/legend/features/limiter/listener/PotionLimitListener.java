package dev.lbuddyboy.legend.listener;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PotionLimitListener implements Listener {

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (item == null) return;
        if (item.getType() != Material.POTION) return;

        short durability = item.getDurability();
        if (!getDisallowedPotions().contains((int)durability)) return;

        event.setCancelled(true);
        player.sendMessage(CC.translate("&cThat potion is currently disabled."));
    }

    @EventHandler
    public void onSplash(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof ThrownPotion)) return;
        ThrownPotion thrownPotion = (ThrownPotion) event.getEntity();
        ItemStack item = thrownPotion.getItem();
        if (item == null) return;

        short durability = item.getDurability();
        if (!getDisallowedPotions().contains((int)durability)) return;

        event.setCancelled(true);
        if (thrownPotion.getShooter() instanceof Player) {
            ((Player) thrownPotion.getShooter()).sendMessage(CC.translate("&cThat potion is currently disabled."));
        }
    }

    public List<Integer> getDisallowedPotions() {
        return LegendBukkit.getInstance().getLimiter().getIntegerList("potion-limits");
    }

}
