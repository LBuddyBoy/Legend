package dev.lbuddyboy.legend.api;

import dev.lbuddyboy.legend.util.ReflectionUtils;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Setter
@Getter
public abstract class PacketEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private boolean cancelled;

    private final Player player;
    private final Channel channel;

    private Object packet;

    public PacketEvent(final Player player, final Channel channel, final Object packet) {
        super(true);

        this.player = player;
        this.channel = channel;
        this.packet = packet;
    }

    @Override
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setValue(final String field, final Object value) {
        ReflectionUtils.setFieldValue(this.packet, field, value);
    }

    public void setValue(Object object, String field, final Object value) {
        ReflectionUtils.setFieldValue(object, field, value);
    }

    public Object getValue(final String field) {
        return ReflectionUtils.getFieldValue(this.packet, field);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return PacketEvent.HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return PacketEvent.HANDLERS_LIST;
    }
}