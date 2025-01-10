package dev.lbuddyboy.legend.classes.impl;

import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.classes.ClassHandler;
import dev.lbuddyboy.legend.classes.PvPClass;
import dev.lbuddyboy.legend.timer.impl.ArcherTagTimer;
import dev.lbuddyboy.legend.util.BukkitUtil;
import dev.lbuddyboy.legend.util.Cooldown;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class MinerClass extends PvPClass {

    @Override
    public void loadDefaults() {
        this.config.addDefault("settings.limit", -1);
        this.config.addDefault("settings.warmup", 0);
        this.config.addDefault("settings.enabled", true);

        this.config.addDefault("lang.limited", "<blend:&7;&f>&lMINER</> &7» &cClass couldn't be applied because your team has too many.");
        this.config.addDefault("lang.applied", "<blend:&7;&f>&lMINER</> &7» &aThe miner class is now enabled! Mine your heart out!");
        this.config.addDefault("lang.removed", "<blend:&7;&f>&lMINER</> &7» &cThe miner class is now disabled!");
        this.config.addDefault("lang.warming", "<blend:&7;&f>&lMINER</> &7» &eThe miner class is warming up!");
    }

    @Override
    public String getName() {
        return "Miner";
    }

    @Override
    public String getDisplayName() {
        return "";
    }

    @Override
    public boolean hasSetOn(Player player) {
        return player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType() == Material.IRON_HELMET &&
                player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType() == Material.IRON_CHESTPLATE &&
                player.getInventory().getLeggings() != null && player.getInventory().getLeggings().getType() == Material.IRON_LEGGINGS &&
                player.getInventory().getBoots() != null && player.getInventory().getBoots().getType() == Material.IRON_BOOTS;
    }

    @Override
    public boolean isTickable() {
        return true;
    }

    @Override
    public boolean apply(Player player) {
        if (!super.apply(player)) return false;

        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, PotionEffect.INFINITE_DURATION, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, PotionEffect.INFINITE_DURATION, 1));
        return true;
    }

    @Override
    public void remove(Player player) {
        player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        player.removePotionEffect(PotionEffectType.HASTE);
        super.remove(player);
    }

    @Override
    public List<String> getScoreboardLines(Player player) {
        List<String> lines = new ArrayList<>();



        return lines;
    }

    @Override
    public void tick(Player player) {
        if (player.getLocation().getY() <= 20) {
            if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 1));
            }
        } else {
            if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
            }
        }
    }
}
