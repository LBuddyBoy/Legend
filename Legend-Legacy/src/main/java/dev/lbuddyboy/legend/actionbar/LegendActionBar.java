package dev.lbuddyboy.legend.actionbar;

import dev.lbuddyboy.commons.actionbar.ActionBarProvider;
import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.timer.PlayerTimer;
import org.bukkit.entity.Player;

import java.util.List;

public class LegendActionBar implements ActionBarProvider {

    @Override
    public String getActionBar(Player player) {
        List<PlayerTimer> timers = LegendBukkit.getInstance().getTimerHandler().getActionBarTimers().stream().filter(t -> t.isActive(player.getUniqueId())).toList();
        String separator = LegendBukkit.getInstance().getLanguage().getString("action-bar.separator");

        return StringUtils.join(
                timers.stream().map(t -> t.format(player.getUniqueId(), LegendBukkit.getInstance().getLanguage().getString("action-bar.format"))).toList(),
                separator
        );
    }

    @Override
    public boolean qualifies(Player player) {
        List<PlayerTimer> timers = LegendBukkit.getInstance().getTimerHandler().getActionBarTimers().stream().filter(t -> t.isActive(player.getUniqueId())).toList();

        return !timers.isEmpty();
    }
}
