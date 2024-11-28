package dev.lbuddyboy.events.schedule;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.events.Events;
import dev.lbuddyboy.events.IEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.ChatColor;

import java.util.Calendar;
import java.util.Date;

@Data
@AllArgsConstructor
public class ScheduledEvent {

    private int dayOfWeek, hourOfDay, minute;
    private String eventName;
    private boolean activated = false;

    public boolean shouldActivate() {
        int currentDayOfWeek = APIConstants.getCalendar().get(Calendar.DAY_OF_WEEK);
        int currentHourOfDay = APIConstants.getCalendar().get(Calendar.HOUR_OF_DAY);
        int currentMinute = APIConstants.getCalendar().get(Calendar.MINUTE);

        return currentDayOfWeek == this.dayOfWeek && currentHourOfDay == this.hourOfDay && currentMinute == this.minute && !activated;
    }

    public Date getDate() {
        Calendar calendar = Calendar.getInstance();

        // Set the calendar to the nearest upcoming dayOfWeek, hourOfDay, and minute
        calendar.set(Calendar.DAY_OF_WEEK, this.dayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, this.hourOfDay);
        calendar.set(Calendar.MINUTE, this.minute);
        calendar.set(Calendar.SECOND, 0); // Reset seconds and milliseconds
        calendar.set(Calendar.MILLISECOND, 0);

        // If the calculated time is in the past, add 7 days to get the next occurrence
        if (calendar.getTime().before(new Date())) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        }

        return calendar.getTime();
    }

    public void activate() {
        if (getEvent() == null) {
            Events.getInstance().getLogger().info(ChatColor.RED + "Tried to activate event " + this.eventName + " but it doesn't exist.");
            return;
        }

        if (getEvent().isActive()) {
            Events.getInstance().getLogger().info(ChatColor.RED + "Tried to activate event " + this.eventName + " but it was already active.");
            return;
        }

        getEvent().start();
        activated = true;
    }

    public IEvent getEvent() {
        return Events.getInstance().getEvents().get(this.eventName);
    }

}
