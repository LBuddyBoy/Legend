package dev.lbuddyboy.legend.features.kitmap.listener;

import dev.lbuddyboy.legend.team.model.TeamType;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class BlockBoostListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (event.getTo().getX() == event.getFrom().getX() && event.getTo().getY() == event.getFrom().getY() && event.getTo().getZ() == event.getFrom().getZ()) {
            return;
        }

        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.SLIME_BLOCK) return;
        if (!TeamType.SPAWN.appliesAt(event.getPlayer().getLocation())) return;

        double x = event.getPlayer().getLocation().getDirection().getX();
        double y = event.getPlayer().getLocation().getDirection().getY();
        double z = event.getPlayer().getLocation().getDirection().getZ();

        player.setVelocity(new Vector(
                x * 2.5,
                y + 1.0,
                z * 2.5));

        player.playSound(event.getPlayer().getLocation(), Sound.BLOCK_LADDER_STEP, 10.0F, 5.0F);
        player.playEffect(event.getPlayer().getLocation(), Effect.STEP_SOUND, Material.SLIME_BLOCK);
    }


}
