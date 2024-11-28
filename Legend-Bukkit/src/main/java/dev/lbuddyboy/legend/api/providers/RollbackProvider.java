package dev.lbuddyboy.legend.api.providers;

import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.deathmessage.DeathMessageProvider;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.model.LegendUser;
import dev.lbuddyboy.rollback.api.RollbackAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        return user.isTimerActive("deathban");
    }

    @Override
    public void applyDeath(UUID victim, ItemStack[] armor, ItemStack[] contents, int foodLevel, String ipAddress, Location location, Player killer, String deathMessage) {
        Player player = Bukkit.getPlayer(victim);
        DeathMessageProvider provider = CommonsPlugin.getInstance().getDeathMessageHandler().getProvider();

        if (player != null && !provider.getLastDeathMessage(player).equalsIgnoreCase("N/A")) {
            deathMessage = provider.getLastDeathMessage(player);
        }

        RollbackAPI.super.applyDeath(victim, armor, contents, foodLevel, ipAddress, location, killer, deathMessage);
    }

    @Override
    public int getPlayerDeaths(UUID uuid) {
        return LegendBukkit.getInstance().getUserHandler().getUser(uuid).getDeaths();
    }

    @Override
    public int getPlayerKills(UUID uuid) {
        return LegendBukkit.getInstance().getUserHandler().getUser(uuid).getKills();
    }
}
