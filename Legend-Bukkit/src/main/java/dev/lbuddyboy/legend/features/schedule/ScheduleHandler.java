package dev.lbuddyboy.legend.features.schedule;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.events.Events;
import dev.lbuddyboy.events.schedule.ScheduledEvent;
import dev.lbuddyboy.legend.LegendBukkit;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.time.DayOfWeek;
import java.util.*;

@Getter
public class ScheduleHandler implements IModule {

    private final List<ScheduleEntry> scheduleEntries = new ArrayList<>();
    private Config config;

    @Override
    public void load() {
        new ScheduleThread().start();
        reload();
    }

    @Override
    public void unload() {
        this.saveSchedule();
    }

    @Override
    public void reload() {
        this.loadSchedule();

        this.config.setComments("schedule", Arrays.asList(
                "SUNDAY = 1",
                "SATURDAY = 7",
                "1 AM = 1",
                "12 AM = 24",
                "MINUTE = 1-60",
                "Current Day of Week: " + Calendar.getInstance(APIConstants.TIME_ZONE).get(Calendar.DAY_OF_WEEK),
                "Current Hour: " + Calendar.getInstance(APIConstants.TIME_ZONE).get(Calendar.HOUR_OF_DAY),
                "Current Minute: " + Calendar.getInstance(APIConstants.TIME_ZONE).get(Calendar.MINUTE),
                "Format: id;;dayOfWeek;;hourOfDay;;minute;;command;;display;;adminOnly;;removeAfterExecute;;broadcast"
        ));
        this.config.save();
    }

    public void saveSchedule() {
        this.config.set("schedule", this.scheduleEntries.stream().filter(entry -> !entry.isActivated()).map(ScheduleEntry::serialize).toList());
        this.config.save();
    }

    public void loadSchedule() {
        this.scheduleEntries.clear();
        this.config = new Config(LegendBukkit.getInstance(), "schedule");

        for (String s : this.config.getStringList("schedule")) {
            String[] parts = s.split(";;");
            String eventName = parts[0];
            int dayOfWeek = Integer.parseInt(parts[1]);
            int hourOfDay = Integer.parseInt(parts[2]);
            int minute = Integer.parseInt(parts[3]);
            String command = parts[4];
            String displayName = parts[5];
            boolean adminOnly = Boolean.parseBoolean(parts[6]);
            boolean removeAfterExecute = Boolean.parseBoolean(parts[7]);
            boolean broadcast = Boolean.parseBoolean(parts[8]);

            this.scheduleEntries.add(new ScheduleEntry(dayOfWeek, hourOfDay, minute, eventName, command, displayName, false, adminOnly, removeAfterExecute, broadcast));
        }
    }

    public ScheduleEntry getScheduleEntry(String id) {
        return this.scheduleEntries.stream().filter(entry -> entry.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    public List<ScheduleEntry> getSortedSchedule() {
        return this.scheduleEntries.stream().sorted(Comparator.comparingLong(o -> o.getDate().getTime())).toList();
    }

    public ScheduleEntry getNextEvent() {
        return this.scheduleEntries.stream().min(Comparator.comparingLong(o -> o.getDate().getTime())).orElse(null);
    }

    public ScheduleEntry getNextEvent(Player sender) {
        return this.scheduleEntries.stream().filter(e -> {
            if (e.isAdminOnly()) return sender.hasPermission("legend.command.schedule.admin");

            return true;
        }).min(Comparator.comparingLong(o -> Long.compare(o.getDate().getTime(), System.currentTimeMillis()))).orElse(null);
    }

    public List<ScheduleEntry> getScheduleEntries(int dayOfWeek) {
        return getSortedSchedule().stream().filter(entry -> entry.getDayOfWeek() == dayOfWeek).toList();
    }

    public List<ScheduleEntry> getScheduleEntries(int dayOfWeek, Player sender) {
        return getSortedSchedule().stream().filter(e -> {
            if (e.isAdminOnly()) return sender.hasPermission("legend.command.schedule.admin");

            return true;
        }).filter(entry -> entry.getDayOfWeek() == dayOfWeek).toList();
    }

    public ScheduleEntry createScheduleEntry(String id, int dayOfTheWeek, int hourOfTheDay, int minuteOfTheDay, String command, String displayName, boolean adminOnly, boolean removeAfterExecute, boolean broadcast) {
        ScheduleEntry entry = new ScheduleEntry(
                dayOfTheWeek,
                hourOfTheDay,
                minuteOfTheDay,
                id,
                command,
                displayName,
                false,
                adminOnly,
                removeAfterExecute,
                broadcast
        );

        LegendBukkit.getInstance().getScheduleHandler().getScheduleEntries().add(entry);
        LegendBukkit.getInstance().getScheduleHandler().saveSchedule();

        return entry;
    }

}
