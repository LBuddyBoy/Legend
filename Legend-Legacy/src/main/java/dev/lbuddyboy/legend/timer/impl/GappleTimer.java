package dev.lbuddyboy.legend.timer.impl;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.timer.PlayerTimer;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class GappleTimer extends PlayerTimer {

    @Override
    public String getId() {
        return "gapple";
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        if (item == null) return;
        if (item.getType() != Material.GOLDEN_APPLE) return;
        if (item.getDurability() != 1) return;

        if (!isActive(player.getUniqueId())) {
            apply(player);
            return;
        }

        event.setCancelled(true);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("gapple-cooldown")
                .replaceAll("%cooldown%", getRemainingSeconds(player.getUniqueId()))
        ));
    }

}
