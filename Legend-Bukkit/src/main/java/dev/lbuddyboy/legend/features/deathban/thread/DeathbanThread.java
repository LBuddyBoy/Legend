package dev.lbuddyboy.legend.features.deathban.thread;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.model.LegendUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DeathbanThread extends Thread {

    @Override
    public void run() {
        while (LegendBukkit.isENABLED()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());
                if (!user.isDeathBanned()) continue;
                if (user.isTimerActive("deathban")) continue;

                LegendBukkit.getInstance().getDeathbanHandler().handleRevive(player);
            }

            try {
                Thread.sleep(1_000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
