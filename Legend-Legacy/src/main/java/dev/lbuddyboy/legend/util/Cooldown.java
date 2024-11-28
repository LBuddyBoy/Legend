package dev.lbuddyboy.legend.util;

import dev.lbuddyboy.commons.api.util.TimeUtils;
import lombok.Getter;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project AbilityItems
 * @file dev.lbuddyboy.abilityitems.util
 * @since 5/10/2024
 */

@Getter
public class Cooldown {

    private final Map<UUID, Long> cooldowns;
    private final long duration;

    public Cooldown(int seconds) {
        this.cooldowns = new ConcurrentHashMap<>();
        this.duration = seconds * 1000L;
    }

    public void apply(UUID playerUUID) {
        this.cooldowns.put(playerUUID, System.currentTimeMillis());
    }

    public void remove(UUID playerUUID) {
        this.cooldowns.remove(playerUUID);
    }

    public boolean isActive(UUID playerUUID) {
        return this.cooldowns.containsKey(playerUUID) && this.cooldowns.get(playerUUID) + this.duration > System.currentTimeMillis();
    }

    public long getRemaining(UUID playerUUID) {
        return (this.cooldowns.getOrDefault(playerUUID, 0L) + this.duration) - System.currentTimeMillis();
    }

    public String getMMSS(UUID playerUUID) {
        return TimeUtils.formatIntoMMSS((int) (getRemaining(playerUUID) / 1000));
    }

    public String getFancy(UUID playerUUID) {
        return TimeUtils.formatIntoDetailedString(getRemaining(playerUUID));
    }

    public String getSeconds(UUID playerUUID) {
        return String.valueOf(getRemaining(playerUUID) / 1000L);
    }

    public String getRemainingDecimal(UUID playerUUID) {
        return getRemaining(getRemaining(playerUUID), true);
    }

    public String applyPlaceholders(String s, Player player) {
        return s
                .replaceAll("%cooldown-duration%", TimeUtils.formatIntoMMSS((int) (getDuration() / 1000)))
                .replaceAll("%cooldown-mmss%", getMMSS(player.getUniqueId()))
                .replaceAll("%cooldown-fancy%", getFancy(player.getUniqueId()))
                .replaceAll("%cooldown-decimal%", getRemainingDecimal(player.getUniqueId()))
                .replaceAll("%cooldown-seconds%", getSeconds(player.getUniqueId()))
                ;
    }

    /**
     * Duration format logic
     */

    public static ThreadLocal<DecimalFormat> REMAINING_SECONDS = new ThreadLocal<DecimalFormat>() {
        @Override
        protected DecimalFormat initialValue() {
            return new DecimalFormat("0.#");
        }
    };

    public static ThreadLocal<DecimalFormat> REMAINING_SECONDS_TRAILING = ThreadLocal.withInitial(() -> new DecimalFormat("0.0"));

    private static long MINUTE = TimeUnit.MINUTES.toMillis(1L);
    private static long HOUR = TimeUnit.HOURS.toMillis(1L);

    public static String getRemaining(long millis, boolean milliseconds) {
        return getRemaining(millis, milliseconds, true);
    }

    public static String getRemaining(long duration, boolean milliseconds, boolean trail) {
        if (milliseconds && duration < MINUTE) {
            return (trail ? REMAINING_SECONDS_TRAILING : REMAINING_SECONDS).get().format(duration * 0.001);
        } else {
            return DurationFormatUtils.formatDuration(duration, (duration >= HOUR ? "HH:" : "") + "mm:ss");
        }
    }

    public void cleanUp() {

        for (Map.Entry<UUID, Long> entry : cooldowns.entrySet()) {
            if (isActive(entry.getKey())) continue;

            cooldowns.remove(entry.getKey());
        }

    }

}
