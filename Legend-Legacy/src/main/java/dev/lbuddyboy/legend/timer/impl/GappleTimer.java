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

public class CrappleTimer extends PlayerTimer {

    @Override
    public String getId() {
        return "crapple";
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        if (item == null) return;
        if (item.getType() != Material.GOLDEN_APPLE && item.getDurability() != 0) return;
        if (!isActive(player.getUniqueId())) {
            apply(player);
            return;
        }

        event.setCancelled(true);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("crapple-cooldown")
                .replaceAll("%cooldown%", getRemainingSeconds(player.getUniqueId()))
        ));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (item.getType() != Material.ENDER_PEARL) return;
        if (!isActive(player.getUniqueId())) {
            event.setUseItemInHand(Event.Result.DENY);
            player.getInventory().setItemInHand(ItemUtils.takeItem(item));
            player.launchProjectile(EnderPearl.class);
            apply(player.getUniqueId());
            return;
        }

        event.setUseItemInHand(Event.Result.DENY);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("enderpearl-cooldown")
                .replaceAll("%cooldown%", getRemainingSeconds(player.getUniqueId()))
        ));
    }

}
