package dev.lbuddyboy.samurai.api.impl;

import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.deathmessage.DeathMessageProvider;
import dev.lbuddyboy.minigames.MiniGames;
import dev.lbuddyboy.rollback.api.RollbackAPI;
import dev.lbuddyboy.rollback.rollback.PlayerDeath;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.user.SamuraiUser;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project Skyblock
 * @file dev.lbuddyboy.samurai.api
 * @since 12/20/2023
 */
public class RollbackProvider implements RollbackAPI {

    @Override
    public boolean isDeathExempted(Player player) {
        return CitizensAPI.getNPCRegistry().isNPC(player)
                || Samurai.getInstance().getTeamWarHandler().getPlayers().contains(player)
                || Samurai.getInstance().getArenaHandler().isDeathbanned(player.getUniqueId())
                || player.getWorld() == MiniGames.getInstance().getWorld();
    }

    @Override
    public void applyDeath(UUID victim, ItemStack[] armor, ItemStack[] contents, ItemStack[] extras, int foodLevel, String ipAddress, Location location, Player killer, String deathMessage) {
        Player player = Bukkit.getPlayer(victim);
        DeathMessageProvider provider = CommonsPlugin.getInstance().getDeathMessageHandler().getProvider();

        if (player != null && !provider.getLastDeathMessage(player).equalsIgnoreCase("N/A")) {
            deathMessage = provider.getLastDeathMessage(player);
        }

        RollbackAPI.super.applyDeath(victim, armor, contents, extras, foodLevel, ipAddress, location, killer, deathMessage);
    }

    @Override
    public int getPlayerDeaths(UUID uuid) {
        return Samurai.getInstance().getUserHandler().loadUser(uuid).getDeaths();
    }

    @Override
    public int getPlayerKills(UUID uuid) {
        return Samurai.getInstance().getUserHandler().loadUser(uuid).getKills();
    }
}
