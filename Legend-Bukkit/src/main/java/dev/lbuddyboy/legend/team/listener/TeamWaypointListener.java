package dev.lbuddyboy.legend.team.listener;

import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.TeamHandler;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.WaypointType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TeamWaypointListener implements Listener {

    private final TeamHandler teamHandler = LegendBukkit.getInstance().getTeamHandler();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        this.teamHandler.getTeam(player.getUniqueId()).ifPresent(team -> {
            Tasks.runLater(() -> {
                if (team.getRallyLocation() != null) {
                    team.sendWaypoint(team.getWaypoints().get(WaypointType.RALLY), player);
                }
                if (team.getFocusedTeam() != null) {
                    team.sendWaypoint(team.getWaypoints().get(WaypointType.FOCUSED), player);
                }
                if (team.getHome() != null) {
                    team.sendWaypoint(team.getWaypoints().get(WaypointType.HOME), player);
                }
            }, 20);

            team.sendMessage(LegendBukkit.getInstance().getLanguage().getString("team.connected")
                    .replaceAll("%player%", player.getName())
            );
        });

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        this.teamHandler.getTeam(player.getUniqueId()).ifPresent(team -> {
            for (WaypointType type : team.getWaypoints().keySet()) {
                team.removeWaypoint(type.getWaypointName(), player);
            }

            team.sendMessage(LegendBukkit.getInstance().getLanguage().getString("team.disconnected")
                    .replaceAll("%player%", player.getName())
            );
        });
    }

}
