package dev.lbuddyboy.samurai.map.leaderboard.impl;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.leaderboard.ILeaderBoardStat;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project Samurai
 * @file dev.lbuddyboy.samurai.map.leaderboard.impl
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
        return Samurai.getInstance().getUserHandler().getUser(playerUUID).getKDR();
    }

    @Override
    public String format(Double value) {
        return Team.KDR_FORMAT.format(value);
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
