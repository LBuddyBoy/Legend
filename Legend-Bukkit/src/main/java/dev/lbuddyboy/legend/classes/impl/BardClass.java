package dev.lbuddyboy.legend.classes.impl;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.classes.ClassHandler;
import dev.lbuddyboy.legend.classes.PvPClass;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.timer.impl.CombatTimer;
import dev.lbuddyboy.legend.util.Cooldown;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

public class BardClass extends PvPClass {

    private final ClassHandler classHandler = LegendBukkit.getInstance().getClassHandler();
    private final Map<UUID, Double> energy = new HashMap<>();
    private final Map<Material, PotionEffect> heldEffects = new HashMap<>();
    private final Map<Material, ClickableBardEffect> clickableEffects = new HashMap<>();
    private final Cooldown effectCooldown;

    public BardClass() {
        super();

        this.heldEffects.clear();
        this.clickableEffects.clear();
        this.effectCooldown = new Cooldown(getConfig().getInt("settings.effect-cooldown"));

        ConfigurationSection heldSection = this.config.getConfigurationSection("held_effects");

        if (heldSection != null) {
            heldSection.getKeys(false).forEach(key -> {
                PotionEffectType type = Registry.EFFECT.match(key);
                if (type == null) {
                    LegendBukkit.getInstance().getLogger().warning("Couldn't parse: " + key + " as a potion effect in the classes/bard.yml");
                    return;
                }

                Material material = Registry.MATERIAL.match(heldSection.getString(key + ".material"));

                if (material == null) {
                    LegendBukkit.getInstance().getLogger().warning("Couldn't parse: " + key + ".material as a material in the classes/bard.yml");
                    return;
                }

                this.heldEffects.put(material, new PotionEffect(type, 20 * 5, heldSection.getInt(key + ".power") - 1));
            });
        }

        ConfigurationSection clickableSection = this.config.getConfigurationSection("clickable_effects");

        if (clickableSection != null) {
            clickableSection.getKeys(false).forEach(key -> {
                PotionEffectType type = Registry.EFFECT.match(key);
                Material material = Registry.MATERIAL.match(clickableSection.getString(key + ".material"));
                int duration = clickableSection.getInt(key + ".duration");
                int power = clickableSection.getInt(key + ".power");

                if (type == null) {
                    LegendBukkit.getInstance().getLogger().warning("Couldn't parse: " + key + " as a potion effect in the classes/bard.yml");
                    return;
                }

                if (material == null) {
                    LegendBukkit.getInstance().getLogger().warning("Couldn't parse: " + key + ".material as a material in the classes/bard.yml");
                    return;
                }

                this.clickableEffects.put(material, new ClickableBardEffect(
                        clickableSection.getString(key + ".name"),
                        clickableSection.getDouble(key + ".cost"),
                        new PotionEffect(type, 20 * duration, power - 1),
                        clickableSection.getBoolean(key + ".friendly"),
                        clickableSection.getInt(key + ".cooldown")
                ));
            });
        }

    }

