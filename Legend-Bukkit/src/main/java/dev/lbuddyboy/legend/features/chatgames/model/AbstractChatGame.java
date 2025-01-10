package dev.lbuddyboy.legend.features.chatgames.model;

import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.legend.LegendBukkit;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

@Getter
public abstract class AbstractChatGame implements Listener {

    public final Config config;

    public AbstractChatGame() {
        this.config = new Config(LegendBukkit.getInstance(), getId(), LegendBukkit.getInstance().getChatGameHandler().getDirectory());

        this.loadDefaults();
        this.config.options().copyDefaults(true);
        this.config.save();
    }

    public abstract String getId();
    public abstract void loadDefaults();
    public abstract void start();
    public abstract void end(Player winner);
    public abstract boolean isStarted();

}
