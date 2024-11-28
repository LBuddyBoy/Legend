package dev.lbuddyboy.legend.features.limiter;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.limiter.listener.EnchantLimitListener;
import dev.lbuddyboy.legend.features.limiter.listener.PotionLimitListener;
import lombok.Getter;
import org.bukkit.Bukkit;

@Getter
public class LimiterHandler implements IModule {

    private Config config;

    @Override
    public void load() {
        this.config = new Config(LegendBukkit.getInstance(), "lang");

        if (this.config.getBoolean("enchant-limiter", true)) {
            Bukkit.getServer().getPluginManager().registerEvents(new EnchantLimitListener(), LegendBukkit.getInstance());
        }
        if (this.config.getBoolean("potion-limiter", true)) {
            Bukkit.getServer().getPluginManager().registerEvents(new PotionLimitListener(), LegendBukkit.getInstance());
        }
    }

    @Override
    public void unload() {

    }

}