    @Override
    public void loadDefaults() {
        this.config.addDefault("effects.regeneration", 1);
        this.config.addDefault("effects.resistance", 1);
        this.config.addDefault("effects.speed", 2);
        this.config.addDefault("settings.damage-modifier.normal", 1.0D);
        this.config.addDefault("settings.damage-modifier.strength-one", 1.0D);
        this.config.addDefault("settings.damage-modifier.strength-two", 1.0D);
        this.config.addDefault("settings.use-energy", true);
        this.config.addDefault("settings.effect-cooldown", 5);
        this.config.addDefault("settings.limit", 1);
        this.config.addDefault("settings.warmup", 3);
        this.config.addDefault("settings.enabled", true);

        this.config.addDefault("held_effects.jump_boost.material", "RABBIT_FOOT");
        this.config.addDefault("held_effects.jump_boost.power", 2);
        this.config.addDefault("held_effects.jump_boost.name", "Jump Boost II");

        this.config.addDefault("held_effects.speed.material", "SUGAR");
        this.config.addDefault("held_effects.speed.power", 2);
        this.config.addDefault("held_effects.speed.name", "Speed II");

        this.config.addDefault("held_effects.strength.material", "BLAZE_POWDER");
        this.config.addDefault("held_effects.strength.power", 1);
        this.config.addDefault("held_effects.strength.name", "Strength I");

        this.config.addDefault("held_effects.resistance.material", "IRON_INGOT");
        this.config.addDefault("held_effects.resistance.power", 1);
        this.config.addDefault("held_effects.resistance.name", "Resistance I");

        this.config.addDefault("held_effects.regeneration.material", "GHAST_TEAR");
        this.config.addDefault("held_effects.regeneration.power", 1);
        this.config.addDefault("held_effects.regeneration.name", "Regeneration I");

        this.config.addDefault("held_effects.invisibility.material", "INK_SAC");
        this.config.addDefault("held_effects.invisibility.power", 2);
        this.config.addDefault("held_effects.invisibility.name", "Invisibility I");

        this.config.addDefault("clickable_effects.wither.material", "SPIDER_EYE");
        this.config.addDefault("clickable_effects.wither.cost", 35.0D);
        this.config.addDefault("clickable_effects.wither.duration", 5);
        this.config.addDefault("clickable_effects.wither.cooldown", 5);
        this.config.addDefault("clickable_effects.wither.power", 2);
        this.config.addDefault("clickable_effects.wither.name", "Wither II");
        this.config.addDefault("clickable_effects.wither.friendly", false);

        this.config.addDefault("clickable_effects.jump_boost.material", "RABBIT_FOOT");
        this.config.addDefault("clickable_effects.jump_boost.cost", 35.0D);
        this.config.addDefault("clickable_effects.jump_boost.duration", 5);
        this.config.addDefault("clickable_effects.jump_boost.cooldown", 5);
        this.config.addDefault("clickable_effects.jump_boost.power", 7);
        this.config.addDefault("clickable_effects.jump_boost.name", "Jump Boost VII");
        this.config.addDefault("clickable_effects.jump_boost.friendly", true);

        this.config.addDefault("clickable_effects.levitation.material", "PHANTOM_MEMBRANE");
        this.config.addDefault("clickable_effects.levitation.cost", 30.0D);
        this.config.addDefault("clickable_effects.levitation.duration", 5);
        this.config.addDefault("clickable_effects.levitation.power", 5);
        this.config.addDefault("clickable_effects.levitation.cooldown", 5);
        this.config.addDefault("clickable_effects.levitation.name", "Levitation V");
        this.config.addDefault("clickable_effects.levitation.friendly", true);

        this.config.addDefault("clickable_effects.speed.material", "SUGAR");
        this.config.addDefault("clickable_effects.speed.cost", 25.0D);
        this.config.addDefault("clickable_effects.speed.duration", 5);
        this.config.addDefault("clickable_effects.speed.power", 3);
        this.config.addDefault("clickable_effects.speed.cooldown", 5);
        this.config.addDefault("clickable_effects.speed.name", "Speed III");
        this.config.addDefault("clickable_effects.speed.friendly", true);

        this.config.addDefault("clickable_effects.strength.material", "BLAZE_POWDER");
        this.config.addDefault("clickable_effects.strength.cost", 45.0D);
        this.config.addDefault("clickable_effects.strength.duration", 5);
        this.config.addDefault("clickable_effects.strength.power", 2);
        this.config.addDefault("clickable_effects.strength.cooldown", 5);
        this.config.addDefault("clickable_effects.strength.name", "Strength II");
        this.config.addDefault("clickable_effects.strength.friendly", true);

        this.config.addDefault("clickable_effects.resistance.material", "IRON_INGOT");
        this.config.addDefault("clickable_effects.resistance.cost", 30.0D);
        this.config.addDefault("clickable_effects.resistance.duration", 5);
        this.config.addDefault("clickable_effects.resistance.power", 3);
        this.config.addDefault("clickable_effects.resistance.cooldown", 5);
        this.config.addDefault("clickable_effects.resistance.name", "Resistance III");
        this.config.addDefault("clickable_effects.resistance.friendly", true);

        this.config.addDefault("clickable_effects.regeneration.material", "GHAST_TEAR");
        this.config.addDefault("clickable_effects.regeneration.cost", 40.0D);
        this.config.addDefault("clickable_effects.regeneration.duration", 5);
        this.config.addDefault("clickable_effects.regeneration.power", 3);
        this.config.addDefault("clickable_effects.regeneration.cooldown", 5);
        this.config.addDefault("clickable_effects.regeneration.name", "Regeneration III");
        this.config.addDefault("clickable_effects.regeneration.friendly", true);

        this.config.addDefault("clickable_effects.invisibility.material", "INK_SAC");
        this.config.addDefault("clickable_effects.invisibility.cost", 40.0D);
        this.config.addDefault("clickable_effects.invisibility.duration", 5);
        this.config.addDefault("clickable_effects.invisibility.cooldown", 5);
        this.config.addDefault("clickable_effects.invisibility.power", 2);
        this.config.addDefault("clickable_effects.invisibility.name", "Invisibility II");
        this.config.addDefault("clickable_effects.invisibility.friendly", true);

        this.config.addDefault("scoreboard.energy", "&fEnergy: &b%energy%");
        this.config.addDefault("scoreboard.cooldown", "&fEffect: &c%cooldown-mmss%");

        this.config.addDefault("lang.limited", "<blend:&6;&e>&lBARD</> &7» &cClass couldn't be applied because your team has too many.");
        this.config.addDefault("lang.spawn", "<blend:&6;&e>&lBARD</> &7» &cYou cannot use effect in spawn.");
        this.config.addDefault("lang.applied", "<blend:&6;&e>&lBARD</> &7» &aThe bard class is now enabled! Hold effects to support your team!");
        this.config.addDefault("lang.removed", "<blend:&6;&e>&lBARD</> &7» &cThe bard class is now disabled!");
        this.config.addDefault("lang.warming", "<blend:&6;&e>&lBARD</> &7» &eThe bard class is warming up!");
        this.config.addDefault("lang.insufficient", "<blend:&6;&e>&lBARD</> &7» &cYou need %required-energy% bard energy to use %effect%!");
        this.config.addDefault("lang.used", "<blend:&6;&e>&lBARD</> &7» &aSuccessfully gave %effect% to %players% players! You used %energy% bard energy.");
        this.config.addDefault("lang.cooldown", "<blend:&6;&e>&lBARD</> &7» &cYou cannot use a bard effect for %cooldown-decimal% seconds!");
    }

