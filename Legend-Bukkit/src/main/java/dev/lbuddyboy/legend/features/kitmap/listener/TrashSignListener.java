package dev.lbuddyboy.legend.features.kitmap.listener;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.LegendConstants;
import dev.lbuddyboy.legend.command.impl.TrashCommand;
import dev.lbuddyboy.legend.features.kitmap.kit.Kit;
import dev.lbuddyboy.legend.features.kitmap.kit.menu.EditKitMenu;
import dev.lbuddyboy.legend.util.Cooldown;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TrashSignListener implements Listener {

    private final Cooldown signCooldown = new Cooldown(1);

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (event.getLines().length < 4) return;
        String header = event.getLine(0);
        if (!event.getPlayer().isOp()) return;
        if (!CC.stripColor(header).contains("- Trash -")) return;

        event.setLine(0, CC.translate(header));
        event.getPlayer().sendMessage(CC.translate("&aTrash created!"));
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null) return;
        if (!(block.getState() instanceof Sign)) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Sign sign = (Sign) block.getState();
        if (sign.getLines().length < 1) return;

        String header = sign.getLine(0);

        if (!CC.stripColor(header).equalsIgnoreCase("- Trash -")) return;

        if (!player.isOp() && !LegendConstants.isAdminBypass(player) && !player.isSneaking()) {
            event.setCancelled(true);
        }

        TrashCommand.def(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.signCooldown.remove(event.getPlayer().getUniqueId());
    }

}
