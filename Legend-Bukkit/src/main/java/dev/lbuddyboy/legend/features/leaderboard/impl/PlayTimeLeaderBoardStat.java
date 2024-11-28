package dev.lbuddyboy.legend.features.leaderboard.impl;

import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.leaderboard.ILeaderBoardStat;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project Samurai
 * @file dev.lbuddyboy.legend.features.leaderboard.impl
 * @since 5/9/2024
 */
public class PlayTimeLeaderBoardStat implements ILeaderBoardStat {

    @Override
    public String getId() {
        return "PlayTime";
    }

    @Override
    public String getValueName() {
        return "Play Time";
    }

    @Override
    public Double getValue(UUID playerUUID) {
        return (double) LegendBukkit.getInstance().getUserHandler().getUser(playerUUID).getActivePlayTime();
    }

    @Override
    public String format(Double value) {
        return TimeUtils.formatIntoDetailedString(value.intValue());
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
