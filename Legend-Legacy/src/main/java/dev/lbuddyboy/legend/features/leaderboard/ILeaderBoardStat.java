package dev.lbuddyboy.samurai.map.leaderboard;

import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project Samurai
 * @file dev.lbuddyboy.samurai.map.leaderboard
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
        return Samurai.getInstance().getLeaderBoardHandler().getConfig().getConfigurationSection("leaderboards." + getId());
    }

}
