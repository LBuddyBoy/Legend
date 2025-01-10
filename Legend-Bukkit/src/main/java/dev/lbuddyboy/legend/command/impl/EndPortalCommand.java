package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.LocationUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.timer.impl.CombatTimer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

@CommandAlias("endportal|portal")
public class EndPortalCommand extends BaseCommand {

    @Default
    public static void def(Player sender) {
        List<Team> endPortals = LegendBukkit.getInstance().getTeamHandler().getSystemTeams().stream().filter(t -> t.getTeamType() == TeamType.ENDPORTAL).filter(t -> Objects.nonNull(t.getHome())).toList();

        if (endPortals.isEmpty()) {
            sender.sendMessage(CC.blend("There are no end portals created.", "&4", "&c"));
            return;
        }

        sender.sendMessage(" ");
        sender.sendMessage(CC.translate("<blend:&5;&d>&lEnd Portals</>"));
        for (Team team : endPortals) {
            sender.sendMessage(CC.translate("&7» &f" + getCorner(team) + " &e" + toString(team.getHome()) + " &a[Overworld] &7&o(( HOVER ))"));
        }
        sender.sendMessage(" ");
    }

    public static String toString(Location location) {
        int x = location.getBlockX();
        return x + ", " + location.getBlockY() + ", " + location.getBlockZ();
    }

    public static String getCorner(Team team) {
        Location location = team.getHome();
        int x = location.getBlockX(), z = location.getBlockZ();

        if (x < 0 && z < 0) {
            return "North West";
        } else if (x > 0 && z < 0) {
            return "North East";
        } else if (x > 0 && z > 0) {
            return "South East";
        } else if (x < 0 && z > 0) {
            return "South West";
        }

        return " ";
    }

}
