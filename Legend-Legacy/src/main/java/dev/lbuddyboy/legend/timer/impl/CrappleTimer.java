package dev.lbuddyboy.legend.timer.impl;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.timer.PlayerTimer;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EnderPearlTimer extends PlayerTimer {

    @Override
    public String getId() {
        return "enderpearl";
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
