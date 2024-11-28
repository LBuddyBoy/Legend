package dev.lbuddyboy.legend.api.providers;

import dev.lbuddyboy.auctionhouse.AuctionHouse;
import dev.lbuddyboy.auctionhouse.api.AuctionAPI;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.model.LegendUser;

import java.util.UUID;

public class AuctionHouseProvider implements AuctionAPI {

    public AuctionHouseProvider() {
        AuctionHouse.getInstance().setAuctionAPI(this);
    }

    public boolean hasFunds(UUID player, double amount) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player);

        return user.getBalance() >= amount;
    }

    public void withdrawFunds(UUID player, double amount) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player);

        user.setBalance(user.getBalance() - amount);
    }

    public void depositFunds(UUID player, double amount) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player);

        user.setBalance(user.getBalance() + amount);
    }


}
