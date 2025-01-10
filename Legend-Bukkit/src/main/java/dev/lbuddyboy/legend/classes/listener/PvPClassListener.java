package dev.lbuddyboy.legend.classes.listener;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.classes.ClassHandler;
import dev.lbuddyboy.legend.classes.PvPClass;
import dev.lbuddyboy.legend.classes.impl.MinerClass;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PvPClassListener implements Listener {

    private final ClassHandler classHandler = LegendBukkit.getInstance().getClassHandler();

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!this.classHandler.isClassApplied(player)) return;

        this.classHandler.getClassApplied(player).remove(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player damager)) return;
        if (event.getFinalDamage() <= 0) return;
        if (!this.classHandler.isClassApplied(victim)) return;
        if (this.classHandler.isClassApplied(victim, MinerClass.class)) return;

        PvPClass clazz = this.classHandler.getClassApplied(victim);

        double modifier = clazz.getConfig().getDouble("settings.damage-modifier.normal");

        if (damager.hasPotionEffect(PotionEffectType.STRENGTH) && damager.getPotionEffect(PotionEffectType.STRENGTH).getAmplifier() == 0) {
            modifier = clazz.getConfig().getDouble("settings.damage-modifier.strength-one");
        } else if (damager.hasPotionEffect(PotionEffectType.STRENGTH) && damager.getPotionEffect(PotionEffectType.STRENGTH).getAmplifier() == 1) {
            modifier = clazz.getConfig().getDouble("settings.damage-modifier.strength-two");
        }

        event.setDamage(event.getFinalDamage() * modifier);
    }

}
