package dev.lbuddyboy.legend.features.limiter;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.limiter.listener.EnchantLimitListener;
import dev.lbuddyboy.legend.features.limiter.listener.PotionLimitListener;
import org.bukkit.Bukkit;

public class LimiterHandler implements IModule {

    @Override
    public void load() {
        Bukkit.getServer().getPluginManager().registerEvents(new EnchantLimitListener(), LegendBukkit.getInstance());
        Bukkit.getServer().getPluginManager().registerEvents(new PotionLimitListener(), LegendBukkit.getInstance());
    }

    @Override
    public void unload() {

    }

}
