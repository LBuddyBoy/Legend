package dev.lbuddyboy.legend.features.limiter.listener;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.Objects;

public class PotionLimitListener implements Listener {

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (item == null) return;
        if (item.getType() != Material.POTION) return;
        if (!(item.getItemMeta() instanceof PotionMeta meta)) return;
        if (!getDisallowedPotions().contains(meta.getBasePotionType())) return;

        event.setCancelled(true);
        player.sendMessage(CC.translate("&cThat potion is currently disabled."));
    }

    @EventHandler
    public void onSplash(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof ThrownPotion thrownPotion)) return;
        ItemStack item = thrownPotion.getItem();
        if (!(item.getItemMeta() instanceof PotionMeta meta)) return;
        if (!getDisallowedPotions().contains(meta.getBasePotionType())) return;

        event.setCancelled(true);
        if (thrownPotion.getShooter() instanceof Player) {
            ((Player) thrownPotion.getShooter()).sendMessage(CC.translate("&cThat potion is currently disabled."));
        }
    }

    public List<PotionType> getDisallowedPotions() {
        return LegendBukkit.getInstance().getLimiterHandler().getConfig().getStringList("potion-limits").stream().map(Registry.POTION::match).filter(Objects::nonNull).toList();
    }

}
