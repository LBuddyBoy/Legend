package dev.lbuddyboy.legend.features.limiter.listener;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

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
        return LegendBukkit.getInstance().getLimiterHandler().getConfig().getIntegerList("potion-limits");
    }

}
