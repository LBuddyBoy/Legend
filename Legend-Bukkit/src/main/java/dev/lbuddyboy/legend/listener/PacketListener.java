package dev.lbuddyboy.legend.listener;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.api.PacketReceiveEvent;
import dev.lbuddyboy.legend.api.PacketSendEvent;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.game.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.LevelEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class PacketListener implements Listener {

    public static boolean LOG_PACKETS = false;
    public static Map<String, Long> SOUND_LOG_DELAY = new HashMap<>();
    public static Map<Class, Long> SEND_LOG_DELAY = new HashMap<>();
    public static Map<Class, Long> RECEIVE_LOG_DELAY = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        LegendBukkit.getInstance().getPacketHandler().injectPlayer(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        LegendBukkit.getInstance().getPacketHandler().removePlayer(player);
    }

    @EventHandler
    public void onReceive(PacketReceiveEvent event) {
        if (LOG_PACKETS) {
            if (RECEIVE_LOG_DELAY.getOrDefault(event.getPacket().getClass(), 0L) + 1_000L < System.currentTimeMillis()) {
                System.out.println("RECEIVE EVENT: " + event.getPacket().getClass());
            }
            RECEIVE_LOG_DELAY.put(event.getPacket().getClass(), System.currentTimeMillis());
        } else {
            if (!RECEIVE_LOG_DELAY.isEmpty()) {
                RECEIVE_LOG_DELAY.clear();
            }
        }
    }

    @EventHandler
    public void onSend(PacketSendEvent event) {
        if (LOG_PACKETS) {
            if (SEND_LOG_DELAY.getOrDefault(event.getPacket().getClass(), 0L) + 1_000L < System.currentTimeMillis()) {
                System.out.println("SEND EVENT: " + event.getPacket().getClass());
            }
            SEND_LOG_DELAY.put(event.getPacket().getClass(), System.currentTimeMillis());
        } else {
            if (!SEND_LOG_DELAY.isEmpty()) {
                SEND_LOG_DELAY.clear();
            }
        }
    }

    @EventHandler
    public void onSwingSound(PacketSendEvent event) {
        if (!(event.getPacket() instanceof ClientboundSoundPacket packet)) return;
        if (!SettingsConfig.SETTINGS_DISABLE_SWING_SOUND.getBoolean()) return;
        if (!packet.getSound().value().equals(SoundEvents.PLAYER_ATTACK_NODAMAGE)
                && !packet.getSound().value().equals(SoundEvents.PLAYER_ATTACK_SWEEP)
                && !packet.getSound().value().equals(SoundEvents.PLAYER_ATTACK_WEAK)
                && !packet.getSound().value().equals(SoundEvents.PLAYER_ATTACK_KNOCKBACK)
                && !packet.getSound().value().equals(SoundEvents.PLAYER_ATTACK_CRIT)
        ) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPortalCreateSound(PacketSendEvent event) {
        if (!(event.getPacket() instanceof ClientboundLevelEventPacket packet)) return;
        if (packet.getType() != LevelEvent.SOUND_END_PORTAL_SPAWN) return;
        if (!SettingsConfig.SETTINGS_DISABLE_END_PORTAL_CREATE_SOUND.getBoolean()) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PacketSendEvent event) {
        if (!(event.getPacket() instanceof ServerboundInteractPacket packet)) return;
        if (!packet.isAttack()) return;
        if (SettingsConfig.SETTINGS_DISABLE_SPRINT_FIX.getBoolean()) return;

        event.setCancelled(true);
    }

}
