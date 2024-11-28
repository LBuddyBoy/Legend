package dev.lbuddyboy.legend.features.leaderboard;

import dev.lbuddyboy.legend.LegendBukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project Samurai
 * @file dev.lbuddyboy.legend.features.leaderboard
 * @since 5/9/2024
 */
public interface ILeaderBoardStat {

    String getId();
    String getValueName();
    Double getValue(UUID playerUUID);
    String format(Double value);
    ItemStack getMenuItem();
    String getPrimaryColor();
    String getSecondaryColor();

    default ConfigurationSection getSection() {
        return LegendBukkit.getInstance().getLeaderBoardHandler().getConfig().getConfigurationSection("leaderboards." + getId());
    }

}
