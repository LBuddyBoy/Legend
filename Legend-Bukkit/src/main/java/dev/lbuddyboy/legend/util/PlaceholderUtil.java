package dev.lbuddyboy.legend.util;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.util.LocationUtils;
import dev.lbuddyboy.events.Capturable;
import dev.lbuddyboy.events.Events;
import dev.lbuddyboy.events.IEvent;
import dev.lbuddyboy.events.koth.KoTH;
import dev.lbuddyboy.events.schedule.ScheduledEvent;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.LegendConstants;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TopEntry;
import dev.lbuddyboy.legend.team.model.TopType;
import dev.lbuddyboy.legend.user.model.LegendUser;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.Map;

public class PlaceholderUtil {
    
    public static String applyTeamPlaceholders(String s, Team team) {
        return s

                .replaceAll("%team%", team.getName());
    }

    public static String applyAllPlaceholders(String s, Player player) {
        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(player.getUniqueId()).orElse(null);
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());
        Map<Integer, TopEntry> entries = LegendBukkit.getInstance().getTeamHandler().getTopTeams().get(TopType.POINTS);

        if (s.contains("%deathban%")) {
            if (user.isDeathBanned()) {
                s = s
                        .replaceAll("%deathban%", TimeUtils.formatIntoHHMMSS((int) (user.getRemainingCooldown("deathban") / 1000)));
            }
        }

        if (team != null) {
            s = team.applyPlaceholders(s, player);
        }

        if (s.contains("%team_top_")) {
            for (int i = 1; i <= 20; i++) {
                String teamName = "";
                Team topTeam = null;

                if (entries.size() >= i) {
                    topTeam = LegendBukkit.getInstance().getTeamHandler().getTeamById(entries.get(i).getTeamUUID()).orElse(null);
                    if (topTeam != null) teamName = topTeam.getName(player);
                }

                s = s.replaceAll("%team_top_" + i + "%", teamName + (topTeam == null ? "" : "&7: &f" + topTeam.getPoints()));
            }
        }

        if (Events.getInstance().getActiveEvent().isPresent()) {
            IEvent event = Events.getInstance().getActiveEvent().get();

            s = event.applyPlaceHolders(s)
                    .replaceAll("%team%", Events.getApi().getTeamName(player))
                    .replaceAll("%event-name%", event.getName())
                    .replaceAll("%event-starts-in%", "")
            ;
        } else {
            ScheduledEvent event = Events.getInstance().getScheduleHandler().getNextEvent();

            if (event != null && event.getEvent() != null) {
                s = event.getEvent().applyPlaceHolders(s)
                        .replaceAll("%event-name%", event.getEventName())
                        .replaceAll("%event-starts-in%", "&7(" + TimeUtils.formatIntoDetailedStringShorter((int) ((event.getDate().getTime() - System.currentTimeMillis()) / 1000L)) + ")")
                ;
            }
        }

        s = user.applyPlaceholders(s);

        return s
                .replaceAll("%team_at%", LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getTeam(player.getLocation()).map(other -> other.getName(player)).orElse("<blend:&7;&f>Wilderness</>"))
                .replaceAll("%player_location%", LocationUtils.toString(player.getLocation()))
                .replaceAll("%player_claim%", LegendConstants.getTeamAt(player))
                .replaceAll("%credits-goal%", APIConstants.formatNumber(LegendBukkit.getInstance().getDeathbanHandler().getCreditsNeeded()))
                .replaceAll("%online_players%", APIConstants.formatNumber(BukkitUtil.getOnlinePlayers().size()))
                .replaceAll("%player_name%", player.getName())
                .replaceAll("%player_texture%", user.getHeadTextureValue())
                .replaceAll("%player_display_name%", player.getDisplayName())
                .replaceAll("%date%", APIConstants.SDF.format(new Date()));
    }
    
}
