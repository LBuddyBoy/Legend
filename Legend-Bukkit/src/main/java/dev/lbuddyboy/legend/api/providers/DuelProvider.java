package dev.lbuddyboy.legend.api.providers;

import dev.lbuddyboy.arrow.ArrowPlugin;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.duels.DuelConstants;
import dev.lbuddyboy.duels.api.DuelEndEvent;
import dev.lbuddyboy.duels.api.DuelLeaveEvent;
import dev.lbuddyboy.duels.api.DuelRequestAcceptEvent;
import dev.lbuddyboy.duels.api.DuelRequestSendEvent;
import dev.lbuddyboy.duels.model.DuelRequest;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.settings.Setting;
import dev.lbuddyboy.legend.timer.impl.CombatTimer;
import dev.lbuddyboy.legend.timer.impl.InvincibilityTimer;
import dev.lbuddyboy.legend.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

public class DuelProvider implements Listener {

    public DuelProvider() {
        Bukkit.getPluginManager().registerEvents(this, LegendBukkit.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDuelInviteAccept(DuelRequestAcceptEvent event) {
        Player target = event.getPlayer();
        DuelRequest request = event.getDuelRequest();
        UUID senderUUID = request.getSender();
        Player sender = Bukkit.getPlayer(senderUUID);

        if (sender == null) {
            event.setCancelled(true);
            target.sendMessage(DuelConstants.PREFIX_ERROR + CC.blend("Duel couldn't start because " + UUIDUtils.name(senderUUID) + " logged out.", "&c", "&7"));
            return;
        }

        if (ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isInStaffMode(sender)) {
            sender.sendMessage(DuelConstants.PREFIX_ERROR + CC.blend("You can only duel out of staff mode.", "&c", "&7"));
            event.setCancelled(true);
            return;
        }

        if (LegendBukkit.getInstance().getTimerHandler().getTimer(CombatTimer.class).isActive(sender.getUniqueId())) {
            sender.sendMessage(DuelConstants.PREFIX_ERROR + CC.blend("You can only duel when you aren't combat tagged.", "&c", "&7"));
            event.setCancelled(true);
            return;
        }

        if (LegendBukkit.getInstance().getTimerHandler().getTimer(InvincibilityTimer.class).isActive(sender.getUniqueId())) {
            sender.sendMessage(DuelConstants.PREFIX_ERROR + CC.blend("You can only duel when you don't have pvp timer.", "&c", "&7"));
            event.setCancelled(true);
            return;
        }

        if (ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isInStaffMode(target)) {
            sender.sendMessage(DuelConstants.PREFIX_ERROR + CC.blend("Duel couldn't start because " + target.getName() + " is in staff mode.", "&c", "&7"));
            target.sendMessage(DuelConstants.PREFIX_ERROR + CC.blend("You can only duel out of staff mode.", "&c", "&7"));
            event.setCancelled(true);
            return;
        }

        if (LegendBukkit.getInstance().getTimerHandler().getTimer(CombatTimer.class).isActive(target.getUniqueId())) {
            sender.sendMessage(DuelConstants.PREFIX_ERROR + CC.blend("Duel couldn't start because " + target.getName() + " is combat tagged.", "&c", "&7"));
            target.sendMessage(DuelConstants.PREFIX_ERROR + CC.blend("You can only duel when you aren't combat tagged.", "&c", "&7"));
            event.setCancelled(true);
            return;
        }

        if (LegendBukkit.getInstance().getTimerHandler().getTimer(InvincibilityTimer.class).isActive(target.getUniqueId())) {
            sender.sendMessage(DuelConstants.PREFIX_ERROR + CC.blend("Duel couldn't start because " + target.getName() + " has pvp timer.", "&c", "&7"));
            target.sendMessage(DuelConstants.PREFIX_ERROR + CC.blend("You can only duel when you don't have pvp timer.", "&c", "&7"));
            event.setCancelled(true);
            return;
        }

        Player playerA = Bukkit.getPlayer(event.getDuelRequest().getSender());
        Player playerB = Bukkit.getPlayer(event.getDuelRequest().getTarget());

        if (playerA != null) {
            playerA.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20.0D);
            playerA.setHealth(20.0D);
        }

        if (playerB != null) {
            playerB.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20.0D);
            playerB.setHealth(20.0D);
        }

    }

    @EventHandler
    public void onEnd(DuelLeaveEvent event) {
        Player player = event.getPlayer();

        LegendBukkit.getInstance().getTimerHandler().getTimer(CombatTimer.class).remove(player.getUniqueId());
    }

    @EventHandler
    public void onDuelInviteSend(DuelRequestSendEvent event) {
        Player sender = event.getSender();
        Player target = event.getTarget();

        if (!Setting.DUELS.isToggled(target.getUniqueId())) {
            sender.sendMessage(DuelConstants.PREFIX_ERROR + CC.blend("That player is not accepting duels right now.", "&c", "&7"));
            event.setCancelled(true);
            return;
        }

        if (ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isInStaffMode(sender)) {
            sender.sendMessage(DuelConstants.PREFIX_ERROR + CC.blend("You can only duel out of staff mode.", "&c", "&7"));
            event.setCancelled(true);
            return;
        }

        if (LegendBukkit.getInstance().getTimerHandler().getTimer(CombatTimer.class).isActive(target.getUniqueId())) {
            sender.sendMessage(DuelConstants.PREFIX_ERROR + CC.blend("You cannot duel while spawn tagged.", "&c", "&7"));
            event.setCancelled(true);
            return;
        }

        if (LegendBukkit.getInstance().getTimerHandler().getTimer(InvincibilityTimer.class).isActive(target.getUniqueId())) {
            sender.sendMessage(DuelConstants.PREFIX_ERROR + CC.blend("You cannot duel while in your pvp timer is active.", "&c", "&7"));
            event.setCancelled(true);
            return;
        }

    }
}
