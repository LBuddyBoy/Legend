package dev.lbuddyboy.legend.api.providers;

import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.deathmessage.DeathMessageProvider;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.timer.impl.CombatTimer;
import dev.lbuddyboy.legend.user.model.LegendUser;
import dev.lbuddyboy.rollback.Rollback;
import dev.lbuddyboy.rollback.api.RollbackAPI;
import net.citizensnpcs.api.CitizensAPI;
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

    public RollbackProvider() {
        Rollback.getInstance().setRollbackAPI(this);
    }

    @Override
    public boolean isDeathExempted(Player player) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        return CitizensAPI.getNPCRegistry().isNPC(player) || user.isTimerActive("deathban");
    }

    @Override
    public boolean canOpenEnderChest(Player player) {
        return !LegendBukkit.getInstance().getTimerHandler().getTimer(CombatTimer.class).isActive(player.getUniqueId());
    }

    @Override
    public boolean canOpenVault(Player player) {
        return !LegendBukkit.getInstance().getTimerHandler().getTimer(CombatTimer.class).isActive(player.getUniqueId());
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
        return LegendBukkit.getInstance().getUserHandler().getUser(uuid).getDeaths();
    }

    @Override
    public int getPlayerKills(UUID uuid) {
        return LegendBukkit.getInstance().getUserHandler().getUser(uuid).getKills();
    }
}
