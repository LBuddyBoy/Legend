package dev.lbuddyboy.legend.team.listener;

import dev.lbuddyboy.commons.component.FancyBuilder;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.events.Events;
import dev.lbuddyboy.events.citadel.Citadel;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.LegendConstants;
import dev.lbuddyboy.legend.api.PlayerClaimChangeEvent;
import dev.lbuddyboy.legend.features.settings.Setting;
import dev.lbuddyboy.legend.team.ClaimHandler;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.user.model.LegendUser;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeamClaimListener implements Listener {

    private final ClaimHandler claimHandler = LegendBukkit.getInstance().getTeamHandler().getClaimHandler();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo(), from = event.getFrom();

        if (to == null) return;
        if (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ()) return;

        if (player.hasMetadata("CLAIM_MAP_VIEW_LOCATION")) {
            Location startLocation = (Location) player.getMetadata("CLAIM_MAP_VIEW_LOCATION").get(0).value();

            if (startLocation != null) {
                double distanceXZ = Math.sqrt(Math.pow(startLocation.getX() - to.getX(), 2) + Math.pow(startLocation.getZ() - to.getZ(), 2));

                if (distanceXZ > 50) {
                    this.claimHandler.updateMapView(player);
                }
            }
        }

        Team fromTeam = this.claimHandler.getTeam(from).orElse(null), toTeam = this.claimHandler.getTeam(to).orElse(null);

        if (fromTeam == null && toTeam == null) return;
        if (fromTeam != null && toTeam != null && fromTeam.equals(toTeam)) return;

        PlayerClaimChangeEvent claimChangeEvent = new PlayerClaimChangeEvent(player, toTeam, fromTeam);
        LegendBukkit.getInstance().getServer().getPluginManager().callEvent(claimChangeEvent);

        if (claimChangeEvent.isCancelled()) event.setTo(from);

    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo(), from = event.getFrom();

        if (to == null) return;

        Team fromTeam = this.claimHandler.getTeam(from).orElse(null), toTeam = this.claimHandler.getTeam(to).orElse(null);

        if (fromTeam == null && toTeam == null) return;
        if (fromTeam != null && toTeam != null && fromTeam.equals(toTeam)) return;

        PlayerClaimChangeEvent claimChangeEvent = new PlayerClaimChangeEvent(player, toTeam, fromTeam);
        LegendBukkit.getInstance().getServer().getPluginManager().callEvent(claimChangeEvent);

        if (claimChangeEvent.isCancelled()) event.setTo(from);

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClaimChange(PlayerClaimChangeEvent event) {
        if (event.isCancelled()) return;

        Team toTeam = event.getToTeam(), fromTeam = event.getFromTeam();
        Player player = event.getPlayer();

        if (!Setting.CLAIM_ENTER_LEAVE.isToggled(player)) return;

        if (fromTeam == null && toTeam == null) return;

        if (fromTeam != null && toTeam != null) {
            if (fromTeam.equals(toTeam)) return;

            sendLandChange(player, toTeam, fromTeam);
        }

        if (fromTeam == null) {
            sendLandChange(player, toTeam, null);
        }

        if (toTeam == null) {
            sendLandChange(player, null, fromTeam);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Optional<Team> teamOpt = this.claimHandler.getTeam(block.getLocation());

        teamOpt.ifPresent(team -> {
            if (team.isMember(player.getUniqueId()) || team.isRaidable()) return;
            if (LegendConstants.isAdminBypass(player)) return;

            event.setCancelled(true);
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.edit.cant-break")
                    .replaceAll("%team%", team.getName(player))
            ));
        });
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Optional<Team> teamOpt = this.claimHandler.getTeam(block.getLocation());

        teamOpt.ifPresent(team -> {
            if (team.isMember(player.getUniqueId()) || team.isRaidable()) return;
            if (LegendConstants.isAdminBypass(player)) return;

            event.setCancelled(true);
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.edit.cant-place")
                    .replaceAll("%team%", team.getName(player))
            ));
        });
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Block block = event.getBlockClicked();
        Player player = event.getPlayer();
        Optional<Team> teamOpt = this.claimHandler.getTeam(block.getLocation());

        teamOpt.ifPresent(team -> {
            if (team.isMember(player.getUniqueId()) || team.isRaidable()) return;
            if (LegendConstants.isAdminBypass(player)) return;

            event.setCancelled(true);
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.edit.cant-place")
                    .replaceAll("%team%", team.getName(player))
            ));
        });
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBucketFill(PlayerBucketFillEvent event) {
        Block block = event.getBlockClicked();
        Player player = event.getPlayer();
        Optional<Team> teamOpt = this.claimHandler.getTeam(block.getLocation());

        teamOpt.ifPresent(team -> {
            if (team.isMember(player.getUniqueId()) || team.isRaidable()) return;
            if (LegendConstants.isAdminBypass(player)) return;

            List<LivingEntity> entities = getEntities(player);
            for (LivingEntity entity : entities) {
                if (!getLookingAt(player, entity)) continue;
                if (entity instanceof Cow) return;
            }

            event.setCancelled(true);
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.edit.cant-place")
                    .replaceAll("%team%", team.getName(player))
            ));
        });
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Block clicked = event.getClickedBlock();
        Player player = event.getPlayer();

        if (clicked == null) return;
        if (!event.getAction().name().contains("RIGHT_CLICK_")) return;
        if (!clicked.getType().isInteractable()) return;
        if (event.useInteractedBlock() == Event.Result.DENY) return;
        if (clicked.getType() == Material.AIR) return;

        Optional<Team> teamOpt = this.claimHandler.getTeam(clicked.getLocation());

        teamOpt.ifPresent(team -> {
            if (team.isMember(player.getUniqueId()) || team.isRaidable()) return;
            if (LegendConstants.isAdminBypass(player)) return;
            if (team.getTeamType() == TeamType.CITADEL) {
                if (Events.getInstance().getEvents().values().stream().anyMatch(e -> {
                    if (e instanceof Citadel citadel && citadel.canLoot(player)) return true;

                    return false;
                })) return;
            }
            if (team.getTeamType() == TeamType.SPAWN) {
                if (clicked.getType() == Material.ENCHANTING_TABLE || clicked.getType() == Material.CRAFTING_TABLE || clicked.getType() == Material.ANVIL)
                    return;
            }

            event.setUseInteractedBlock(Event.Result.DENY);
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.edit.cant-interact")
                    .replaceAll("%team%", team.getName(player))
            ));
        });

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent event) {
        Entity clicked = event.getRightClicked();
        Player player = event.getPlayer();

        if (!(clicked instanceof ItemFrame)) return;

        Optional<Team> teamOpt = this.claimHandler.getTeam(clicked.getLocation());

        teamOpt.ifPresent(team -> {
            if (team.isMember(player.getUniqueId()) || team.isRaidable()) return;
            if (LegendConstants.isAdminBypass(player)) return;

            event.setCancelled(true);
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.edit.cant-interact")
                    .replaceAll("%team%", team.getName(player))
            ));
        });
    }

    @EventHandler
    public void onLeaveDecay(LeavesDecayEvent event) {
        if (this.claimHandler.getClaim(event.getBlock().getLocation()) != null) event.setCancelled(true);
    }

    @EventHandler
    public void onOxidize(BlockFadeEvent event) {
        Block block = event.getBlock();
        Optional<Team> teamOpt = this.claimHandler.getTeam(block.getLocation());

        teamOpt.ifPresent(team -> {
            if (team.getTeamType() == TeamType.PLAYER) return;

            event.setCancelled(true);
        });
    }

    @EventHandler
    public void onPiston(BlockPistonRetractEvent event) {
        if (!(event.isSticky())) return;

        Block block = event.getBlock();
        Optional<Team> teamOpt = this.claimHandler.getTeam(block.getLocation());

        for (Block other : event.getBlocks()) {
            Optional<Team> otherTeamOpt = this.claimHandler.getTeam(other.getLocation());

            if (teamOpt.isPresent() && otherTeamOpt.isPresent()) {
                if (teamOpt.get().equals(otherTeamOpt.get())) continue;

                event.setCancelled(true);
            } else if (!teamOpt.isPresent() && otherTeamOpt.isPresent()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        Block block = event.getBlock();
        Optional<Team> teamOpt = this.claimHandler.getTeam(block.getLocation());

        for (Block other : event.getBlocks()) {
            Optional<Team> otherTeamOpt = this.claimHandler.getTeam(other.getLocation());

            if (teamOpt.isPresent() && otherTeamOpt.isPresent()) {
                if (teamOpt.get().equals(otherTeamOpt.get())) continue;

                event.setCancelled(true);
            } else if (!teamOpt.isPresent() && otherTeamOpt.isPresent()) {
                event.setCancelled(true);
            }
        }
    }

    private void sendLandChange(Player player, Team toTeam, Team fromTeam) {
        String type = LegendBukkit.getInstance().getLanguage().getString("team.claim.land-change.type");
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());
        String to = toTeam == null ? "Wilderness" : toTeam.getName(player);
        String from = fromTeam == null ? "Wilderness" : fromTeam.getName(player);

        if (type.equalsIgnoreCase("TITLE")) {
            player.sendTitle(CC.translate("&a&lEntering " + to), CC.translate("&cLeaving " + from));
        } else if (type.equalsIgnoreCase("ACTION_BAR")) {
            String entering = LegendBukkit.getInstance().getLanguage().getString("team.claim.land-change.entering").replaceAll("%team%", to);
            String leaving = LegendBukkit.getInstance().getLanguage().getString("team.claim.land-change.leaving").replaceAll("%team%", from);

            user.getQueuedActionBars().add(entering + leaving);
        } else {
            FancyBuilder builder = new FancyBuilder("")
                    .append(LegendBukkit.getInstance().getLanguage().getString("team.claim.land-change.entering").replaceAll("%team%", to))
                    .hover(LegendBukkit.getInstance().getLanguage().getString("team.claim.land-change.hover").replaceAll("%team%", to))
                    .click(ClickEvent.Action.RUN_COMMAND, "/t i " + (toTeam == null ? "Wilderness" : toTeam.getName()))
                    .append(LegendBukkit.getInstance().getLanguage().getString("team.claim.land-change.leaving").replaceAll("%team%", from))
                    .hover(LegendBukkit.getInstance().getLanguage().getString("team.claim.land-change.hover").replaceAll("%team%", from))
                    .click(ClickEvent.Action.RUN_COMMAND, "/t i " + (fromTeam == null ? "Wilderness" : fromTeam.getName()));

            builder.send(player);
        }
    }


    public static boolean getLookingAt(Player player, LivingEntity livingEntity) {
        Location eye = player.getEyeLocation();
        Vector toEntity = livingEntity.getEyeLocation().toVector().subtract(eye.toVector());
        double dot = toEntity.normalize().dot(eye.getDirection());

        return dot > 0.99D;
    }

    public static List<LivingEntity> getEntities(Player player) {
        List<LivingEntity> entities = new ArrayList<>();
        for (Entity e : player.getNearbyEntities(10, 10, 10)) {
            if (e instanceof LivingEntity) {
                if (getLookingAt(player, (LivingEntity) e)) {
                    entities.add((LivingEntity) e);
                }
            }
        }

        return entities;
    }

}
