package dev.lbuddyboy.legend.features.gateways;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.gateways.listener.GatewayListener;
import dev.lbuddyboy.legend.features.gateways.listener.SelectionListeners;
import dev.lbuddyboy.legend.features.gateways.model.Gateway;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Getter
public class GatewayHandler implements IModule {

    private Map<String, Gateway> gateways;
    private File directory;

    @Override
    public void load() {
        this.gateways = new HashMap<>();
        this.directory = new File(LegendBukkit.getInstance().getDataFolder(), "gateways");
        if (!this.directory.exists()) this.directory.mkdir();

        for (String fileNameYML : this.directory.list()) {
            String fileName = fileNameYML.replaceAll(".yml", "");
            Config config = new Config(LegendBukkit.getInstance(), fileName, this.directory);

            this.gateways.put(fileName, new Gateway(config));
        }

        LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(new GatewayListener(), LegendBukkit.getInstance());
        LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(new SelectionListeners(), LegendBukkit.getInstance());
    }

    @Override
    public void unload() {

    }
}
