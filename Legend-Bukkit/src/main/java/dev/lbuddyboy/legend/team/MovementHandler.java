package dev.lbuddyboy.legend.team;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.timer.impl.CombatTimer;
import dev.lbuddyboy.legend.timer.impl.EnderPearlTimer;
import dev.lbuddyboy.legend.timer.impl.InvincibilityTimer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class MovementHandler implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (event.getTo().getBlockX() == event.getFrom().getBlockX() && event.getTo().getBlockZ() == event.getFrom().getBlockZ()) return;

        Team team = LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getTeam(event.getTo()).orElse(null);
        if (team == null) return;
        if (checkMovement(player, team, true)) return;

        event.setTo(event.getFrom());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (event.getTo().getBlockX() == event.getFrom().getBlockX() && event.getTo().getBlockZ() == event.getFrom().getBlockZ()) return;

        Team team = LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getTeam(event.getTo()).orElse(null);
        if (team == null) return;
        if (checkMovement(player, team, true)) return;

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            EnderPearlTimer timer = (EnderPearlTimer) LegendBukkit.getInstance().getTimerHandler().getTimer(EnderPearlTimer.class);

            timer.remove(player.getUniqueId());
        }

        event.setTo(event.getFrom());
    }

    public boolean checkMovement(Player player, Team team, boolean message) {
        CombatTimer combatTimer = (CombatTimer) LegendBukkit.getInstance().getTimerHandler().getTimer(CombatTimer.class);
        InvincibilityTimer invincibilityTimer = (InvincibilityTimer) LegendBukkit.getInstance().getTimerHandler().getTimer(InvincibilityTimer.class);

        if (invincibilityTimer.isActive(player.getUniqueId())) {
            if (team.getTeamType() == TeamType.PLAYER) {
                if (message) {
                    player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("invincibility.cannot-enter")
                            .replaceAll("%team%", team.getName(player))
                    ));
                }
                return false;
            }
        }

        if (combatTimer.isActive(player.getUniqueId())) {
            if (team.getTeamType() == TeamType.SPAWN) {
                if (message) {
                    player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("combat-tagged.cannot-enter")
                            .replaceAll("%team%", team.getName(player))
                    ));
                }
                return false;
            }
        }

        return true;
    }

}
