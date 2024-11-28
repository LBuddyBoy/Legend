package dev.lbuddyboy.legend.listener;

import dev.lbuddyboy.arrow.ArrowPlugin;
import dev.lbuddyboy.arrow.staffmode.StaffModeConstants;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.events.Capturable;
import dev.lbuddyboy.events.IEvent;
import dev.lbuddyboy.events.alcatraz.Alcatraz;
import dev.lbuddyboy.events.api.event.AlcatrazJoinEvent;
import dev.lbuddyboy.events.api.event.EventTickEvent;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.timer.impl.CombatTimer;
import dev.lbuddyboy.legend.timer.impl.InvincibilityTimer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EventListener implements Listener {

    @EventHandler
    public void onJoin(AlcatrazJoinEvent event) {
        Player player = event.getPlayer();
        Alcatraz alcatraz = event.getAlcatraz();


        if (ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isInStaffMode(player)) {
            player.sendMessage(alcatraz.getEventType().getPrefix() + CC.blend("You can only join events out of staff mode.", "&c", "&7"));
            event.setCancelled(true);
            return;
        }

        if (LegendBukkit.getInstance().getTimerHandler().getTimer(CombatTimer.class).isActive(player.getUniqueId())) {
            player.sendMessage(alcatraz.getEventType().getPrefix() + CC.blend("You cannot join events while spawn tagged.", "&c", "&7"));
            event.setCancelled(true);
            return;
        }

        if (LegendBukkit.getInstance().getTimerHandler().getTimer(InvincibilityTimer.class).isActive(player.getUniqueId())) {
            player.sendMessage(alcatraz.getEventType().getPrefix() + CC.blend("You cannot join events while in your pvp timer is active.", "&c", "&7"));
            event.setCancelled(true);
            return;
        }

        boolean inventoryClean = true;
/*        for (ItemStack stack : player.getInventory()) {
            if (stack == null || stack.getType() == Material.AIR) continue;

            for (IAbilityItem item : AbilityItems.getInstance().getAbilityItems().values()) {
                if (!item.isAbilityItem(stack)) continue;

                inventoryClean = false;
                player.sendMessage(iEvent.getEventType().getPrefix() + CC.blend("You have a disallowed item: ", "&c", "&7") + CC.translate(item.getDisplayName()));
            }

        }*/

    }

    @EventHandler
    public void onTick(EventTickEvent event) {
        Capturable capturable = event.getCapturable();
        Player player = event.getPlayer();
        InvincibilityTimer invincibilityTimer = LegendBukkit.getInstance().getTimerHandler().getTimer(InvincibilityTimer.class);

        if (!invincibilityTimer.isActive(player.getUniqueId()) && !player.hasMetadata(StaffModeConstants.VANISH_META_DATA) && player.getGameMode() == GameMode.SURVIVAL) return;

        event.setCancelled(true);
        player.sendMessage(CC.translate("&cYou are in an invalid state to capture this event."));
    }

}
