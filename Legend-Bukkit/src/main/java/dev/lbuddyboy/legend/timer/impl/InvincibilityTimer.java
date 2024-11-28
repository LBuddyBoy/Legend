package dev.lbuddyboy.legend.timer.impl;

import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.api.PlayerClaimChangeEvent;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.timer.PlayerTimer;
import dev.lbuddyboy.legend.user.model.LegendUser;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InvincibilityTimer extends PlayerTimer {

    @Override
    public String getId() {
        return "invincibility";
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player victim = (Player) event.getEntity();

        Player damager = null;
        if (event.getDamager() instanceof Player) damager = (Player) event.getDamager();
        if (event.getDamager() instanceof Projectile && ((Projectile)event.getDamager()).getShooter() instanceof Player) damager = (Player) ((Projectile) event.getDamager()).getShooter();
        if (damager == null) return;

        if (isActive(damager.getUniqueId())) {
            damager.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("invincibility.damager-on-timer")
                    .replaceAll("%target%", victim.getName())
            ));
            event.setCancelled(true);
            return;
        }

        if (isActive(victim.getUniqueId())) {
            damager.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("invincibility.victim-on-timer")
                    .replaceAll("%target%", victim.getName())
            ));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLoss(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (!isActive(player.getUniqueId())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItem();
        ItemStack itemStack = item.getItemStack();
        if (itemStack.getAmount() <= 0) return;

        NBTItem nbtItem = new NBTItem(itemStack);
        if (!nbtItem.hasTag("pvp-loot")) return;

        if (!isActive(player.getUniqueId())) {
            nbtItem.removeKey("pvp-loot");
            item.setItemStack(nbtItem.getItem());
            return;
        }

        event.setCancelled(true);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("invincibility.cannot-pickup")));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        List<ItemStack> items = event.getDrops().stream().filter(Objects::nonNull).filter(i -> i.getType() != Material.AIR && i.getAmount() > 0).map(i -> {
            NBTItem item = new NBTItem(i);
            item.setBoolean("pvp-loot", true);
            return item.getItem();
        }).collect(Collectors.toList());

        event.getDrops().clear();
        event.getDrops().addAll(items);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (user.isPlayedBefore()) {

            return;
        }

        apply(player.getUniqueId());
        LegendBukkit.getInstance().getLanguage().getStringList("invincibility.activated").forEach(s -> player.sendMessage(CC.translate(s)));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClaimChange(PlayerClaimChangeEvent event) {
        Player player = event.getPlayer();
        if (!isActive(player.getUniqueId())) return;

        Team fromTeam = event.getFromTeam(), toTeam = event.getToTeam();

        if (fromTeam != null && fromTeam.getTeamType() == TeamType.SPAWN) {
            resume(player.getUniqueId());
        }

        if (toTeam != null && toTeam.getTeamType() == TeamType.SPAWN) {
            pause(player.getUniqueId());
        }

    }

}
