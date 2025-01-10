package dev.lbuddyboy.legend.team.listener;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.LocationUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.LegendConstants;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.team.ClaimHandler;
import dev.lbuddyboy.legend.team.TeamHandler;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.claim.Claim;
import dev.lbuddyboy.legend.team.model.claim.ClaimMapView;
import dev.lbuddyboy.legend.team.model.claim.ClaimProcess;
import dev.lbuddyboy.legend.user.model.LegendUser;
import dev.lbuddyboy.legend.util.Cuboid;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.Set;

public class ClaimWandListener implements Listener {

    private final TeamHandler teamHandler = LegendBukkit.getInstance().getTeamHandler();
    private final ClaimHandler claimHandler = this.teamHandler.getClaimHandler();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block clicked = event.getClickedBlock();
        boolean rightClick = event.getAction() == Action.RIGHT_CLICK_BLOCK;
        Player player = event.getPlayer();
        boolean confirming = event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK;
        Optional<Team> teamOpt = this.teamHandler.getTeam(player);
        boolean systemClaim = false;

        if (!claimHandler.isClaimWand(event.getItem())) return;

        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);

        if (this.claimHandler.getClaimingFor(event.getItem()).isPresent() && player.hasPermission("legend.command.systemteam")) {
            teamOpt = this.claimHandler.getClaimingFor(event.getItem());
            systemClaim = true;
        }

        if (!teamOpt.isPresent()) {
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.no-team.sender")));
            return;
        }

        if (!systemClaim) {
            if (player.getWorld().getEnvironment() == World.Environment.NETHER && !SettingsConfig.NETHER_PLAYERS_CAN_CLAIM.getBoolean()
                    || player.getWorld().getEnvironment() == World.Environment.THE_END && !SettingsConfig.END_PLAYERS_CAN_CLAIM.getBoolean()) {
                player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.error.not-enabled-world")));
                return;
            }
        }

        Team team = teamOpt.get();
        ClaimProcess process = this.claimHandler.getClaimProcesses().getOrDefault(player.getUniqueId(), new ClaimProcess());
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        /*
        Confirming Claim Selection
         */

        if (player.isSneaking() && confirming) {
            if (process.getPositionOne() == null) {
                player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.not-complete.position-one")));
                return;
            }

            if (process.getPositionTwo() == null) {
                player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.not-complete.position-two")));
                return;
            }

            if (!systemClaim) {
                if (team.getClaims().size() >= this.claimHandler.getMaximumClaims()) {
                    player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.error.too-many")));
                    return;
                }
            }

            Claim claim = new Claim(team.getId(), process.getPositionOne(), process.getPositionTwo());
            Cuboid bounds = claim.getBounds();

            if (!systemClaim) {
                if (team.getBalance() < process.getPrice()) {
                    player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.error.broke")));
                    return;
                }

                if (this.claimHandler.isClaimOverLapping(claim)) {
                    player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.error.overlapped")));
                    return;
                }
            }

            team.setBalance(team.getBalance() - process.getPrice());
            team.getClaims().add(claim);
            team.flagSave();
            this.claimHandler.setClaim(team, claim);
            process.clearPillars(player);
            this.claimHandler.stopClaimProcess(player);

            ClaimMapView mapView = new ClaimMapView(player);
            mapView.showClaim(claim, Material.EMERALD_BLOCK);
            this.claimHandler.getMapViews().put(player.getUniqueId(), mapView);

            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.confirmed")
                    .replaceAll("%price%", APIConstants.formatNumber(user.getBalance()))
            ));

            player.getInventory().setItem(event.getHand(), null);
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            process.clearPillars(player);
            this.claimHandler.getClaimProcesses().remove(player.getUniqueId());
            return;
        }

        if (clicked == null) return;

        int minX = this.claimHandler.getMinimumSizeX(), minZ = this.claimHandler.getMinimumSizeZ();
        int maxX = this.claimHandler.getMaximumSizeX(), maxZ = this.claimHandler.getMaximumSizeZ();
        int bufferSize = this.claimHandler.getBufferSize();
        Set<Claim> touchingClaims = this.claimHandler.getClaims(clicked.getLocation(), bufferSize);

        if (!systemClaim) {
            if (this.claimHandler.getClaim(clicked.getLocation()) != null) {
                player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.error.occupied")));
                return;
            }

            if (!touchingClaims.isEmpty()) {
                player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.error.touching")));
                return;
            }

            if (LegendConstants.isUnclaimable(clicked.getLocation())) {
                player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.error.not-far-enough")
                        .replaceAll("%amount%", APIConstants.formatNumber(LegendConstants.getWilderness(clicked.getWorld())))
                ));
                return;
            }
        }

        if (process.getPositionOne() != null && process.getPositionTwo() != null) {
            Cuboid selection = process.getSelection();

            if (!systemClaim) {
                if (selection.getSizeX() < minX || selection.getSizeZ() < minZ) {
                    player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.error.too-small")));
                    return;
                }

                if (selection.getSizeX() > maxX || selection.getSizeZ() > maxZ) {
                    player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.error.too-big")));
                    return;
                }
            }

            LegendBukkit.getInstance().getLanguage().getStringList("team.claim.complete").forEach(s -> {
                player.sendMessage(CC.translate(s
                        .replaceAll("%size%", selection.getSizeX() + "x" + selection.getSizeZ())
                        .replaceAll("%price%", APIConstants.formatNumber(process.getPrice()))
                        .replaceAll("%position-one%", LocationUtils.toString(process.getPositionOne()))
                        .replaceAll("%position-two%", LocationUtils.toString(process.getPositionTwo()))
                ));
            });
        }

        process.setExact(this.claimHandler.isExactClaimWand(event.getItem()));

        if (!rightClick) {
            process.setPositionOne(clicked.getLocation());
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.position-one")
                    .replaceAll("%location%", LocationUtils.toString(process.getPositionOne()))
            ));
        } else {
            process.setPositionTwo(clicked.getLocation());
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.claim.position-two")
                    .replaceAll("%location%", LocationUtils.toString(process.getPositionTwo()))
            ));
        }

        process.showPillars(player);
        this.claimHandler.getClaimProcesses().put(player.getUniqueId(), process);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.claimHandler.stopClaimProcess(event.getPlayer());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();
        ItemStack itemStack = item.getItemStack();

        if (!this.claimHandler.isClaimWand(itemStack)) return;

        item.remove();
    }

}
