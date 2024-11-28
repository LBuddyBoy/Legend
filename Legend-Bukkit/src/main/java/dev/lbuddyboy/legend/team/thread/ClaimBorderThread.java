package dev.lbuddyboy.legend.team.thread;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.ClaimHandler;
import dev.lbuddyboy.legend.team.MovementHandler;
import dev.lbuddyboy.legend.team.TeamHandler;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.claim.Claim;
import dev.lbuddyboy.legend.timer.impl.CombatTimer;
import dev.lbuddyboy.legend.timer.impl.InvincibilityTimer;
import dev.lbuddyboy.legend.util.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ClaimBorderThread extends Thread {

    public static final int REGION_DISTANCE = 8;
    public static final int REGION_DISTANCE_SQUARED = REGION_DISTANCE * REGION_DISTANCE;

    private final TeamHandler teamHandler = LegendBukkit.getInstance().getTeamHandler();
    private final ClaimHandler claimHandler = this.teamHandler.getClaimHandler();
    private final MovementHandler movementHandler = LegendBukkit.getInstance().getTeamHandler().getMovementHandler();

    @Override
    public void run() {
        while (LegendBukkit.isENABLED()) {
            try {
                CombatTimer combatTimer = LegendBukkit.getInstance().getTimerHandler().getTimer(CombatTimer.class);
                InvincibilityTimer invincibilityTimer = LegendBukkit.getInstance().getTimerHandler().getTimer(InvincibilityTimer.class);

                for (Player player : BukkitUtil.getOnlinePlayers()) {
                    if (!player.getWorld().isChunkLoaded(player.getLocation().getBlockX() >> 4, player.getLocation().getBlockZ() >> 4)) continue;

                    this.claimHandler.clearBorderView(player);

                    if (invincibilityTimer.isActive(player.getUniqueId()) || combatTimer.isActive(player.getUniqueId())) {
                        Set<Claim> claims = this.claimHandler.getClaims(player.getLocation(), REGION_DISTANCE);
                        Map<Claim, Team> teams = claims.stream().filter(c -> c.getTeam().isPresent()).collect(Collectors.toMap(
                                c -> c,
                                c -> c.getTeam().get()
                        ));

                        for (Map.Entry<Claim, Team> entry : teams.entrySet()) {
                            if (this.movementHandler.checkMovement(player, entry.getValue(), false)) continue;

                            this.claimHandler.sendBorderView(player, entry.getKey());
                        }
                    }
                }
            } catch (Exception e) {

            }

            try {
                Thread.sleep(250L);
            } catch (Exception e) {

            }
        }
    }
}
