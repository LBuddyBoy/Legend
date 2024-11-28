package dev.lbuddyboy.legend.team.listener;

import dev.lbuddyboy.commons.component.FancyBuilder;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.api.PlayerClaimChangeEvent;
import dev.lbuddyboy.legend.settings.Setting;
import dev.lbuddyboy.legend.team.ClaimHandler;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Optional;

public class TeamClaimListener implements Listener {

    private final ClaimHandler claimHandler = LegendBukkit.getInstance().getTeamHandler().getClaimHandler();

    @EventHandler
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onClaimChange(PlayerClaimChangeEvent event) {
        if (event.isCancelled()) return;

        Team toTeam = event.getToTeam(), fromTeam = event.getFromTeam();
        Player player = event.getPlayer();

        if (!Setting.CLAIM_ENTER_LEAVE.isToggled(player)) return;

        if (fromTeam == null && toTeam == null) return;

        if (fromTeam != null && toTeam != null) {
            if (fromTeam.equals(toTeam)) return;

            sendLandChange(player, toTeam.getName(player), fromTeam.getName(player));
        }

        if (fromTeam == null) {
            sendLandChange(player, toTeam.getName(player), "Wilderness");
        }

        if (toTeam == null) {
            sendLandChange(player, "Wilderness", fromTeam.getName(player));
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Optional<Team> teamOpt = this.claimHandler.getTeam(block.getLocation());

        teamOpt.ifPresent(team -> {
            if (team.isMember(player.getUniqueId()) || team.isRaidable()) return;

            event.setCancelled(true);
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.edit.cant-break")
                    .replaceAll("%team%", team.getName(player))
            ));
        });
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Optional<Team> teamOpt = this.claimHandler.getTeam(block.getLocation());

        teamOpt.ifPresent(team -> {
            if (team.isMember(player.getUniqueId()) || team.isRaidable()) return;

            event.setCancelled(true);
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.edit.cant-break")
                    .replaceAll("%team%", team.getName(player))
            ));
        });
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Optional<Team> teamOpt = this.claimHandler.getTeam(block.getLocation());

        teamOpt.ifPresent(team -> {
            if (team.isMember(player.getUniqueId()) || team.isRaidable()) return;

            event.setCancelled(true);
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.edit.cant-place")
                    .replaceAll("%team%", team.getName(player))
            ));
        });
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Optional<Team> teamOpt = this.claimHandler.getTeam(block.getLocation());

        teamOpt.ifPresent(team -> {
            if (team.isMember(player.getUniqueId()) || team.isRaidable()) return;

            event.setCancelled(true);
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.edit.cant-place")
                    .replaceAll("%team%", team.getName(player))
            ));
        });
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block clicked = event.getClickedBlock();
        Player player = event.getPlayer();

        if (clicked == null) return;
        if (!clicked.getType().isInteractable()) return;

        Optional<Team> teamOpt = this.claimHandler.getTeam(clicked.getLocation());

        teamOpt.ifPresent(team -> {
            if (team.isMember(player.getUniqueId()) || team.isRaidable()) return;

            event.setUseInteractedBlock(Event.Result.DENY);
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
            } else if (teamOpt.isEmpty() && otherTeamOpt.isPresent()) {
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
            } else if (teamOpt.isEmpty() && otherTeamOpt.isPresent()) {
                event.setCancelled(true);
            }
        }
    }

    private void sendLandChange(Player player, String to, String from) {
        player.sendTitle(CC.translate("&a&lEntering " + to), CC.translate("&cLeaving " + from));

        FancyBuilder builder = new FancyBuilder("")
                .append(LegendBukkit.getInstance().getLanguage().getString("team.claim.land-change.entering").replaceAll("%team%", to))
                .hover(LegendBukkit.getInstance().getLanguage().getString("team.claim.land-change.hover").replaceAll("%team%", to))
                .click(ClickEvent.Action.RUN_COMMAND, "/t i " + to)
                .append(LegendBukkit.getInstance().getLanguage().getString("team.claim.land-change.leaving").replaceAll("%team%", from))
                .hover(LegendBukkit.getInstance().getLanguage().getString("team.claim.land-change.hover").replaceAll("%team%", from))
                .click(ClickEvent.Action.RUN_COMMAND, "/t i " + from);

        builder.send(player);
    }

}
