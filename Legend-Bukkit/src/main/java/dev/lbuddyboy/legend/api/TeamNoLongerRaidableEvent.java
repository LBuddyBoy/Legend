package dev.lbuddyboy.legend.api;

import dev.lbuddyboy.legend.team.model.Team;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Setter
@Getter
public class TeamNoLongerRaidableEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private boolean cancelled;
    private final Team team;

    public TeamNoLongerRaidableEvent(Team team) {
        this.team = team;
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
        return TeamNoLongerRaidableEvent.HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return TeamNoLongerRaidableEvent.HANDLERS_LIST;
    }
}