package dev.lbuddyboy.legend.listener;

import dev.lbuddyboy.legend.util.PlayerUtil;
import dev.lbuddyboy.legend.util.StatTracker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class StatTrackListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onStatTrack(PlayerDeathEvent event) {
        List<ItemStack> items = new ArrayList<>();
        Player victim = event.getEntity();
        Player killer = PlayerUtil.getLastDamageCause(victim);

        if (killer != null) {
            ItemStack hand = killer.getInventory().getItemInMainHand();

            if (StatTracker.shouldBeKillTracked(hand)) {
                killer.getInventory().setItemInMainHand(StatTracker.updateLore(hand, victim, killer));
            }
        }

        for (ItemStack drop : event.getDrops()) {
            if (!StatTracker.shouldBeDeathTracked(drop)) {
                items.add(drop);
                continue;
            }

            items.add(StatTracker.updateLore(drop, victim, killer));
        }

        event.getDrops().clear();
        event.getDrops().addAll(items);
    }

}
