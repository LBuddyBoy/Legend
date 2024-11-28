package dev.lbuddyboy.legend.timer.impl;

import dev.lbuddyboy.legend.timer.PlayerTimer;
import dev.lbuddyboy.legend.util.BukkitUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ClassWarmUpTimer extends PlayerTimer {

    @Override
    public String getId() {
        return "class-warmup";
    }

}
