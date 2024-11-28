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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class InvincibilityTimer extends PlayerTimer {

    @Override
    public String getId() {
        return "invincibility";
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        Player damager = null;
        if (event.getDamager() instanceof Player player) damager = player;
        if (event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player player) damager = player;
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
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!isActive(player.getUniqueId())) return;

        Item item = event.getItem();
        ItemStack itemStack = item.getItemStack();
        if (itemStack.getAmount() <= 0) return;

        NBTItem nbtItem = new NBTItem(itemStack);
        if (!nbtItem.hasTag("pvp-loot")) return;

        event.setCancelled(true);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("invincibility.cannot-pickup")));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        List<ItemStack> items = event.getDrops().stream().filter(Objects::nonNull).filter(i -> i.getType() != Material.AIR && i.getAmount() > 0).map(i -> {
            NBTItem item = new NBTItem(i);
            item.setBoolean("pvp-loot", true);
            return item.getItem();
        }).toList();

        event.getDrops().clear();
        event.getDrops().addAll(items);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (user.isPlayedBefore()) return;

        apply(player.getUniqueId());
        LegendBukkit.getInstance().getLanguage().getStringList("invincibility.activated").forEach(s -> player.sendMessage(CC.translate(s)));
    }

    @EventHandler
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
