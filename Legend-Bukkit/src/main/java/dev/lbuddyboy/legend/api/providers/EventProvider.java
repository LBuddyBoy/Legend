package dev.lbuddyboy.legend.api.providers;

import dev.lbuddyboy.arrow.ArrowPlugin;
import dev.lbuddyboy.arrow.staffmode.StaffModeConstants;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.events.Capturable;
import dev.lbuddyboy.events.Events;
import dev.lbuddyboy.events.IEvent;
import dev.lbuddyboy.events.api.IEventAPI;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.listener.EventListener;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.timer.impl.CombatTimer;
import dev.lbuddyboy.legend.timer.impl.InvincibilityTimer;
import dev.lbuddyboy.legend.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project Samurai
 * @file dev.lbuddyboy.samurai.api.impl
 * @since 5/10/2024
 */

public class EventProvider implements IEventAPI {

    public EventProvider() {
        Events.registerAPI(this);
        Bukkit.getPluginManager().registerEvents(new EventListener(), LegendBukkit.getInstance());
    }

    @Override
    public List<Player> getMembers(Player player) {
        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(player).orElse(null);

        if (team != null) {
            return team.getOnlinePlayers().stream().filter(p -> !p.equals(player)).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    public List<Player> getMembers(UUID playerUUID) {
        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(playerUUID).orElse(null);

        if (team != null) {
            return team.getOnlinePlayers().stream().filter(p -> !p.getUniqueId().equals(playerUUID)).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    public UUID getTeamId(UUID uuid) {
        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(uuid).orElse(null);

        return team == null ? null : team.getId();
    }

    @Override
    public String getTeamName(Player player) {
        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(player.getUniqueId()).orElse(null);

        return team == null ? player.getName() : team.getName();
    }

    @Override
    public String getTeamName(UUID uuid) {
        Team team = LegendBukkit.getInstance().getTeamHandler().getTeamById(uuid).orElse(null);

        return team == null ? "N/A" : team.getName();
    }

    @Override
    public List<Player> getMembersByTeamId(UUID uuid) {
        Team team = LegendBukkit.getInstance().getTeamHandler().getTeamById(uuid).orElse(null);

        return team == null ? Collections.emptyList() : team.getOnlinePlayers();
    }

}
