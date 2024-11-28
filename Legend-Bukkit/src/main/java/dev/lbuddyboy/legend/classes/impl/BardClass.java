package dev.lbuddyboy.legend.classes.impl;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.classes.ClassHandler;
import dev.lbuddyboy.legend.classes.PvPClass;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.util.Cooldown;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
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
    private final Cooldown effectCooldown = new Cooldown(5);

    public BardClass() {
        this.heldEffects.put(Material.SUGAR, new PotionEffect(PotionEffectType.SPEED, 20 * 5, 1));
        this.heldEffects.put(Material.BLAZE_POWDER, new PotionEffect(PotionEffectType.STRENGTH, 20 * 5, 0));
        this.heldEffects.put(Material.GHAST_TEAR, new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 0));
        this.heldEffects.put(Material.IRON_INGOT, new PotionEffect(PotionEffectType.RESISTANCE, 20 * 5, 0));
        this.heldEffects.put(Material.MAGMA_CREAM, new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 5, 0));
        this.heldEffects.put(Material.FEATHER, new PotionEffect(PotionEffectType.JUMP_BOOST, 20 * 5, 1));
        this.heldEffects.put(Material.INK_SAC, new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 5, 0));

        this.clickableEffects.put(Material.SPIDER_EYE, new ClickableBardEffect(
                "Wither II",
                35.0D,
                new PotionEffect(PotionEffectType.WITHER, 20 * 5, 1)
        ));
        this.clickableEffects.put(Material.FEATHER, new ClickableBardEffect(
                "Jump VII",
                35.0D,
                new PotionEffect(PotionEffectType.JUMP_BOOST, 20 * 5, 6)
        ));
        this.clickableEffects.put(Material.SUGAR, new ClickableBardEffect(
                "Speed III",
                35.0D,
                new PotionEffect(PotionEffectType.SPEED, 20 * 5, 2)
        ));
        this.clickableEffects.put(Material.BLAZE_POWDER, new ClickableBardEffect(
                "Strength II",
                45.0D,
                new PotionEffect(PotionEffectType.STRENGTH, 20 * 5, 1)
        ));
        this.clickableEffects.put(Material.IRON_INGOT, new ClickableBardEffect(
                "Resistance III",
                30.0D,
                new PotionEffect(PotionEffectType.RESISTANCE, 20 * 5, 2)
        ));
        this.clickableEffects.put(Material.GHAST_TEAR, new ClickableBardEffect(
                "Regeneration III",
                40.0D,
                new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 2)
        ));
        this.clickableEffects.put(Material.INK_SAC, new ClickableBardEffect(
                "Invisibility II",
                40.0D,
                new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 5, 2)
        ));
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

        lines.add(LegendBukkit.getInstance().getScoreboard().getString("bard.energy")
                .replaceAll("%energy%", String.valueOf(Math.round(getEnergy(player))))
        );

        if (this.effectCooldown.isActive(player.getUniqueId())) {
            lines.add(this.effectCooldown.applyPlaceholders(LegendBukkit.getInstance().getScoreboard().getString("bard.cooldown"), player));
        }

        return lines;
    }

    @Override
    public boolean isTickable() {
        return true;
    }

    @Override
    public void tick(Player player) {

        if (getEnergy(player) < 120) {
            this.energy.put(player.getUniqueId(), getEnergy(player) + 0.25);
        }

        ItemStack item = player.getInventory().getItemInHand();

        if (item != null && this.heldEffects.containsKey(item.getType())) {
            for (Player other : getNearby(player, true)) {
                other.addPotionEffect(this.heldEffects.get(item.getType()));
            }
            player.addPotionEffect(this.heldEffects.get(item.getType()));
        }

    }

    @Override
    public boolean apply(Player player) {
        if (!super.apply(player)) return false;

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
        return true;
    }

    @Override
    public void remove(Player player) {
        this.effectCooldown.remove(player.getUniqueId());
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.RESISTANCE);
        player.removePotionEffect(PotionEffectType.REGENERATION);
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

        ClickableBardEffect effect = this.clickableEffects.get(item.getType());

        if (this.effectCooldown.isActive(player.getUniqueId())) {
            player.sendMessage(CC.translate(this.effectCooldown.applyPlaceholders(LegendBukkit.getInstance().getLanguage().getString("classes.bard.effect.cooldown")
                            .replaceAll("%energy%", String.valueOf(effect.getEnergyCost()))
                            .replaceAll("%effect%", effect.getName()), player)));
            return;
        }

        if (getEnergy(player) < effect.getEnergyCost()) {
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("classes.bard.effect.insufficient")
                    .replaceAll("%required-energy%", String.valueOf(effect.getEnergyCost()))
                    .replaceAll("%effect%", effect.getName())
            ));
            return;
        }

        List<Player> players = getNearby(player, effect.getEffect().getType() != PotionEffectType.WITHER);

        for (Player other : players) {
            other.addPotionEffect(effect.getEffect());
        }

        player.addPotionEffect(effect.getEffect());
        this.effectCooldown.apply(player.getUniqueId());
        this.energy.put(player.getUniqueId(), getEnergy(player) - effect.getEnergyCost());
        player.setItemInHand(ItemUtils.takeItem(item));
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("classes.bard.effect.used")
                .replaceAll("%energy%", String.valueOf(effect.getEnergyCost()))
                .replaceAll("%effect%", effect.getName())
                .replaceAll("%players%", String.valueOf(players.size()))
        ));
    }

    @AllArgsConstructor
    @Getter
    public static class ClickableBardEffect {

        private String name;
        private double energyCost;
        private PotionEffect effect;

    }

    public List<Player> getNearby(Player player, boolean friendly) {
        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(player).orElse(null);

        return player.getNearbyEntities(40, 40, 40).stream().filter(e -> e instanceof Player).map(e -> ((Player)e)).filter(other -> {

            if (friendly) {
                if (team == null || !team.isMember(other.getUniqueId()) || !team.isAlly(other)) return false;

                return true;
            }

            if (team != null && team.isMember(other.getUniqueId()) || team != null && team.isAlly(other)) return false;

            return true;
        }).collect(Collectors.toList());
    }

}
