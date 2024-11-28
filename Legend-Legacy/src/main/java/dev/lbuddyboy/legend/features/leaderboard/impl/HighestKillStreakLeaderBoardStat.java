package dev.lbuddyboy.samurai.map.leaderboard.impl;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.leaderboard.ILeaderBoardStat;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project Samurai
 * @file dev.lbuddyboy.samurai.map.leaderboard.impl
 * @since 5/9/2024
 */
public class HighestKillStreakLeaderBoardStat implements ILeaderBoardStat {

    @Override
    public String getId() {
        return "highest_kill_streaks";
    }

    @Override
    public String getValueName() {
        return "Highest Kill Streak";
    }

    @Override
    public Double getValue(UUID playerUUID) {
        return (double) Samurai.getInstance().getUserHandler().getUser(playerUUID).getHighestKillStreak();
    }

    @Override
    public String format(Double value) {
        return APIConstants.formatNumber(value);
    }

    @Override
    public ItemStack getMenuItem() {
        return ItemUtils.itemStackFromConfigSect("menu-item", getSection());
    }

    @Override
    public String getPrimaryColor() {
        return getSection().getString("primary-color");
    }

    @Override
    public String getSecondaryColor() {
        return getSection().getString("secondary-color");
    }

}
