package dev.lbuddyboy.samurai.api.impl;

import dev.lbuddyboy.arrow.ArrowPlugin;
import dev.lbuddyboy.combatlogger.CombatLogger;
import dev.lbuddyboy.combatlogger.api.CombatLoggerAPI;
import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.deathmessage.DeathMessageProvider;
import dev.lbuddyboy.minigames.MiniGames;
import dev.lbuddyboy.rollback.Rollback;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.user.SamuraiUser;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project Skyblock
 * @file dev.lbuddyboy.samurai.api
 * @since 12/20/2023
 */
public class CombatLoggerProvider implements CombatLoggerAPI {

    @Override
    public boolean onCombatLoggerSpawn(Player player) {
        if (player.hasPermission("arrow.staff")) {
            if (ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isInStaffMode(player) || ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isVanished(player)) return false;
        }

        if (Samurai.getInstance().getArenaHandler().isDeathbanned(player.getUniqueId())) return false;
        if (Samurai.getInstance().getTeamWarHandler().getPlayers().contains(player)) return false;
        if (SOTWCommand.isSOTWTimer() && !SOTWCommand.hasSOTWEnabled(player.getUniqueId())) return false;
        if (Samurai.getInstance().getUserHandler().loadUser(player.getUniqueId()).hasPvPTimer()) return false;
        if (player.getNearbyEntities(20, 20, 20)
                .stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> ((Player) entity)).noneMatch(p -> {
                    Team team = Samurai.getInstance().getTeamHandler().getTeam(p);
                    if (team == null) return false;

                    return team.getOnlineMembers().contains(player);
                }) && !SpawnTagHandler.isTagged(player)) return false;
        if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) return false;
        if (player.hasMetadata("loggedout")) return false;
        if (player.getWorld() == MiniGames.getInstance().getWorld()) return false;

        return true;
    }

    @Override
    public boolean onCombatLoggerDamage(CombatLogger combatLogger, Player attacker) {
        if (DTRBitmask.SAFE_ZONE.appliesAt(attacker.getLocation()) || DTRBitmask.SAFE_ZONE.appliesAt(combatLogger.getNpc().getEntity().getLocation())) {
            return false;
        }

        if (Samurai.getInstance().getUserHandler().loadUser(attacker.getUniqueId()).hasPvPTimer()) {
            return false;
        }

        Team team = Samurai.getInstance().getTeamHandler().getTeam(combatLogger.getOwner());

        if (team != null && team.isMember(attacker.getUniqueId())) {
            return false;
        }

        return true;
    }

    @Override
    public void onCombatLoggerSpawn(Player player, CombatLogger combatLogger) {
        int seconds = (int) Samurai.getInstance().getServerHandler().getDeathban(player);
        boolean shouldBypass = player.isOp() || player.hasPermission("foxtrot.staff");

        combatLogger.getMetadata().put("deathbanSeconds", seconds);
        combatLogger.getMetadata().put("bypassDeathban", shouldBypass);
        combatLogger.getMetadata().put("ipAddress", player.getAddress().getAddress().getHostAddress());
    }

    @Override
    public void onCombatLoggerDeath(CombatLogger combatLogger, Player killer) {
        SamuraiUser user = Samurai.getInstance().getUserHandler().loadUser(combatLogger.getOwner());
        int seconds = (int) combatLogger.getMetadata().get("deathbanSeconds");
        String ipAddress = (String) combatLogger.getMetadata().get("ipAddress");

        combatLogger.getNpc().getEntity().getWorld().strikeLightning(combatLogger.getNpc().getEntity().getLocation());
        if ((Boolean) combatLogger.getMetadata().get("bypassDeathban")) {
            user.revive();
        } else {
            user.deathBan(seconds * 1000L);
        }

        DeathMessageProvider provider = CommonsPlugin.getInstance().getDeathMessageHandler().getProvider();
        String victimName = combatLogger.getNpc().getName();
        String deathMessage = killer == null ? provider.getDeathMessageFormat("died.") : provider.getDeathMessageFormat("was killed by");
        String deathMessageFormatted = CC.translate(victimName + " " + deathMessage + " " + (provider.getPlayerFormat(killer)));

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!provider.shouldReceiveDeathMessage(player)) continue;

            player.sendMessage(deathMessageFormatted);
        }

        Team team = Samurai.getInstance().getTeamHandler().getTeam(combatLogger.getOwner());
        if (team != null) {
            team.playerDeath(UUIDUtils.name(combatLogger.getOwner()), team.getDTR(), Samurai.getInstance().getServerHandler().getDTRLoss(combatLogger.getNpc().getEntity().getLocation()), killer);
        }

        if (killer != null) {
            Team killerTeam = Samurai.getInstance().getTeamHandler().getTeam(killer);

            if (killerTeam != null) {
                int pointsBefore = Samurai.getInstance().getTopHandler().getTotalPoints(killerTeam);

                if (Arrays.stream(combatLogger.getArmor()).noneMatch(s -> s == null || s.getType() == Material.AIR)) {
                    killerTeam.createLog(killer.getUniqueId(), "NAKED LOGGER KILL &7(" + pointsBefore + " -> " + Samurai.getInstance().getTopHandler().getTotalPoints(killerTeam) + "&7)", killer.getName() + " killed " + victimName + "'s Logger");
                } else {
                    killerTeam.setKills(killerTeam.getKills() + 1);
                    killerTeam.createLog(killer.getUniqueId(), "LOGGER KILL &7(" + pointsBefore + " -> " + Samurai.getInstance().getTopHandler().getTotalPoints(killerTeam) + "&7)", killer.getName() + " killed " + victimName + "'s Logger");
                }
            }

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
        return Samurai.getInstance().getArenaHandler().getSpawn();
    }
}
