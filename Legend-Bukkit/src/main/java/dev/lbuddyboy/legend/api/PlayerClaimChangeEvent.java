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
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
@Getter
public class PlayerClaimChangeEvent extends Event implements Cancellable {

    @Getter
    public static HandlerList handlerList = new HandlerList();

    private final Player player;
    @Nullable private final Team toTeam;
    @Nullable private final Team fromTeam;

    @Setter
    private boolean cancelled = false;

    @NonNull
    public HandlerList getHandlers() {
        return handlerList;
    }

}
