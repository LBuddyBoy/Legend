package dev.lbuddyboy.legend.features.leaderboard.impl;

import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.LegendConstants;
import dev.lbuddyboy.legend.features.leaderboard.ILeaderBoardStat;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project Samurai
 * @file dev.lbuddyboy.legend.features.leaderboard.impl
 * @since 5/9/2024
 */
public class KDRLeaderBoardStat implements ILeaderBoardStat {

    @Override
    public String getId() {
        return "kdr";
    }

    @Override
    public String getValueName() {
        return "KDR";
    }

    @Override
    public Double getValue(UUID playerUUID) {
        return LegendBukkit.getInstance().getUserHandler().getUser(playerUUID).getKDR();
    }

    @Override
    public String format(Double value) {
        return LegendConstants.KDR_FORMAT.format(value);
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
