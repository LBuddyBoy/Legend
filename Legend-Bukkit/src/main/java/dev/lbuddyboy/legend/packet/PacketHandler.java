package dev.lbuddyboy.legend.packet;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.api.PacketReceiveEvent;
import dev.lbuddyboy.legend.api.PacketSendEvent;
import dev.lbuddyboy.legend.listener.PacketListener;
import io.netty.channel.*;
import lombok.SneakyThrows;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketHandler implements IModule {

    @Override
    public void load() {
        LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(new PacketListener(), LegendBukkit.getInstance());
    }

    @Override
    public void unload() {

    }
    
    public void removePlayer(Player player) {
        ChannelPipeline channelPipeline = getChannel(player).pipeline();

        if (channelPipeline.names().contains(player.getName())) {
            channelPipeline.remove(player.getName());
        }
    }

    public void injectPlayer(Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {

            @Override
            public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
                Player player = Bukkit.getPlayer(context.name());
                if (player == null) {
                    super.channelRead(context, packet);
                    return;
                }

                PacketReceiveEvent event = new PacketReceiveEvent(player, context.channel(), packet);
                Bukkit.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return;
                }

                super.channelRead(context, event.getPacket());
            }

            @Override
            public void write(ChannelHandlerContext context, Object packet, ChannelPromise promise) throws Exception {
                Player player = Bukkit.getPlayer(context.name());
                if (player == null) {
                    super.write(context, packet, promise);
                    return;
                }

                PacketSendEvent event = new PacketSendEvent(player, context.channel(), packet);
                Bukkit.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return;
                }

                super.write(context, event.getPacket(), promise);
            }
        };

        ChannelPipeline channelPipeline = getChannel(player).pipeline();
        channelPipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
    }

    @SneakyThrows
    private static Channel getChannel(Player player) {
        ServerGamePacketListenerImpl listener = ((CraftPlayer) player).getHandle().connection;

        return listener.connection.channel;
    }
    
}