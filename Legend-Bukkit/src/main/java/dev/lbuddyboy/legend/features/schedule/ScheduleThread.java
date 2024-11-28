package dev.lbuddyboy.legend.features.schedule;

import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.events.Events;
import dev.lbuddyboy.events.schedule.ScheduledEvent;
import dev.lbuddyboy.legend.LegendBukkit;
import org.bukkit.ChatColor;

public class ScheduleThread extends Thread {

    @Override
    public void run() {
        while (LegendBukkit.isENABLED()) {
            try {
                for (ScheduleEntry entry : LegendBukkit.getInstance().getScheduleHandler().getScheduleEntries()) {
                    if (!entry.shouldActivate()) continue;

                    LegendBukkit.getInstance().getLogger().info(ChatColor.RED + "Attempting to activate " + entry.getId() + " from the schedule.");
                    Tasks.run(entry::activate);
                }

                boolean removed = LegendBukkit.getInstance().getScheduleHandler().getScheduleEntries().removeIf(e -> e.isActivated() && e.isRemoveAfterExecute());

                if (removed) {
                    LegendBukkit.getInstance().getScheduleHandler().saveSchedule();
                }

            } catch (Exception e) {
                e.printStackTrace();;
            }
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
