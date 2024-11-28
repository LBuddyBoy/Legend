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
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RogueClass extends PvPClass {

    private final ClassHandler classHandler = LegendBukkit.getInstance().getClassHandler();
    private final Cooldown speedCooldown = new Cooldown(60);
    private final Cooldown jumpCooldown = new Cooldown(60);

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

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
        return true;
    }

    @Override
    public void remove(Player player) {
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.RESISTANCE);
        player.removePotionEffect(PotionEffectType.REGENERATION);
        super.remove(player);
    }

    @Override
    public List<String> getScoreboardLines(Player player) {
        List<String> lines = new ArrayList<>();

        if (speedCooldown.isActive(player.getUniqueId())) {
            lines.add(speedCooldown.applyPlaceholders(LegendBukkit.getInstance().getScoreboard().getString("rogue.speed"), player));
        }

        if (jumpCooldown.isActive(player.getUniqueId())) {
            lines.add(jumpCooldown.applyPlaceholders(LegendBukkit.getInstance().getScoreboard().getString("rogue.jump"), player));
        }

        return lines;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player victim = (Player) event.getEntity();
        Player damager = BukkitUtil.getDamager(event);

        if (damager == null) return;
        if (!this.classHandler.isClassApplied(damager, RogueClass.class)) return;
        if (damager.getItemInHand() == null || damager.getItemInHand().getType() != Material.GOLDEN_SWORD) return;

        Vector playerVector = damager.getLocation().getDirection();
        Vector entityVector = victim.getLocation().getDirection();

        playerVector.setY(0F);
        entityVector.setY(0F);

        double degrees = playerVector.angle(entityVector);

        if (Math.abs(degrees) > 1.4) {
            damager.sendMessage(CC.translate("&cBackstab failed."));
            return;
        }

        damager.setItemInHand(new ItemStack(Material.AIR));

        double backstabDamage = 4.3D;
        if (damager.getHealth() - backstabDamage <= 0) {
            event.setDamage(0);
        } else {
            victim.setHealth(damager.getHealth() - backstabDamage);
        }

        damager.playSound(damager.getLocation(), Sound.ITEM_SHIELD_BREAK, 1F, 1F);
        damager.getWorld().playEffect(victim.getEyeLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);

        damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 2));

    }

    @EventHandler
    public void onClickSpeed(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (item == null || item.getType() != Material.SUGAR) return;
        if (!this.classHandler.isClassApplied(player, RogueClass.class)) return;

        if (this.speedCooldown.isActive(player.getUniqueId())) {
            player.sendMessage(CC.translate(this.speedCooldown.applyPlaceholders(LegendBukkit.getInstance().getLanguage().getString("classes.rogue.speed.cooldown"), player)));
            return;
        }

        player.setItemInHand(ItemUtils.takeItem(item));
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("classes.rogue.speed.used")));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 5, 3));
        speedCooldown.apply(player.getUniqueId());
        // TODO Add restore effects
    }

    @EventHandler
    public void onClickJump(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (item == null || item.getType() != Material.FEATHER) return;
        if (!this.classHandler.isClassApplied(player, RogueClass.class)) return;

        if (this.jumpCooldown.isActive(player.getUniqueId())) {
            player.sendMessage(CC.translate(this.jumpCooldown.applyPlaceholders(LegendBukkit.getInstance().getLanguage().getString("classes.rogue.jump.cooldown"), player)));
            return;
        }

        player.setItemInHand(ItemUtils.takeItem(item));
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("classes.rogue.jump.used")));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 20 * 5, 6));
        jumpCooldown.apply(player.getUniqueId());
        // TODO Add restore effects
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.speedCooldown.cleanUp();
        this.jumpCooldown.cleanUp();
    }

}
