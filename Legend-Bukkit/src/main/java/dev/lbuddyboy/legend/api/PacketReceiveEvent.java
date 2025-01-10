package dev.lbuddyboy.legend.api;

import io.netty.channel.Channel;
import org.bukkit.entity.Player;

public final class PacketReceiveEvent extends PacketEvent {

    public PacketReceiveEvent(final Player player, final Channel channel, final Object packet) {
        super(player, channel, packet);
    }
}