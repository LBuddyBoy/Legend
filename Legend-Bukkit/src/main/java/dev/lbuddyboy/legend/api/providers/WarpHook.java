package dev.lbuddyboy.legend.api.providers;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.warps.api.PlayerWarpEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class WarpHook implements Listener {

    public WarpHook() {
        Bukkit.getPluginManager().registerEvents(this, LegendBukkit.getInstance());
    }

    @EventHandler
    public void onClaim(PlayerWarpEvent event) {
        Player player = event.getPlayer();


        if (!TeamType.SPAWN.appliesAt(player.getLocation()) && !LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getTeam(player.getLocation()).map(at -> at.isMember(player.getUniqueId())).orElse(false)) {
            player.sendMessage(CC.translate("&cYou need to be either in a safezone or in your claim to do this."));
            event.setCancelled(true);
            return;
        }

    }

}
