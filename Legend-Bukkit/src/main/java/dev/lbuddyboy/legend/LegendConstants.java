package dev.lbuddyboy.legend;

import dev.lbuddyboy.legend.team.model.Team;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class LegendConstants {

    public static final DecimalFormat KDR_FORMAT = new DecimalFormat("#.##");

    public static boolean isUHCKitMap(Location location) {
        return SettingsConfig.SETTINGS_UHC_MODE.getBoolean() && SettingsConfig.KITMAP_ENABLED.getBoolean() && !isWarzone(location);
    }

    public static boolean isAdminBypass(Player player) {
        return player.hasMetadata("BUILD_MODE") && player.hasPermission("legend.command.build");
    }

    public static boolean isWarzone(Location location) {
        return !isInBuffer(location);
    }

    public static String getTeamAt(Player player) {
        Team teamAt = LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getTeam(player.getLocation()).orElse(null);

        if (teamAt != null) return teamAt.getName(player);
        if (isWarzone(player.getLocation())) return "&4Warzone";

        return "&7Wilderness";
    }

    public static int getWilderness(World world) {
        int buffer = SettingsConfig.WILDERNESS_OVERWORLD.getInt();

        if (world.getEnvironment() == World.Environment.NETHER) buffer = SettingsConfig.WILDERNESS_NETHER.getInt();
        if (world.getEnvironment() == World.Environment.THE_END) buffer = SettingsConfig.WILDERNESS_END.getInt();

        return buffer;
    }

    public static int getBuffer(World world) {
        int buffer = SettingsConfig.BUFFER_OVERWORLD.getInt();

        if (world.getEnvironment() == World.Environment.NETHER) buffer = SettingsConfig.BUFFER_NETHER.getInt();
        if (world.getEnvironment() == World.Environment.THE_END) buffer = SettingsConfig.BUFFER_END.getInt();

        return buffer;
    }

    public static boolean isInBuffer(Location location) {
        World world = location.getWorld();
        int buffer = getBuffer(world);
        int x = location.getBlockX(), z = location.getBlockZ();

        return x >= -buffer && x <= buffer && z >= -buffer && z <= buffer;
    }

    public static boolean isUnclaimable(Location location) {
        World world = location.getWorld();
        int buffer = getWilderness(world);
        int x = location.getBlockX(), z = location.getBlockZ();

        return x >= -buffer && x <= buffer && z >= -buffer && z <= buffer;
    }

}
