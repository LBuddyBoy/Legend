package dev.lbuddyboy.legend.classes.impl;

import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.deathmessage.event.CustomPlayerDamageEvent;
import dev.lbuddyboy.commons.deathmessage.objects.PlayerDamage;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LangConfig;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.classes.ClassHandler;
import dev.lbuddyboy.legend.classes.PvPClass;
import dev.lbuddyboy.legend.timer.impl.ArcherTagTimer;
import dev.lbuddyboy.legend.util.BukkitUtil;
import dev.lbuddyboy.legend.util.Cooldown;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RogueClass extends PvPClass {

    private final ClassHandler classHandler = LegendBukkit.getInstance().getClassHandler();
    private final Cooldown speedCooldown;
    private final Cooldown jumpCooldown;
    private final Cooldown leviCooldown;
    private final Cooldown resistanceCooldown;
    private final Cooldown backstabCooldown;

    public RogueClass() {
        super();

        this.speedCooldown = new Cooldown(getConfig().getInt("settings.speed.cooldown"));
        this.jumpCooldown = new Cooldown(getConfig().getInt("settings.jump.cooldown"));
        this.leviCooldown = new Cooldown(getConfig().getInt("settings.levitation.cooldown"));
        this.resistanceCooldown = new Cooldown(getConfig().getInt("settings.resistance.cooldown"));
        this.backstabCooldown = new Cooldown(getConfig().getInt("settings.backStabCooldown"));
    }

    @Override
    public void loadDefaults() {
        this.config.addDefault("effects.regeneration", 2);
        this.config.addDefault("effects.resistance", 1);
        this.config.addDefault("effects.speed", 3);
        this.config.addDefault("settings.limit", 1);
        this.config.addDefault("settings.backStabCooldown", 3);
        this.config.addDefault("settings.damage-modifier.normal", 1.0D);
        this.config.addDefault("settings.damage-modifier.strength-one", 1.0D);
        this.config.addDefault("settings.damage-modifier.strength-two", 1.0D);
        this.config.addDefault("settings.warmup", 3);
        this.config.addDefault("settings.enabled", true);

        this.config.addDefault("settings.speed.power", 4);
        this.config.addDefault("settings.speed.duration", 5);
        this.config.addDefault("settings.speed.cooldown", 45);

        this.config.addDefault("settings.jump.power", 7);
        this.config.addDefault("settings.jump.duration", 5);
        this.config.addDefault("settings.jump.cooldown", 60);

        this.config.addDefault("settings.levitation.power", 5);
        this.config.addDefault("settings.levitation.duration", 5);
        this.config.addDefault("settings.levitation.cooldown", 60);

        this.config.addDefault("settings.resistance.power", 3);
        this.config.addDefault("settings.resistance.duration", 5);
        this.config.addDefault("settings.resistance.cooldown", 60);

        this.config.addDefault("scoreboard.speed", "&fSpeed: &e%cooldown-mmss%");
        this.config.addDefault("scoreboard.jump", "&fJump: &a%cooldown-mmss%");
        this.config.addDefault("scoreboard.levitation", "&fLevitation: &d%cooldown-mmss%");
        this.config.addDefault("scoreboard.resistance", "&fResistance: &e%cooldown-mmss%");

        this.config.addDefault("lang.limited", "<blend:&8;&7>&lROGUE</> &7» &cClass couldn't be applied because your team has too many.");
        this.config.addDefault("lang.speed.used", "<blend:&8;&7>&lROGUE ABILITY</> &7» &aSuccessfully used the Speed IV rogue ability!");
        this.config.addDefault("lang.speed.cooldown", "<blend:&8;&7>&lROGUE ABILITY</> &7» &cYou are on speed cooldown for %cooldown-decimal%s");
        this.config.addDefault("lang.jump.used", "<blend:&8;&7>&lROGUE ABILITY</> &7» &aSuccessfully used the Jump VII rogue ability!");
        this.config.addDefault("lang.jump.cooldown", "<blend:&8;&7>&lROGUE ABILITY</> &7» &cYou are on jump boost cooldown for %cooldown-decimal%s");
        this.config.addDefault("lang.levitation.used", "<blend:&8;&7>&lROGUE ABILITY</> &7» &aSuccessfully used the Levitation V rogue ability!");
        this.config.addDefault("lang.levitation.cooldown", "<blend:&8;&7>&lROGUE ABILITY</> &7» &cYou are on levitation cooldown for %cooldown-decimal%s");
        this.config.addDefault("lang.resistance.used", "<blend:&8;&7>&lROGUE ABILITY</> &7» &aSuccessfully used the Resistance III rogue ability!");
        this.config.addDefault("lang.resistance.cooldown", "<blend:&8;&7>&lROGUE ABILITY</> &7» &cYou are on resistance cooldown for %cooldown-decimal%s");
        this.config.addDefault("lang.applied", "<blend:&8;&7>&lROGUE</> &7» &aThe rogue class is now enabled! Hit players from behind with a gold sword to deal 4 hearts!");
        this.config.addDefault("lang.removed", "<blend:&8;&7>&lROGUE</> &7» &cThe rogue class is now disabled!");
        this.config.addDefault("lang.warming", "<blend:&8;&7>&lROGUE</> &7» &eThe rogue class is warming up!");
    }

    @Override
    public String getName() {
        return "Rogue";
    }

    @Override
    public String getDisplayName() {
        return "";
    }

    @Override
    public boolean hasSetOn(Player player) {
        return player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType() == Material.CHAINMAIL_HELMET &&
                player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType() == Material.CHAINMAIL_CHESTPLATE &&
                player.getInventory().getLeggings() != null && player.getInventory().getLeggings().getType() == Material.CHAINMAIL_LEGGINGS &&
                player.getInventory().getBoots() != null && player.getInventory().getBoots().getType() == Material.CHAINMAIL_BOOTS;
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

                player.addPotionEffect(new PotionEffect(type, PotionEffect.INFINITE_DURATION,  power - 1));
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

        if (speedCooldown.isActive(player.getUniqueId())) {
            lines.add(speedCooldown.applyPlaceholders(this.config.getString("scoreboard.speed"), player));
        }

        if (jumpCooldown.isActive(player.getUniqueId())) {
            lines.add(jumpCooldown.applyPlaceholders(this.config.getString("scoreboard.jump"), player));
        }

        if (leviCooldown.isActive(player.getUniqueId())) {
            lines.add(leviCooldown.applyPlaceholders(this.config.getString("scoreboard.levitation"), player));
        }

        if (resistanceCooldown.isActive(player.getUniqueId())) {
            lines.add(resistanceCooldown.applyPlaceholders(this.config.getString("scoreboard.resistance"), player));
        }

        return lines;
    }

    @EventHandler
    public void onClickSpeed(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (item == null || item.getType() != Material.SUGAR) return;
        if (!this.classHandler.isClassApplied(player, this.getClass())) return;

        if (this.speedCooldown.isActive(player.getUniqueId())) {
            player.sendMessage(CC.translate(this.speedCooldown.applyPlaceholders(this.config.getString("lang.speed.cooldown"), player)));
            return;
        }

        int power = this.config.getInt("settings.speed.power");
        if (power <= 0) return;

        int duration = this.config.getInt("settings.speed.duration");

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * duration, power - 1));
        speedCooldown.apply(player.getUniqueId());
        player.getInventory().setItem(event.getHand(), ItemUtils.takeItem(item));
        player.sendMessage(CC.translate(this.config.getString("lang.speed.used")));
        // TODO Add restore effects
    }

    @EventHandler
    public void onClickJump(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (item == null || item.getType() != Material.RABBIT_FOOT) return;
        if (!this.classHandler.isClassApplied(player, this.getClass())) return;

        if (this.jumpCooldown.isActive(player.getUniqueId())) {
            player.sendMessage(CC.translate(this.jumpCooldown.applyPlaceholders(this.config.getString("lang.jump.cooldown"), player)));
            return;
        }

        int power = this.config.getInt("settings.jump.power");
        if (power <= 0) return;

        int duration = this.config.getInt("settings.jump.duration");

        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 20 * duration, power - 1));
        jumpCooldown.apply(player.getUniqueId());
        player.getInventory().setItem(event.getHand(), ItemUtils.takeItem(item));
        player.sendMessage(CC.translate(this.config.getString("lang.jump.used")));
        // TODO Add restore effects
    }

    @EventHandler
    public void onClickLevi(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (item == null || item.getType() != Material.PHANTOM_MEMBRANE) return;
        if (!this.classHandler.isClassApplied(player, this.getClass())) return;

        if (this.leviCooldown.isActive(player.getUniqueId())) {
            player.sendMessage(CC.translate(this.leviCooldown.applyPlaceholders(this.config.getString("lang.levitation.cooldown"), player)));
            return;
        }

        int power = this.config.getInt("settings.levitation.power");
        if (power <= 0) return;

        int duration = this.config.getInt("settings.levitation.duration");

        player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20 * duration, power - 1));
        leviCooldown.apply(player.getUniqueId());
        player.getInventory().setItem(event.getHand(), ItemUtils.takeItem(item));
        player.sendMessage(CC.translate(this.config.getString("lang.levitation.used")));
        // TODO Add restore effects
    }

    @EventHandler
    public void onClickRes(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (item == null || item.getType() != Material.IRON_INGOT) return;
        if (!this.classHandler.isClassApplied(player, this.getClass())) return;

        if (this.resistanceCooldown.isActive(player.getUniqueId())) {
            player.sendMessage(CC.translate(this.resistanceCooldown.applyPlaceholders(this.config.getString("lang.resistance.cooldown"), player)));
            return;
        }

        int power = this.config.getInt("settings.resistance.power");
        if (power <= 0) return;

        int duration = this.config.getInt("settings.resistance.duration");

        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * duration, power - 1));
        resistanceCooldown.apply(player.getUniqueId());
        player.getInventory().setItem(event.getHand(), ItemUtils.takeItem(item));
        player.sendMessage(CC.translate(this.config.getString("lang.resistance.used")));
        // TODO Add restore effects
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.speedCooldown.cleanUp();
        this.jumpCooldown.cleanUp();
        this.leviCooldown.cleanUp();
        this.resistanceCooldown.cleanUp();
        this.backstabCooldown.cleanUp();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(CustomPlayerDamageEvent customPlayerDamageEvent) {
        if (!(customPlayerDamageEvent.getCause() instanceof EntityDamageByEntityEvent event)) return;
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getDamager() instanceof Projectile) return;

        Player victim = (Player) event.getEntity();
        Player damager = BukkitUtil.getDamager(event);

        if (damager == null) return;
        if (!this.classHandler.isClassApplied(damager, RogueClass.class)) return;
        if (damager.getInventory().getItemInMainHand().getType() != Material.GOLDEN_SWORD) return;
        if (backstabCooldown.isActive(damager.getUniqueId())) return;

        Vector playerVector = damager.getLocation().getDirection();
        Vector entityVector = victim.getLocation().getDirection();

        playerVector.setY(0F);
        entityVector.setY(0F);

        double degrees = playerVector.angle(entityVector);

        if (Math.abs(degrees) > 1.4) {
            damager.sendMessage(CC.translate("&cBackstab failed."));
            return;
        }

        damager.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

        double backstabDamage = 4.3D;
        if (victim.getHealth() - backstabDamage <= 0) {
            event.setDamage(0);
        } else {
            victim.setHealth(victim.getHealth() - backstabDamage);
        }

        damager.playSound(damager.getLocation(), Sound.ITEM_SHIELD_BREAK, 1F, 1F);
        damager.getWorld().playEffect(victim.getEyeLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);

        damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 2));
        customPlayerDamageEvent.setTrackerDamage(new BackstabDamage(victim.getName(), event.getDamage(), damager.getName()));
        this.backstabCooldown.apply(damager.getUniqueId());
    }

    public static class BackstabDamage extends PlayerDamage {

        public BackstabDamage(String damaged, double damage, String damager) {
            super(damaged, damage, damager);
        }

        public String getDeathMessage() {
            return (wrapName(getDamaged()) + " " + provider.getDeathMessageFormat("was backstabbed by") + " " + wrapName(getDamager()));
        }

    }

}
