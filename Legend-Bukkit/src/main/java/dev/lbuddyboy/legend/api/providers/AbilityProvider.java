package dev.lbuddyboy.legend.api.providers;

import dev.lbuddyboy.abilityitems.AbilityItems;
import dev.lbuddyboy.abilityitems.api.AbilityAPI;
import dev.lbuddyboy.arrow.ArrowPlugin;
import dev.lbuddyboy.auctionhouse.AuctionHouse;
import dev.lbuddyboy.events.Events;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.LegendConstants;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.timer.impl.ArcherTagTimer;
import dev.lbuddyboy.legend.timer.server.SOTWTimer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project Samurai
 * @file dev.lbuddyboy.samurai.api.impl
 * @since 5/10/2024
 */

public class AbilityProvider implements AbilityAPI {

    public AbilityProvider() {
        AbilityItems.registerAPI(this);
    }

    @Override
    public boolean isAtSpawn(Player player) {
        return TeamType.SPAWN.appliesAt(player.getLocation());
    }

    @Override
    public boolean isAtEvent(Player player) {
        return TeamType.KOTH.appliesAt(player.getLocation()) || TeamType.CTP.appliesAt(player.getLocation()) || TeamType.CITADEL.appliesAt(player.getLocation());
    }

    @Override
    public boolean isInWarZone(Player player) {
        if (LegendConstants.isInBuffer(player.getLocation())) {
            return !SettingsConfig.KITMAP_ENABLED.getBoolean();
        }

        return false;
    }

    @Override
    public boolean isTeammate(Player damager, Player victim) {
        return getTeam(damager).contains(victim);
    }

    @Override
    public boolean shouldBypassEffect(Player player) {
        return ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isInStaffMode(player)
                || LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId()).isTimerActive("invincibility")
                || LegendBukkit.getInstance().getTimerHandler().getServerTimer(SOTWTimer.class).isEnabled(player)
                || ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isVanished(player);
    }

    @Override
    public List<Player> getTeam(Player player) {
        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(player).orElse(null);
        List<Player> members = new ArrayList<>();

        if (team != null) {
            members.addAll(team.getOnlinePlayers());
            team.getAllies().forEach(ally -> members.addAll(ally.getOnlinePlayers()));
        }

        return members;
    }

    @Override
    public List<Player> getTeam(UUID uuid) {
        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(uuid).orElse(null);
        List<Player> members = new ArrayList<>();

        if (team != null) {
            members.addAll(team.getOnlinePlayers());
            team.getAllies().forEach(ally -> members.addAll(ally.getOnlinePlayers()));
        }

        return members;
    }

    @Override
    public void applyArcherTag(UUID uuid, int seconds) {
        LegendBukkit.getInstance().getTimerHandler().getTimer(ArcherTagTimer.class).apply(uuid, seconds);
    }
}
