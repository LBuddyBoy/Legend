package dev.lbuddyboy.legend.api;

import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.util.ReflectionUtils;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Setter
@Getter
public class TeamSetRaidableEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private boolean cancelled;
    private final Team team;
    private final UUID victimUUID;
    private final UUID killerUUID;

    public TeamSetRaidableEvent(Team team, UUID victimUUID, UUID killerUUID) {
        this.team = team;
        this.victimUUID = victimUUID;
        this.killerUUID = killerUUID;
    }

    @Override
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return TeamSetRaidableEvent.HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return TeamSetRaidableEvent.HANDLERS_LIST;
    }
}