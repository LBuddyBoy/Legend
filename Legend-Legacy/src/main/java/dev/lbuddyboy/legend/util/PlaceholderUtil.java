package dev.lbuddyboy.legend.util;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.user.model.LegendUser;
import org.bukkit.entity.Player;

import java.util.Date;

public class PlaceholderUtil {
    
    public static String applyTeamPlaceholders(String s, Team team) {
        return s

                .replaceAll("%team%", team.getName());
    }

    public static String applyAllPlaceholders(String s, Player player) {
        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(player.getUniqueId()).orElse(null);
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (team != null) {
            s = team.applyPlaceholders(s, player);
        }

        s = user.applyPlaceholders(s);

        return s
                .replaceAll("%player_name%", player.getName())
                .replaceAll("%player_texture%", user.getSkin().getTexture())
                .replaceAll("%player_signature%", user.getSkin().getSignature())
                .replaceAll("%player_display_name%", player.getDisplayName())
                .replaceAll("%date%", APIConstants.SDF.format(new Date()));
    }
    
}
