package dev.lbuddyboy.legend.actionbar;

import dev.lbuddyboy.commons.actionbar.ActionBarProvider;
import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.classes.PvPClass;
import dev.lbuddyboy.legend.timer.PlayerTimer;
import dev.lbuddyboy.legend.user.model.LegendUser;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class LegendActionBar implements ActionBarProvider {

    @Override
    public String getActionBar(Player player) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (!user.getQueuedActionBars().isEmpty()) {
            String actionBar = user.getQueuedActionBars().getFirst();

            if (user.getActionBarDelay() < System.currentTimeMillis()) {
                user.setActionBarDelay(System.currentTimeMillis() + 1_000L);
                user.getQueuedActionBars().remove(actionBar);
            }

            return actionBar;
        }

        List<PlayerTimer> timers = LegendBukkit.getInstance().getTimerHandler().getActionBarTimers().stream().filter(t -> t.isActive(player.getUniqueId())).collect(Collectors.toList());
        List<String> actionBar = timers.stream().map(t -> t.format(player.getUniqueId(), LegendBukkit.getInstance().getLanguage().getString("action-bar.format"))).collect(Collectors.toList());
        String separator = LegendBukkit.getInstance().getLanguage().getString("action-bar.separator");

        return StringUtils.join(
                actionBar,
                separator
        );
    }

    @Override
    public boolean qualifies(Player player) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());
        List<PlayerTimer> timers = LegendBukkit.getInstance().getTimerHandler().getActionBarTimers().stream().filter(t -> t.isActive(player.getUniqueId())).collect(Collectors.toList());

        return !timers.isEmpty() || !user.getQueuedActionBars().isEmpty();
    }

}
