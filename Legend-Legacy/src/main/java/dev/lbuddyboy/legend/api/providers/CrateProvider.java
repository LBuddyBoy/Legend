package dev.lbuddyboy.legend.api.providers;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.crates.api.CrateAPI;
import dev.lbuddyboy.crates.model.Crate;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import org.bukkit.entity.Player;

public class CrateProvider extends CrateAPI {

    @Override
    public boolean attemptUse(Player player, Crate crate) {
        Team teamAt = LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getTeam(player.getLocation()).orElse(null);

        if (teamAt != null && teamAt.getTeamType() == TeamType.SPAWN) {
            return true;
        }

        player.sendMessage(CC.translate("&cYou can only open crate keys in safe zones."));
        return false;
    }
}
