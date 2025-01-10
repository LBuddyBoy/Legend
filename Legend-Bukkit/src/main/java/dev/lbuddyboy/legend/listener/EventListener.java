package dev.lbuddyboy.legend.listener;

import de.tr7zw.nbtapi.NBT;
import dev.lbuddyboy.arrow.ArrowPlugin;
import dev.lbuddyboy.arrow.staffmode.StaffModeConstants;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.loottable.impl.LootTableItem;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.crates.util.Tasks;
import dev.lbuddyboy.events.Capturable;
import dev.lbuddyboy.events.EventType;
import dev.lbuddyboy.events.IEvent;
import dev.lbuddyboy.events.alcatraz.Alcatraz;
import dev.lbuddyboy.events.api.event.AlcatrazJoinEvent;
import dev.lbuddyboy.events.api.event.EventCapturedEvent;
import dev.lbuddyboy.events.api.event.EventTickEvent;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.team.model.log.impl.TeamPointsChangeLog;
import dev.lbuddyboy.legend.timer.impl.CombatTimer;
import dev.lbuddyboy.legend.timer.impl.EnderPearlTimer;
import dev.lbuddyboy.legend.timer.impl.InvincibilityTimer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Date;

public class EventListener implements Listener {

    private static final String CAPTURED_BY_NBT = "EVENT_CAPTURED_BY";
    private static final String CAPTURED_AT_NBT = "EVENT_CAPTURED_AT";
    private static final String CAPTURED_TYPE_NBT = "EVENT_CAPTURED_TYPE";
    private static final String CAPTURED_NAME_NBT = "EVENT_CAPTURED_NAME";

    @EventHandler
    public void onCapture(EventCapturedEvent event) {
        EventType type = event.getEvent().getEventType();
        Player winner = event.getWinner();
        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(winner.getUniqueId()).orElse(null);

        if (type != EventType.CITADEL) {
            for (LootTableItem item : event.getEvent().getLootTable().roll(0, 1)) {
                if (!item.isGiveItem()) {
                    item.getCommands().forEach((command) -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("%player%", winner.getName())));
                } else {
                    ItemStack stack = ItemUtils.tryFit(winner, item.getItem());

                    if (stack == null) {
                        winner.getInventory().addItem(item.getItem());
                        continue;
                    }

                    winner.getWorld().dropItemNaturally(winner.getLocation(), item.getItem());
                }
            }
        }

        ItemUtils.tryFit(winner, this.createEventSign(type, event.getEvent(), winner), false);

        if (team == null) return;

        int points = type == EventType.KOTH ? SettingsConfig.POINTS_KOTH.getInt() :
                type == EventType.DTC ? SettingsConfig.POINTS_DTC.getInt() :
                type == EventType.CITADEL ? SettingsConfig.POINTS_CITADEL.getInt() : 0;
        TeamPointsChangeLog.ChangeCause cause = null;

        try {
            cause = TeamPointsChangeLog.ChangeCause.valueOf(type.name());
        } catch (Exception ignored) {
            return;
        }

        team.setPoints(
                team.getPoints() + points,
                winner.getUniqueId(),
                cause
        );
    }

    @EventHandler
    public void onSignEdit(SignChangeEvent event) {
        if (!(event.getBlock().getState() instanceof Sign sign)) return;
        if (!NBT.getPersistentData(sign, (tag) -> {
            return tag.hasTag(CAPTURED_BY_NBT);
        })) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (event.getItemInHand().getType() != Material.OAK_SIGN) return;
        if (!event.getItemInHand().hasItemMeta()) return;
        if (!event.getItemInHand().getItemMeta().hasDisplayName()) return;
        if (!NBT.get(event.getItemInHand(), (tag) -> {
            return tag.hasTag(CAPTURED_BY_NBT);
        })) return;


        if (!(block.getState() instanceof Sign sign)) return;

        int index = 0;
        for (String line : ItemUtils.getLore(event.getItemInHand())) {
            if (index >= 4) return;

            sign.setLine(index, CC.translate(line));
            index++;
        }

        sign.update(true);

        NBT.modifyPersistentData(sign, (tag) -> {
            tag.setBoolean(CAPTURED_BY_NBT, true);
        });

    }

    @EventHandler
    public void onTick(EventTickEvent event) {
        Capturable capturable = event.getCapturable();
        Player player = event.getPlayer();
        InvincibilityTimer invincibilityTimer = LegendBukkit.getInstance().getTimerHandler().getTimer(InvincibilityTimer.class);

        if (!invincibilityTimer.isActive(player.getUniqueId()) && !player.hasMetadata(StaffModeConstants.VANISH_META_DATA) && player.getGameMode() == GameMode.SURVIVAL) return;

        event.setCancelled(true);
        player.sendMessage(CC.translate("&cYou are in an invalid state to capture this event."));
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof EnderPearl pearl)) return;
        if (!(pearl.getShooter() instanceof Player shooter)) return;

        if (!TeamType.CITADEL.appliesAt(shooter.getLocation())) return;

        event.setCancelled(true);
        shooter.sendMessage(CC.translate("&cYou cannot enderpearl in citadel."));
    }

    @EventHandler
    public void onLaunch(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof EnderPearl pearl)) return;
        if (!(pearl.getShooter() instanceof Player shooter)) return;

        if (!TeamType.CITADEL.appliesAt(pearl.getLocation())) return;

        event.setCancelled(true);
        shooter.sendMessage(CC.translate("&cYou cannot enderpearl in citadel."));
        LegendBukkit.getInstance().getTimerHandler().getTimer(EnderPearlTimer.class).remove(shooter.getUniqueId());
    }

    public ItemStack createEventSign(EventType type, IEvent event, Player winner) {
        ItemStack item = new ItemStack(Material.OAK_SIGN, 1);

        NBT.modify(item, (tag) -> {
            tag.setUUID(CAPTURED_BY_NBT, winner.getUniqueId());
            tag.setString(CAPTURED_NAME_NBT, event.getName());
            tag.setString(CAPTURED_TYPE_NBT, type.name());
            tag.setLong(CAPTURED_AT_NBT, System.currentTimeMillis());
        });

        return new ItemFactory(item)
                .displayName("<blend:&6;&e>&lEvent Captured Sign</>")
                .lore(Arrays.asList(
                        "&6" + winner.getName(),
                        "&fcaptured",
                        "&6" + event.getName(),
                        "&d" + APIConstants.SDF.format(new Date())
                ))
                .build();
    }

}
