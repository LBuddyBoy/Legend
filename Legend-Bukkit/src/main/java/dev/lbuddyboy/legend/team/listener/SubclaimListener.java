package dev.lbuddyboy.legend.team.listener;

import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.ClaimHandler;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamMember;
import dev.lbuddyboy.legend.team.model.TeamRole;
import dev.lbuddyboy.legend.util.UUIDUtils;
import dev.lbuddyboy.rollback.Rollback;
import dev.lbuddyboy.rollback.locator.LocationType;
import dev.lbuddyboy.rollback.locator.model.ItemCache;
import dev.lbuddyboy.rollback.locator.model.ItemLocation;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class SubclaimListener implements Listener {

    private final ClaimHandler claimHandler = LegendBukkit.getInstance().getTeamHandler().getClaimHandler();
    private final BlockFace[] SIGN_FACES = new BlockFace[]{
            BlockFace.SOUTH,
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.WEST
    };

    @EventHandler
    public void onSign(SignChangeEvent event) {
        String[] lines = event.getLines();

        if (lines.length < 2) return;
        if (!lines[0].equalsIgnoreCase("[Subclaim]")) return;

        event.setLine(0, CC.translate("&6&l[Subclaim]"));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clicked = event.getClickedBlock();

        if (clicked == null) return;
        if (processSubclaimBlock(player, clicked)) return;

        event.setUseInteractedBlock(Event.Result.DENY);
        player.sendMessage(CC.translate("&cYou do not have access to this subclaim."));
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getState() instanceof Sign) {
            if (!isSubclaimSign((Sign) block.getState())) return;

            Team teamAt = this.claimHandler.getTeam(block.getLocation()).orElse(null);
            if (teamAt == null) return;
            if (!teamAt.isMember(player.getUniqueId())) return;
            if (hasPermission(teamAt.getMember(player.getUniqueId()).get(), ((Sign) block.getState())))
                return;

            event.setCancelled(true);
            player.sendMessage(CC.translate("&cYou do not have access to this subclaim."));
            return;
        }

        if (processSubclaimBlock(player, block)) return;

        event.setCancelled(true);
        player.sendMessage(CC.translate("&cYou do not have access to this subclaim."));
    }

    @EventHandler
    public void onTransferInitiator(InventoryMoveItemEvent event) {
        Inventory initiator = event.getInitiator(), destination = event.getDestination();
        if (!(destination.getHolder() instanceof Hopper)) return;

        Hopper hopper = (Hopper) destination.getHolder();
        if (processSubclaimBlock(null, hopper.getBlock())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPiston(BlockPistonRetractEvent event) {
        if (!(event.isSticky())) return;

        Block block = event.getBlock();
        if (this.processSubclaimBlock(null, block)) return;

        for (Block other : event.getBlocks()) {
            if (this.processSubclaimBlock(null, other)) continue;

            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        Block block = event.getBlock();
        if (this.processSubclaimBlock(null, block)) return;

        for (Block other : event.getBlocks()) {
            if (this.processSubclaimBlock(null, other)) continue;

            event.setCancelled(true);
            return;
        }
    }

    public boolean processSubclaimBlock(Player player, Block block) {
        Sign sign = getSubclaimSign(block);
        if (sign == null) return true;

        if (player != null) {
            Team teamAt = this.claimHandler.getTeam(block.getLocation()).orElse(null);
            if (teamAt == null) return true;
            if (!teamAt.isMember(player.getUniqueId())) return true;

            return hasPermission(teamAt.getMember(player.getUniqueId()).get(), sign);
        }

        return false;
    }

    public Sign getSubclaimSign(Block block) {
        for (BlockFace face : SIGN_FACES) {
            Block relative = block.getRelative(face);

            if (!(relative.getState() instanceof Sign)) continue;
            if (!(isSubclaimSign((Sign) relative.getState()))) continue;

            return (Sign) relative.getState();
        }

        return null;
    }

    public boolean isSubclaimSign(Sign sign) {
        if (sign.getLines().length < 2) return false;

        String header = sign.getLine(0);

        return CC.stripColor(header).equalsIgnoreCase("[Subclaim]");
    }

    public boolean hasPermission(TeamMember member, Sign sign) {
        List<String> allowed = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            if (sign.getLines().length < i + 1) continue;

            String line = sign.getLine(i);
            allowed.addAll(Arrays.stream(line.split(",")).collect(Collectors.toList()));
        }

        for (String line : allowed) {
            if (!isPermitted(member, line)) continue;

            return true;
        }

        return false;
    }

    public boolean isPermitted(TeamMember member, String line) {
        if (line.equalsIgnoreCase("Leader") || line.equalsIgnoreCase("Leaders"))
            return member.isAtLeast(TeamRole.LEADER);
        else if (line.equalsIgnoreCase("CoLeader") || line.equalsIgnoreCase("CoLeaders")
                || line.equalsIgnoreCase("Co-Leader") || line.equalsIgnoreCase("Co-Leaders")
                || line.equalsIgnoreCase("Co Leader") || line.equalsIgnoreCase("Co Leaders")
        ) return member.isAtLeast(TeamRole.CO_LEADER);
        else if (line.equalsIgnoreCase("Captain") || line.equalsIgnoreCase("Captains")
        ) return member.isAtLeast(TeamRole.CAPTAIN);

        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(member.getUuid()).orElse(null);

        System.out.println(" ");
        System.out.println(line);

        if (line.equalsIgnoreCase(member.getName())) return true;
        if (member.getRole() == TeamRole.LEADER) return true;

        if (team != null) {
            UUID targetUUID = UUIDUtils.uuid(line);
            if (targetUUID != null) {
                TeamRole role = team.getMember(targetUUID).map(TeamMember::getRole).orElse(TeamRole.MEMBER);

                return member.getRole().getWeight() > role.getWeight();
            }
        }

        return false;
    }

}
