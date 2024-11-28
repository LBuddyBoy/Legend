package dev.lbuddyboy.legend.api;

import dev.lbuddyboy.legend.team.model.Team;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
@Getter
public class TeamMemberDeathEvent extends Event implements Cancellable {

    @Getter
    public static HandlerList handlerList = new HandlerList();

    private final Player player;
    private final Team team;

    @Setter
    private boolean cancelled = false;

    @NonNull
    public HandlerList getHandlers() {
        return handlerList;
    }

}
