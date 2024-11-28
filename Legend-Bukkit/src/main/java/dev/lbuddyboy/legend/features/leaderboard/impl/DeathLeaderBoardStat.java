package dev.lbuddyboy.legend.features.leaderboard.impl;

import dev.lbuddyboy.commons.api.APIConstants;
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
public class DeathLeaderBoardStat implements ILeaderBoardStat {

    @Override
    public String getId() {
        return "Death";
    }

    @Override
    public String getValueName() {
        return "Deaths";
    }

    @Override
    public Double getValue(UUID playerUUID) {
        return (double) LegendBukkit.getInstance().getUserHandler().getUser(playerUUID).getDeaths();
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
