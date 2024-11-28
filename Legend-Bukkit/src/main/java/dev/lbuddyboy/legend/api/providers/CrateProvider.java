package dev.lbuddyboy.legend.api.providers;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.crates.CrateAPI;
import dev.lbuddyboy.crates.Crates;
import dev.lbuddyboy.crates.model.Crate;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CrateProvider implements CrateAPI {

    public CrateProvider() {
        Crates.getInstance().registerAPI(this);
    }

    @Override
    public boolean attemptUse(Player player, Crate crate) {
        Team teamAt = LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getTeam(player.getLocation()).orElse(null);

        if (teamAt != null && teamAt.getTeamType() == TeamType.SPAWN) {
            return true;
        }

        player.sendMessage(CC.translate("&cYou can only open crate keys in safe zones."));
        return false;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void registerCrate(Crate crate) {

    }

    @Override
    public void unregisterCrate(Crate crate) {

    }

    @Override
    public int getKeys(UUID uuid, Crate crate) {
        return 0;
    }

    @Override
    public void setKeys(UUID uuid, Crate crate, int i) {

    }
}
