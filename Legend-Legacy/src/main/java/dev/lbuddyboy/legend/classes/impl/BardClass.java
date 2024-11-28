package dev.lbuddyboy.legend.classes.impl;

import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.commons.util.Tasks;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArcherClass extends PvPClass {

    private final String ARCHER_META = "SHOT_BY_ARCHER";
    private final ClassHandler classHandler = LegendBukkit.getInstance().getClassHandler();
    private final Map<UUID, BukkitTask> tasks = new HashMap<>();
    private final Cooldown speedCooldown = new Cooldown(60);
    private final Cooldown jumpCooldown = new Cooldown(60);

    @Override
    public String getName() {
        return "Archer";
    }

    @Override
    public String getDisplayName() {
        return "";
    }

    @Override
    public boolean hasSetOn(Player player) {
        return player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType() == Material.LEATHER_HELMET &&
                player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType() == Material.LEATHER_CHESTPLATE &&
                player.getInventory().getLeggings() != null && player.getInventory().getLeggings().getType() == Material.LEATHER_LEGGINGS &&
                player.getInventory().getBoots() != null && player.getInventory().getBoots().getType() == Material.LEATHER_BOOTS;
    }

    @Override
    public boolean isTickable() {
        return false;
    }

    @Override
    public int getLimit() {
        return 1;
    }

    @Override
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
        super.apply(player);
    }

    @Override
    public void remove(Player player) {
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        player.removePotionEffect(PotionEffectType.REGENERATION);
        super.remove(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player victim = (Player) event.getEntity();
        Player shooter = BukkitUtil.getDamager(event);

        if (shooter == null) return;
        if (!event.getDamager().hasMetadata(ARCHER_META)) return;
        if (!this.classHandler.isClassApplied(shooter, ArcherClass.class)) return;

        // TODO force checks

        ArcherTagTimer tagTimer = LegendBukkit.getInstance().getTimerHandler().getTimer(ArcherTagTimer.class);

        tagTimer.apply(victim);

        if (victim.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            PotionEffect effect = victim.getActivePotionEffects().stream().filter(e -> e.getType() == PotionEffectType.INVISIBILITY).findFirst().orElse(null);
            if (effect != null) {
                if (this.tasks.containsKey(victim.getUniqueId())) {
                    this.tasks.get(victim.getUniqueId()).cancel();
                    this.tasks.remove(victim.getUniqueId());
                }

                BukkitTask task = new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (!victim.isOnline()) return;

                        tasks.remove(victim.getUniqueId());
                        victim.addPotionEffect(effect);
                    }

                }.runTaskLater(LegendBukkit.getInstance(), (20 * 5) + 10);

                victim.removePotionEffect(PotionEffectType.INVISIBILITY);
                this.tasks.put(victim.getUniqueId(), task);
            }
        }

        victim.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("classes.archer.shot.victim").replaceAll("%shooter%", shooter.getName())));
        shooter.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("classes.archer.shot.shooter").replaceAll("%victim%", victim.getName())));
        CommonsPlugin.getInstance().getNametagHandler().reloadPlayer(victim);
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (!this.classHandler.isClassApplied(player, ArcherClass.class)) return;

        System.out.println(event.getForce());
        event.getProjectile().setMetadata(ARCHER_META, new FixedMetadataValue(LegendBukkit.getInstance(), event.getForce()));
    }

    @EventHandler
    public void onClickSpeed(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (item == null || item.getType() != Material.SUGAR) return;
        if (!this.classHandler.isClassApplied(player, ArcherClass.class)) return;

        if (this.speedCooldown.isActive(player.getUniqueId())) {
            player.sendMessage(CC.translate(this.speedCooldown.applyPlaceholders(LegendBukkit.getInstance().getLanguage().getString("classes.archer.speed.cooldown"), player)));
            return;
        }

        player.setItemInHand(ItemUtils.takeItem(item));
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("classes.archer.speed.used")));
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
        if (!this.classHandler.isClassApplied(player, ArcherClass.class)) return;

        if (this.jumpCooldown.isActive(player.getUniqueId())) {
            player.sendMessage(CC.translate(this.jumpCooldown.applyPlaceholders(LegendBukkit.getInstance().getLanguage().getString("classes.archer.jump.cooldown"), player)));
            return;
        }

        player.setItemInHand(ItemUtils.takeItem(item));
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("classes.archer.jump.used")));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 5, 6));
        jumpCooldown.apply(player.getUniqueId());
        // TODO Add restore effects
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.speedCooldown.cleanUp();
        this.jumpCooldown.cleanUp();
    }

}
