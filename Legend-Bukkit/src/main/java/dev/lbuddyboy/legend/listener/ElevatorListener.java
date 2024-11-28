package dev.lbuddyboy.legend.listener;

import dev.lbuddyboy.commons.util.CC;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ElevatorListener implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        String[] lines = event.getLines();

        if (lines.length < 2) return;
        if (!lines[0].equalsIgnoreCase("[Elevator]")) return;

        event.setLine(0, CC.translate("&6&l[Elevator]"));

        if (lines[1].toLowerCase().contains("down")) event.setLine(1, "Down");
        else {
            event.setLine(1, "Up");
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clicked = event.getClickedBlock();

        if (clicked == null) return;
        if (!(clicked.getState() instanceof Sign)) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Sign sign = (Sign) clicked.getState();
        if (sign.getLines().length < 2) return;
        if (!CC.stripColor(sign.getLine(0)).equalsIgnoreCase("[Elevator]")) return;

        String direction = sign.getLine(1);

        Location signLocation = sign.getLocation().clone();
        Location location = findLocation(signLocation, direction.equalsIgnoreCase("Up"));

        if (location != null) {
            location.setYaw(player.getLocation().getYaw());
            location.setPitch(player.getLocation().getPitch());
            player.teleport(location);
            return;
        }

        player.sendMessage(CC.translate("&cCould not find a valid location for you to teleport to."));
    }

    public Location findLocation(Location signLocation, boolean up) {
        Location location = null;

        if (up) {
            for (int y = signLocation.getBlockY(); y <= signLocation.getWorld().getMaxHeight(); y++) {
                signLocation.setY(y);
                Block block = signLocation.getBlock();

                if (block.getType().isSolid()) continue;
                if (block.getRelative(BlockFace.UP).getType().isSolid()) continue;

                location = block.getLocation().clone();
                break;
            }
        } else {
            for (int y = signLocation.getBlockY(); y >= 1; y--) {
                signLocation.setY(y);
                Block block = signLocation.getBlock();

                if (block.getType().isSolid()) continue;
                if (block.getRelative(BlockFace.UP).getType().isSolid()) continue;

                location = block.getLocation().clone();
                break;
            }
        }

        if (location != null) {
            location.setX(location.getBlockX() + 0.5);
            location.setZ(location.getBlockZ() + 0.5);
        }

        return location;
    }

}
