package dev.lbuddyboy.legend.listener;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.nametag.NametagModule;
import com.lunarclient.apollo.recipients.Recipients;
import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.crates.util.Tasks;
import dev.lbuddyboy.legend.LangConfig;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.api.PacketReceiveEvent;
import dev.lbuddyboy.legend.command.impl.HelpCommand;
import dev.lbuddyboy.legend.features.settings.Setting;
import dev.lbuddyboy.legend.user.model.LegendUser;
import dev.lbuddyboy.legend.util.BukkitUtil;
import dev.lbuddyboy.legend.util.NameTagUtil;
import net.citizensnpcs.api.CitizensAPI;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class GeneralListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity(), killer = victim.getKiller();

        if (CitizensAPI.getNPCRegistry().isNPC(victim)) return;
        if (killer == null) return;
        if (!SettingsConfig.SETTINGS_TAKE_MONEY_ON_DEATH.getBoolean()) return;

        LegendUser victimUser = LegendBukkit.getInstance().getUserHandler().getUser(victim.getUniqueId()), killerUser = LegendBukkit.getInstance().getUserHandler().getUser(killer.getUniqueId());

        killerUser.setBalance(killerUser.getBalance() + victimUser.getBalance());
        killer.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("took-money-on-death")
                .replaceAll("%amount%", APIConstants.formatNumber(victimUser.getBalance()))
                .replaceAll("%target%", victim.getName())
        ));
        victimUser.setBalance(0);
        killer.playSound(killer, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.SUFFOCATION) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onCrossBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getProjectile() instanceof Arrow) return;
        if (!SettingsConfig.SETTINGS_DISABLE_CHARGED_PROJECTILES.getBoolean()) return;

        event.setCancelled(true);
        player.sendMessage(CC.translate("&cCharged projectiles are disabled."));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        if (SettingsConfig.SETTINGS_DEATHBANS.getBoolean()) {
            return;
        }

        event.setRespawnLocation(LegendBukkit.getInstance().getSpawnLocation());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        HelpCommand.def(event.getPlayer(), 1);
    }

    @EventHandler
    public void onShield(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();
        if (stack == null || stack.getType() != Material.SHIELD) return;
        if (!SettingsConfig.SETTINGS_DISABLE_SHIELDS.getBoolean()) return;

        event.setCancelled(true);
        player.sendMessage(CC.translate("&cShields are disabled."));
    }

    @EventHandler
    public void onMace(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();
        if (stack == null || stack.getType() != Material.MACE) return;
        if (!SettingsConfig.SETTINGS_DISABLE_MACES.getBoolean()) return;

        event.setCancelled(true);
        player.sendMessage(CC.translate("&cMaces are disabled."));
    }

    // staffmode|h|staff|mm|modmode|mod
    @EventHandler
    public void onCommandProcess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();

        if (command.equalsIgnoreCase("/staffmode")
                || command.equalsIgnoreCase("/h")
                || command.equalsIgnoreCase("/mm")
                || command.equalsIgnoreCase("/modmode")
                || command.equalsIgnoreCase("/mod")
                || command.equalsIgnoreCase("/staff")) {
            if (!player.hasPermission("arrow.command.staffmode")) return;

            Tasks.run(() -> {
                NameTagUtil.updateTargetsForViewer(BukkitUtil.getOnlinePlayers(), player);
                NameTagUtil.updateTargetForViewers(player, BukkitUtil.getOnlinePlayers());
            });
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager)) return;
        if (damager.getInventory().getItemInMainHand().getType() != Material.MACE) return;
        if (!SettingsConfig.SETTINGS_DISABLE_MACES.getBoolean()) return;

        event.setCancelled(true);
        damager.sendMessage(CC.translate("&cMaces are disabled."));
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof EnderCrystal crystal)) return;
        if (!SettingsConfig.SETTINGS_DISABLE_END_CRYSTALS.getBoolean()) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTridentDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Trident trident)) return;

        event.setDamage(event.getDamage() * SettingsConfig.MODIFIERS_TRIDENT_DAMAGE.getDouble());
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();

        if (block.getY() > SettingsConfig.SETTINGS_DOORS.getInt()) return;
        if (!block.getType().name().endsWith("_DOOR")) return;

        event.setCancelled(true);
        LangConfig.DOORS_DISABLED.sendMessage(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Apollo.getModuleManager().getModule(NametagModule.class).resetNametag(Recipients.ofEveryone(), player.getUniqueId());
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getItem().getItemStack().getType() != Material.COBBLESTONE
                && event.getItem().getItemStack().getType() != Material.DEEPSLATE
                && event.getItem().getItemStack().getType() != Material.COBBLED_DEEPSLATE
                && event.getItem().getItemStack().getType() != Material.GRANITE
                && event.getItem().getItemStack().getType() != Material.DIORITE
        ) return;
        if (Setting.COBBLESTONE.isToggled(player.getUniqueId())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockDropItemEvent event) {
        Player player = event.getPlayer();
        if (!Setting.INSTANT_PICKUP.isToggled(player.getUniqueId())) return;

        event.getItems().removeIf(item -> {
            if (ItemUtils.tryFit(player, item.getItemStack()) == null) {
                player.getInventory().addItem(item.getItemStack());
                return true;
            }
            return false;
        });
    }

    @EventHandler
    public void onPacket(PacketReceiveEvent event) {
        if (!(event.getPacket() instanceof ClientboundSetPlayerTeamPacket packet)) return;

        if (!packet.getName().equalsIgnoreCase("invis")) {
            int i = 0;
            i |= 2;

            event.setValue("d", "always");
            event.setValue("g", i);
        }
    }

}
