package dev.lbuddyboy.legend.api;

import io.netty.channel.Channel;
import org.bukkit.entity.Player;

public final class PacketSendEvent extends PacketEvent {

    public PacketSendEvent(final Player player, final Channel channel, final Object packet) {
        super(player, channel, packet);
    }

}