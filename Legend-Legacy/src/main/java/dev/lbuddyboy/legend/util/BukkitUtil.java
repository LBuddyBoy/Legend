package dev.lbuddyboy.legend.util;

import dev.lbuddyboy.legend.LegendBukkit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class BukkitUtil {

    public static List<? extends Player> getOnlinePlayers() {
        return LegendBukkit.getInstance().getServer().getOnlinePlayers().stream().filter(p -> {
            return !p.hasMetadata("REPLACE_WITH_ARROW_SHIT");
        }).toList();
    }

    public static void broadcast(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {

        }
    }

}
