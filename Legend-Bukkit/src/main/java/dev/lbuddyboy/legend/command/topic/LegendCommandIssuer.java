package dev.lbuddyboy.legend.command.topic;

import co.aikar.commands.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class LegendCommandIssuer implements CommandIssuer {

    private final BukkitCommandManager manager;
    private final CommandSender sender;

    public LegendCommandIssuer(BukkitCommandManager manager, CommandSender sender) {
        this.manager = manager;
        this.sender = sender;
    }

    public boolean isPlayer() {
        return this.sender instanceof Player;
    }

    public CommandSender getIssuer() {
        return this.sender;
    }

    public Player getPlayer() {
        return this.isPlayer() ? (Player)this.sender : null;
    }

    public @NotNull UUID getUniqueId() {
        return this.isPlayer() ? ((Player)this.sender).getUniqueId() : UUID.nameUUIDFromBytes(this.sender.getName().getBytes(StandardCharsets.UTF_8));
    }

    public CommandManager getManager() {
        return this.manager;
    }

    public void sendMessageInternal(String message) {
        this.sender.sendMessage(ACFBukkitUtil.color(message));
    }

    public boolean hasPermission(String name) {
        return this.sender.hasPermission(name);
    }

}
