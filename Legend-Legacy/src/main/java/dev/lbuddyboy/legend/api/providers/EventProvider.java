package dev.lbuddyboy.samurai.api.impl;

import dev.lbuddyboy.abilityitems.AbilityItems;
import dev.lbuddyboy.abilityitems.IAbilityItem;
import dev.lbuddyboy.arrow.ArrowPlugin;
import dev.lbuddyboy.events.Capturable;
import dev.lbuddyboy.events.IEvent;
import dev.lbuddyboy.events.api.IEventAPI;
import dev.lbuddyboy.events.citadel.Citadel;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.user.SamuraiUser;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project Samurai
 * @file dev.lbuddyboy.samurai.api.impl
 * @since 5/10/2024
 */

public class SamuraiEventProvider implements IEventAPI {

    @Override
    public boolean canJoinEvent(Player player, IEvent iEvent) {
        if (ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isInStaffMode(player)) {
            player.sendMessage(iEvent.getEventType().getPrefix() + CC.blend("You can only join events out of staff mode.", "&c", "&7"));
            return false;
        }

        if (SpawnTagHandler.isTagged(player)) {
            player.sendMessage(iEvent.getEventType().getPrefix() + CC.blend("You cannot join events while spawn tagged.", "&c", "&7"));
            return false;
        }

        if (Samurai.getInstance().getUserHandler().loadUser(player.getUniqueId()).hasPvPTimer()) {
            player.sendMessage(iEvent.getEventType().getPrefix() + CC.blend("You cannot join events while in your pvp timer is active.", "&c", "&7"));
            return false;
        }

        boolean inventoryClean = true;
        for (ItemStack stack : player.getInventory()) {
            if (stack == null || stack.getType() == Material.AIR) continue;

            for (IAbilityItem item : AbilityItems.getInstance().getAbilityItems().values()) {
                if (!item.isAbilityItem(stack)) continue;

                inventoryClean = false;
                player.sendMessage(iEvent.getEventType().getPrefix() + CC.blend("You have a disallowed item: ", "&c", "&7") + CC.translate(item.getDisplayName()));
            }

        }

        return inventoryClean;
    }

    @Override
    public List<Player> getMembers(Player player) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(player);

        if (team != null) {
            return team.getOnlineMembers().stream().filter(p -> !p.equals(player)).toList();
        }

        return Collections.emptyList();
    }

    @Override
    public List<Player> getMembers(UUID playerUUID) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(playerUUID);

        if (team != null) {
            return team.getOnlineMembers().stream().filter(p -> !p.getUniqueId().equals(playerUUID)).toList();
        }

        return Collections.emptyList();
    }

    @Override
    public boolean canCapture(Player player, Capturable capturable) {
        SamuraiUser user = Samurai.getInstance().getUserHandler().getUser(player.getUniqueId());

        return !user.hasPvPTimer() && !user.isVanished() && player.getGameMode() == GameMode.SURVIVAL;
    }
}
