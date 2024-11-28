package dev.lbuddyboy.legend.features.schedule;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.events.Events;
import dev.lbuddyboy.events.IEvent;
import dev.lbuddyboy.legend.LegendBukkit;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Calendar;
import java.util.Date;

@Data
@AllArgsConstructor
public class ScheduleEntry {

    private int dayOfWeek, hourOfDay, minute;
    private String id, command, displayName;
    private boolean activated = false, adminOnly, removeAfterExecute, broadcast;

    public boolean shouldActivate() {
        int currentDayOfWeek = APIConstants.getCalendar().get(Calendar.DAY_OF_WEEK);
        int currentHourOfDay = APIConstants.getCalendar().get(Calendar.HOUR_OF_DAY);
        int currentMinute = APIConstants.getCalendar().get(Calendar.MINUTE);

        return currentDayOfWeek == this.dayOfWeek && currentHourOfDay == this.hourOfDay && currentMinute == this.minute && !activated;
    }

    public Date getDate() {
        Calendar calendar = Calendar.getInstance(APIConstants.TIME_ZONE, APIConstants.LOCALE);

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

    public long getTimeLeft() {
        return getDate().getTime() - APIConstants.getCalendar().getTime().getTime();
    }

    public void activate() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.command);
        if (this.broadcast) {
            LegendBukkit.getInstance().getScheduleHandler().getConfig().getStringList("broadcast-format").forEach(s -> {
                LegendBukkit.getInstance().getServer().broadcastMessage(CC.translate(s
                        .replaceAll("%entry-display%", this.displayName)
                ));
            });
        }
        activated = true;
    }

    public String serialize() {
        return this.id + ";;" + this.dayOfWeek + ";;" + this.hourOfDay + ";;" + this.minute + ";;" + this.command + ";;" + this.displayName + ";;" + this.adminOnly + ";;" + this.removeAfterExecute + ";;" + this.broadcast;
    }

}
