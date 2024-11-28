package dev.lbuddyboy.events.schedule.thread;

import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.events.Events;
import dev.lbuddyboy.events.schedule.ScheduledEvent;
import org.bukkit.ChatColor;

import java.util.ArrayList;

public class ScheduleThread extends Thread {

    @Override
    public void run() {
        while (true) {
            if (Events.getInstance().getScheduleHandler() != null) {
                for (ScheduledEvent event : Events.getInstance().getScheduleHandler().getScheduledEvents()) {
                    if (!event.shouldActivate()) continue;

                    Events.getInstance().getLogger().info(ChatColor.RED + "Attempting to activate " + event.getEventName() + " from event schedule.");
                    Tasks.run(event::activate);
                }
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
