package dev.lbuddyboy.legend.api.providers;

import dev.lbuddyboy.arrow.ArrowPlugin;
import dev.lbuddyboy.combatlogger.CombatLogger;
import dev.lbuddyboy.combatlogger.CombatLoggerPlugin;
import dev.lbuddyboy.combatlogger.api.CombatLoggerAPI;
import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.deathmessage.DeathMessageProvider;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.team.model.log.impl.TeamDTRChangeLog;
import dev.lbuddyboy.legend.team.model.log.impl.TeamPointsChangeLog;
import dev.lbuddyboy.legend.timer.impl.CombatTimer;
import dev.lbuddyboy.legend.timer.impl.InvincibilityTimer;
import dev.lbuddyboy.legend.timer.server.SOTWTimer;
import dev.lbuddyboy.legend.user.model.LegendUser;
import dev.lbuddyboy.rollback.Rollback;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project Skyblock
 * @file dev.lbuddyboy.samurai.api
 * @since 12/20/2023
 */
public class CombatLoggerProvider implements CombatLoggerAPI {

    public CombatLoggerProvider() {
        CombatLoggerPlugin.getInstance().registerAPI(this);
    }

    @Override
    public boolean onCombatLoggerSpawn(Player player) {
        if (player.hasPermission("arrow.staff")) {
            if (ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isInStaffMode(player) || ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isVanished(player))
                return false;
        }

        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());
        InvincibilityTimer invincibilityTimer = LegendBukkit.getInstance().getTimerHandler().getTimer(InvincibilityTimer.class);
        SOTWTimer sotwTimer = LegendBukkit.getInstance().getTimerHandler().getServerTimer(SOTWTimer.class);
        CombatTimer combatTimer = LegendBukkit.getInstance().getTimerHandler().getTimer(CombatTimer.class);

        if (user.isDeathBanned()) return false;
        if (sotwTimer.isActive() && !sotwTimer.getEnabledPlayers().contains(player.getUniqueId())) return false;
        if (invincibilityTimer.isActive(player.getUniqueId())) return false;
        if (player.getNearbyEntities(40, 40, 40)
                .stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> ((Player) entity))
                .noneMatch(p -> {
                    Optional<Team> teamOpt = LegendBukkit.getInstance().getTeamHandler().getTeam(p);
                    return teamOpt.map(team -> !team.getOnlinePlayers().contains(player)).orElse(false);

                }) && !combatTimer.isActive(player.getUniqueId())) return false;
        if (TeamType.SPAWN.appliesAt(player.getLocation())) return false;
        if (player.hasMetadata("safe_disconnect")) return false;

        return true;
    }

    @Override
    public boolean onCombatLoggerDamage(CombatLogger combatLogger, Player attacker) {
        InvincibilityTimer invincibilityTimer = LegendBukkit.getInstance().getTimerHandler().getTimer(InvincibilityTimer.class);

        if (TeamType.SPAWN.appliesAt(attacker.getLocation()) || TeamType.SPAWN.appliesAt(combatLogger.getNpc().getEntity().getLocation())) {
            return false;
        }

        if (invincibilityTimer.isActive(attacker.getUniqueId())) {
            return false;
        }

        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(combatLogger.getOwner()).orElse(null);

/*        if (combatLogger.getNpc() instanceof Player entity) {
            entity.setNoDamageTicks(20);
        }*/

        return team == null || !team.isMember(attacker.getUniqueId());
    }

    @Override
    public void onCombatLoggerSpawn(Player player, CombatLogger combatLogger) {
        long time = LegendBukkit.getInstance().getDeathbanHandler().getDeathbanTime(player);
        boolean shouldBypass = player.isOp() || player.hasPermission("legend.deathban.bypass");

        combatLogger.getMetadata().put("deathbanTime", time);
        combatLogger.getMetadata().put("bypassDeathban", shouldBypass);
        combatLogger.getMetadata().put("ipAddress", player.getAddress().getAddress().getHostAddress());
    }

    @Override
    public void onCombatLoggerDeath(CombatLogger combatLogger, Player killer) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(combatLogger.getOwner());
        long time = (long) combatLogger.getMetadata().get("deathbanTime");
        String ipAddress = (String) combatLogger.getMetadata().get("ipAddress");

        for (Player player : Bukkit.getOnlinePlayers()) {
            LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, ((CraftWorld)player.getWorld()).getHandle());
            Location location = combatLogger.getNpc().getEntity().getLocation();

            bolt.setVisualOnly(true);

            ((CraftPlayer)player).getHandle().connection.sendPacket(new ClientboundAddEntityPacket(bolt, 0, new BlockPos(
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ()
            )));
        }

        if ((Boolean) combatLogger.getMetadata().get("bypassDeathban")) {
            user.setCombatLoggerDied(true);
        } else {
            user.applyTimer("deathban", time);
            user.setDeathBanned(true);
        }

        user.removeTimer("combat");

        DeathMessageProvider provider = CommonsPlugin.getInstance().getDeathMessageHandler().getProvider();
        String victimName = combatLogger.getNpc().getFullName();
        String deathMessage = killer == null ? provider.getDeathMessageFormat("died.") : provider.getDeathMessageFormat("was killed by");
        String deathMessageFormatted = CC.translate(victimName + " " + deathMessage + " " + (provider.getPlayerFormat(killer)));

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!provider.shouldReceiveDeathMessage(player)) continue;

            player.sendMessage(deathMessageFormatted);
        }

        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(combatLogger.getOwner()).orElse(null);

        if (team != null) {
            double previousDTR = team.getDeathsUntilRaidable();
            double newDTR = previousDTR - SettingsConfig.TEAM_DTR_LOSS.getDouble();

            team.applyDTRFreeze(SettingsConfig.TEAM_REGEN_COOLDOWN.getTimeDuration().transform());
            team.setDeathsUntilRaidable(newDTR);
            team.setPoints(
                    team.getPoints() + SettingsConfig.POINTS_DEATH.getInt(),
                    combatLogger.getOwner(),
                    TeamPointsChangeLog.ChangeCause.LOGGER_DEATH
            );
            team.createTeamLog(new TeamDTRChangeLog(team.getId(), previousDTR, team.getDeathsUntilRaidable(), combatLogger.getOwner(), TeamDTRChangeLog.ChangeCause.LOGGER_DEATH));
            team.flagSave();
        }

        if (killer != null) {
            LegendBukkit.getInstance().getTeamHandler().getTeam(killer).ifPresent(killerTeam -> killerTeam.setPoints(
                    killerTeam.getPoints() + SettingsConfig.POINTS_KILL.getInt(),
                    killer.getUniqueId(),
                    TeamPointsChangeLog.ChangeCause.LOGGER_KILL
            ));
        }

        Rollback.getInstance().getRollbackAPI().applyDeath(
                combatLogger.getOwner(),
                combatLogger.getArmor(),
                combatLogger.getContents(),
                combatLogger.getExtras(),
                20,
                ipAddress,
                combatLogger.getNpc().getEntity().getLocation(),
                killer,
                deathMessageFormatted
        );
    }

    @Override
    public Location getSpawnLocation() {
        return LegendBukkit.getInstance().getSpawnLocation();
    }
}