    public boolean isUseEnergy() {
        return this.config.getBoolean("settings.use-energy");
    }

    @Override
    public String getName() {
        return "Bard";
    }

    @Override
    public String getDisplayName() {
        return "";
    }

    @Override
    public boolean hasSetOn(Player player) {
        return player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType() == Material.GOLDEN_HELMET &&
                player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType() == Material.GOLDEN_CHESTPLATE &&
                player.getInventory().getLeggings() != null && player.getInventory().getLeggings().getType() == Material.GOLDEN_LEGGINGS &&
                player.getInventory().getBoots() != null && player.getInventory().getBoots().getType() == Material.GOLDEN_BOOTS;
    }

    @Override
    public List<String> getScoreboardLines(Player player) {
        List<String> lines = new ArrayList<>();

        if (isUseEnergy()) {
            lines.add(this.config.getString("scoreboard.energy")
                    .replaceAll("%energy%", String.valueOf(Math.round(getEnergy(player))))
            );
        }

        if (this.effectCooldown.isActive(player.getUniqueId())) {
            lines.add(this.effectCooldown.applyPlaceholders(this.config.getString("scoreboard.cooldown"), player));
        }

        return lines;
    }

    @Override
    public boolean isTickable() {
        return true;
    }

    @Override
    public void tick(Player player) {

        if (isUseEnergy()) {
            if (getEnergy(player) < 120) {
                this.energy.put(player.getUniqueId(), getEnergy(player) + 0.25);
            }
        }

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        if (this.heldEffects.containsKey(mainHand.getType())) {
            if (TeamType.SPAWN.appliesAt(player.getLocation())) {
                player.sendMessage(CC.translate(this.config.getString("lang.spawn")));
                return;
            }

            PotionEffect effect = this.heldEffects.get(mainHand.getType());

            for (Player other : getNearby(player, true)) {
                other.addPotionEffect(effect);
            }

            if (effect.getType() != PotionEffectType.STRENGTH) {
                player.addPotionEffect(effect);
            }
        }

        if (this.heldEffects.containsKey(offHand.getType())) {
            if (TeamType.SPAWN.appliesAt(player.getLocation())) {
                player.sendMessage(CC.translate(this.config.getString("lang.spawn")));
                return;
            }

            PotionEffect effect = this.heldEffects.get(offHand.getType());

            for (Player other : getNearby(player, true)) {
                other.addPotionEffect(effect);
            }

            if (effect.getType() != PotionEffectType.STRENGTH) {
                player.addPotionEffect(effect);
            }
        }

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

    public double getEnergy(Player player) {
        return this.energy.getOrDefault(player.getUniqueId(), 0D);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (item == null) return;
        if (!this.clickableEffects.containsKey(item.getType())) return;
        if (!this.classHandler.isClassApplied(player, BardClass.class)) return;

        if (TeamType.SPAWN.appliesAt(player.getLocation())) {
            player.sendMessage(CC.translate(this.config.getString("lang.spawn")));
            return;
        }

        ClickableBardEffect effect = this.clickableEffects.get(item.getType());

        if (this.effectCooldown.isActive(player.getUniqueId())) {
            player.sendMessage(CC.translate(this.effectCooldown.applyPlaceholders(this.config.getString("lang.cooldown")
                    .replaceAll("%energy%", String.valueOf(effect.getEnergyCost()))
                    .replaceAll("%effect%", effect.getName()), player)));
            return;
        }

        if (!isUseEnergy()) {
            if (effect.getCooldown().isActive(player.getUniqueId())) {
                player.sendMessage(CC.translate(effect.getCooldown().applyPlaceholders(this.config.getString("lang.cooldown")
                        .replaceAll("%energy%", String.valueOf(effect.getEnergyCost()))
                        .replaceAll("%effect%", effect.getName()), player)));
                return;
            }
        }

        if (isUseEnergy()) {
            if (getEnergy(player) < effect.getEnergyCost()) {
                player.sendMessage(CC.translate(this.config.getString("lang.insufficient")
                        .replaceAll("%required-energy%", String.valueOf(effect.getEnergyCost()))
                        .replaceAll("%effect%", effect.getName())
                ));
                return;
            }
        }

        List<Player> players = getNearby(player, effect.getEffect().getType() != PotionEffectType.WITHER);

        for (Player other : players) {
            other.addPotionEffect(effect.getEffect());
        }

        player.addPotionEffect(effect.getEffect());
        this.effectCooldown.apply(player.getUniqueId());
        effect.getCooldown().apply(player.getUniqueId());
        this.energy.put(player.getUniqueId(), getEnergy(player) - effect.getEnergyCost());
        player.getInventory().setItem(event.getHand(), ItemUtils.takeItem(item));
        player.sendMessage(CC.translate(this.config.getString("lang.used")
                .replaceAll("%energy%", String.valueOf(effect.getEnergyCost()))
                .replaceAll("%effect%", effect.getName())
                .replaceAll("%players%", String.valueOf(players.size()))
        ));
        LegendBukkit.getInstance().getTimerHandler().getTimer(CombatTimer.class).apply(player);
    }

    @Getter
    public static class ClickableBardEffect {

        private String name;
        private double energyCost;
        private PotionEffect effect;
        private boolean self;
        private int cooldownSeconds;
        private Cooldown cooldown;

        public ClickableBardEffect(String name, double energyCost, PotionEffect effect, boolean self, int cooldownSeconds) {
            this.name = name;
            this.energyCost = energyCost;
            this.effect = effect;
            this.self = self;
            this.cooldownSeconds = cooldownSeconds;
            this.cooldown = new Cooldown(cooldownSeconds);
        }

    }

    public List<Player> getNearby(Player player, boolean friendly) {
        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(player).orElse(null);

        return player.getNearbyEntities(40, 40, 40).stream().filter(e -> e instanceof Player).map(e -> ((Player) e)).filter(other -> {

            if (friendly) {
                if (team != null && team.isMember(other.getUniqueId())) return true;
                if (team != null && team.isAlly(other)) return true;

                return false;
            }

            if (team != null && team.isMember(other.getUniqueId()) || team != null && team.isAlly(other)) return false;

            return true;
        }).collect(Collectors.toList());
    }

}
