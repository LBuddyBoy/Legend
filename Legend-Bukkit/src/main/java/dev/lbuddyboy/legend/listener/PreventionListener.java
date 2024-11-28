package dev.lbuddyboy.legend.listener;

import com.google.common.collect.Sets;
import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.util.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;

public class PreventionListener implements Listener {

    @EventHandler
    public void onExplode(ExplosionPrimeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) return;
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPreCommand(PlayerCommandPreprocessEvent event) {

    }

/*
    @EventHandler
    public void onEffect(PotionEffectAddEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        if (event.getEffect().getType() == PotionEffectType.INVISIBILITY) {
            Tasks.runAsync(() -> CommonsPlugin.getInstance().getNametagHandler().reloadPlayer(player));
        }
    }

    @EventHandler
    public void onEffect(PotionEffectExpireEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        if (event.getEffect().getType() == PotionEffectType.INVISIBILITY) {
            Tasks.runAsync(() -> CommonsPlugin.getInstance().getNametagHandler().reloadPlayer(player));
        }
    }

    @EventHandler
    public void onEffect(PotionEffectRemoveEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        if (event.getEffect().getType() == PotionEffectType.INVISIBILITY) {
            Tasks.runAsync(() -> CommonsPlugin.getInstance().getNametagHandler().reloadPlayer(player));
        }
    }
*/

}
