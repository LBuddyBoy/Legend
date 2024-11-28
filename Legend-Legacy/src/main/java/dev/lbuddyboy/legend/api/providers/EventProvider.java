package dev.lbuddyboy.legend.api.providers;

import dev.lbuddyboy.arrow.ArrowPlugin;
import dev.lbuddyboy.arrow.staffmode.StaffModeConstants;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.events.Capturable;
import dev.lbuddyboy.events.IEvent;
import dev.lbuddyboy.events.api.IEventAPI;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.timer.impl.CombatTimer;
import dev.lbuddyboy.legend.timer.impl.InvincibilityTimer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project Samurai
 * @file dev.lbuddyboy.samurai.api.impl
 * @since 5/10/2024
 */

public class EventProvider implements IEventAPI {

    @Override
    public boolean canJoinEvent(Player player, IEvent iEvent) {
        if (ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isInStaffMode(player)) {
            player.sendMessage(iEvent.getEventType().getPrefix() + CC.blend("You can only join events out of staff mode.", "&c", "&7"));
            return false;
        }

        if (LegendBukkit.getInstance().getTimerHandler().getTimer(CombatTimer.class).isActive(player.getUniqueId())) {
            player.sendMessage(iEvent.getEventType().getPrefix() + CC.blend("You cannot join events while spawn tagged.", "&c", "&7"));
            return false;
        }

        if (LegendBukkit.getInstance().getTimerHandler().getTimer(InvincibilityTimer.class).isActive(player.getUniqueId())) {
            player.sendMessage(iEvent.getEventType().getPrefix() + CC.blend("You cannot join events while in your pvp timer is active.", "&c", "&7"));
            return false;
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

        return inventoryClean;
    }

    @Override
    public List<Player> getMembers(Player player) {
        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(player).orElse(null);

        if (team != null) {
            return team.getOnlinePlayers().stream().filter(p -> !p.equals(player)).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    public List<Player> getMembers(UUID playerUUID) {
        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(playerUUID).orElse(null);

        if (team != null) {
            return team.getOnlinePlayers().stream().filter(p -> !p.getUniqueId().equals(playerUUID)).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    public boolean canCapture(Player player, Capturable capturable) {
        InvincibilityTimer invincibilityTimer = LegendBukkit.getInstance().getTimerHandler().getTimer(InvincibilityTimer.class);

        return !invincibilityTimer.isActive(player.getUniqueId()) && !player.hasMetadata(StaffModeConstants.VANISH_META_DATA) && player.getGameMode() == GameMode.SURVIVAL;
    }
}
