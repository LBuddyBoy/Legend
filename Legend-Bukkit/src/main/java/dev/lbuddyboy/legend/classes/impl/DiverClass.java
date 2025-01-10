package dev.lbuddyboy.legend.classes.impl;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.deathmessage.event.CustomPlayerDamageEvent;
import dev.lbuddyboy.commons.deathmessage.objects.PlayerDamage;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.classes.ClassHandler;
import dev.lbuddyboy.legend.classes.PvPClass;
import dev.lbuddyboy.legend.timer.impl.ArcherTagTimer;
import dev.lbuddyboy.legend.util.BukkitUtil;
import dev.lbuddyboy.legend.util.Cooldown;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.WeatherType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class DiverClass extends PvPClass {

    private long lastHeartUsed = 0L;

    private final String DIVER_META = "SHOT_BY_DIVER";
    private final String DIVER_DISTANCE_META = "SHOT_BY_DIVER_DISTANCE";
    private final ClassHandler classHandler = LegendBukkit.getInstance().getClassHandler();

    private final Cooldown heartOfTheSeaCooldown;
    private final Cooldown dolphinsGraceCooldown;

    public DiverClass() {
        super();

        this.heartOfTheSeaCooldown = new Cooldown(getConfig().getInt("settings.heart_of_the_sea.cooldown"));
        this.dolphinsGraceCooldown = new Cooldown(getConfig().getInt("settings.dolphins_grace.cooldown"));
    }

    @Override
    public void loadDefaults() {
        this.config.addDefault("effects.regeneration", 0);
        this.config.addDefault("effects.resistance", 0);
        this.config.addDefault("effects.speed", 0);
        this.config.addDefault("settings.distance.blocks", 10);
        this.config.addDefault("settings.distance.multiplier", 1.20D);
        this.config.addDefault("settings.damage-modifier.normal", 1.0D);
        this.config.addDefault("settings.damage-modifier.strength-one", 1.0D);
        this.config.addDefault("settings.damage-modifier.strength-two", 1.0D);
        this.config.addDefault("settings.limit", 1);
        this.config.addDefault("settings.warmup", 3);
        this.config.addDefault("settings.enabled", true);

        this.config.addDefault("settings.dolphins_grace.power", 4);
        this.config.addDefault("settings.dolphins_grace.duration", 5);
        this.config.addDefault("settings.dolphins_grace.cooldown", 600);

        this.config.addDefault("settings.heart_of_the_sea.power", 4);
        this.config.addDefault("settings.heart_of_the_sea.duration", 600);
        this.config.addDefault("settings.heart_of_the_sea.cooldown", 3600);

        this.config.addDefault("scoreboard.heart_of_the_sea", "&fRain: &b%cooldown-mmss%");
        this.config.addDefault("scoreboard.dolphins_grace", "&fDolphins Grace: &e%cooldown-mmss%");

        this.config.addDefault("lang.limited", "<blend:&3;&b>&lDIVER</> &cClass couldn't be applied because your team has too many.");
        this.config.addDefault("lang.dolphins_grace.used", "<blend:&3;&b>&lDIVER ABILITY</> &7» &aSuccessfully used the Dolphins Grace diver ability!");
        this.config.addDefault("lang.dolphins_grace.cooldown", "<blend:&3;&b>&lDIVER ABILITY</> &7» &cYou are on dolphins grace cooldown for %cooldown-decimal%s");
        this.config.addDefault("lang.heart_of_the_sea.used", "<blend:&3;&b>&lDIVER ABILITY</> &7» &aSuccessfully used the Heart of the Sea diver ability!");
        this.config.addDefault("lang.heart_of_the_sea.cooldown", "<blend:&3;&b>&lDIVER ABILITY</> &7» &cYou are on heart of the sea cooldown for %cooldown-decimal%s");
        this.config.addDefault("lang.shot.victim", "<blend:&3;&b>&lDIVER PIERCING</> &7» &eYou have been diver pierced by %shooter%! It dealt %damage% hearts!");
        this.config.addDefault("lang.shot.shooter", "<blend:&3;&b>&lDIVER PIERCING</> &7» &eYou pierced %victim% with your trident! It dealt %damage% hearts!");
        this.config.addDefault("lang.applied", "<blend:&3;&b>&lDIVER</> &7» &aThe diver class is now enabled! Shoot players to pierce through their protection!");
        this.config.addDefault("lang.removed", "<blend:&3;&b>&lDIVER</> &7» &cThe diver class is now disabled!");
        this.config.addDefault("lang.warming", "<blend:&3;&b>&lDIVER</> &7» &eThe diver class is warming up!");
    }

    @Override
    public String getName() {
        return "Diver";
    }

    @Override
    public String getDisplayName() {
        return "";
    }

    @Override
    public boolean hasSetOn(Player player) {
        return player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType() == Material.TURTLE_HELMET &&
                player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType() == Material.DIAMOND_CHESTPLATE &&
                player.getInventory().getLeggings() != null && player.getInventory().getLeggings().getType() == Material.DIAMOND_LEGGINGS &&
                player.getInventory().getBoots() != null && player.getInventory().getBoots().getType() == Material.DIAMOND_BOOTS;
    }

    @Override
    public boolean isTickable() {
        return false;
    }

    @Override
    public boolean apply(Player player) {
        if (!super.apply(player)) return false;

        ConfigurationSection section = this.config.getConfigurationSection("effects");

        if (section != null) {
            section.getKeys(false).forEach(key -> {
                PotionEffectType type = Registry.EFFECT.match(key);
                if (type == null) return;
                int power = section.getInt(key);
                if (power <= 0) return;

                player.addPotionEffect(new PotionEffect(type, PotionEffect.INFINITE_DURATION, power - 1));
            });
        }

        return true;
    }

    @Override
    public void remove(Player player) {
        ConfigurationSection section = this.config.getConfigurationSection("effects");

        if (section != null) {
            section.getKeys(false).forEach(key -> {
                PotionEffectType type = Registry.EFFECT.match(key);
                if (type == null) return;

                player.removePotionEffect(type);
            });
        }

        super.remove(player);
    }

    @Override
    public List<String> getScoreboardLines(Player player) {
        List<String> lines = new ArrayList<>();

        if (heartOfTheSeaCooldown.isActive(player.getUniqueId())) {
            lines.add(heartOfTheSeaCooldown.applyPlaceholders(this.config.getString("scoreboard.heart_of_the_sea"), player));
        }

        if (dolphinsGraceCooldown.isActive(player.getUniqueId())) {
            lines.add(dolphinsGraceCooldown.applyPlaceholders(this.config.getString("scoreboard.dolphins_grace"), player));
        }

        return lines;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(CustomPlayerDamageEvent customPlayerDamageEvent) {
        if (!(customPlayerDamageEvent.getCause() instanceof EntityDamageByEntityEvent event)) return;
        if (!(event.getDamager() instanceof Trident trident)) return;
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getFinalDamage() <= 0) return;

        Player victim = (Player) event.getEntity();
        Player shooter = BukkitUtil.getDamager(event);

        if (shooter == null) return;
        if (!event.getDamager().hasMetadata(DIVER_META)) return;
        if (!this.classHandler.isClassApplied(shooter, DiverClass.class)) return;

        float force = event.getDamager().getMetadata(DIVER_META).getFirst().asFloat();
        Location shotFrom = (Location) event.getDamager().getMetadata(DIVER_DISTANCE_META).getFirst().value();
        double distance = 0.0D;
        double multiplier = 1.0D;

        if (shotFrom != null) {
            distance = shotFrom.distance(victim.getLocation());

            if (distance >= this.config.getInt("settings.distance.blocks")) {
                multiplier = this.config.getDouble("settings.distance.multiplier");
            }
        }

        event.setDamage(event.getFinalDamage() * multiplier);

        victim.sendMessage(CC.translate(this.config.getString("lang.shot.victim")
                .replaceAll("%damage%", APIConstants.formatNumber(event.getDamage() / 2))
                .replaceAll("%shooter%", shooter.getName())));
        shooter.sendMessage(CC.translate(this.config.getString("lang.shot.shooter")
                .replaceAll("%distance%", APIConstants.formatNumber(distance / 2))
                .replaceAll("%damage%", APIConstants.formatNumber(event.getDamage()))
                .replaceAll("%victim%", victim.getName())));

        customPlayerDamageEvent.setTrackerDamage(new DiverDamage(victim.getName(), event.getDamage(), shooter.getName(), distance));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBowShoot(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Trident trident)) return;
        if (!(trident.getShooter() instanceof Player shooter)) return;
        if (!this.classHandler.isClassApplied(shooter, DiverClass.class)) {
            event.setCancelled(true);
            return;
        }

        trident.setMetadata(DIVER_META, new FixedMetadataValue(LegendBukkit.getInstance(), 0));
        trident.setMetadata(DIVER_DISTANCE_META, new FixedMetadataValue(LegendBukkit.getInstance(), shooter.getLocation()));
    }

    @EventHandler
    public void onClickHeart(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (item == null || item.getType() != Material.HEART_OF_THE_SEA) return;
        if (!this.classHandler.isClassApplied(player, this.getClass())) return;

        if (this.heartOfTheSeaCooldown.isActive(player.getUniqueId())) {
            player.sendMessage(CC.translate(this.heartOfTheSeaCooldown.applyPlaceholders(this.config.getString("lang.heart_of_the_sea.cooldown"), player)));
            return;
        }

        int power = this.config.getInt("settings.heart_of_the_sea.power");
        if (power <= 0) return;

        int duration = this.config.getInt("settings.heart_of_the_sea.duration");

        this.lastHeartUsed = System.currentTimeMillis() + (duration * 1000L);
        player.getWorld().setWeatherDuration(20 * duration);
        player.getWorld().setStorm(true);
        heartOfTheSeaCooldown.apply(player.getUniqueId());
        player.getInventory().setItem(event.getHand(), ItemUtils.takeItem(item));
        player.sendMessage(CC.translate(this.config.getString("lang.heart_of_the_sea.used")));
        // TODO Add restore effects
    }

    @EventHandler
    public void onClickJump(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (item == null || item.getType() != Material.PRISMARINE_SHARD) return;
        if (!this.classHandler.isClassApplied(player, this.getClass())) return;

        if (this.dolphinsGraceCooldown.isActive(player.getUniqueId())) {
            player.sendMessage(CC.translate(this.dolphinsGraceCooldown.applyPlaceholders(this.config.getString("lang.dolphins_grace.cooldown"), player)));
            return;
        }

        int power = this.config.getInt("settings.dolphins_grace.power");
        if (power <= 0) return;

        int duration = this.config.getInt("settings.dolphins_grace.duration");

        player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 20 * duration, power - 1));
        dolphinsGraceCooldown.apply(player.getUniqueId());
        player.getInventory().setItem(event.getHand(), ItemUtils.takeItem(item));
        player.sendMessage(CC.translate(this.config.getString("lang.dolphins_grace.used")));
        // TODO Add restore effects
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.heartOfTheSeaCooldown.cleanUp();
        this.dolphinsGraceCooldown.cleanUp();
    }

    public static class DiverDamage extends PlayerDamage {

        private final double distance;

        public DiverDamage(String damaged, double damage, String damager, double distance) {
            super(damaged, damage, damager);

            this.distance = distance;
        }

        public String getDeathMessage() {
            return (wrapName(getDamaged()) + " " + provider.getDeathMessageFormat("was impaled by") + " " + wrapName(getDamager()) + provider.getDeathMessageFormat(" from " + APIConstants.formatNumber(this.distance) + " blocks away"));
        }

    }

}
