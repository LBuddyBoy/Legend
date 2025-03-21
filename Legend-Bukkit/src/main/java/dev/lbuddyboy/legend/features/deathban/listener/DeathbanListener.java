package dev.lbuddyboy.legend.features.deathban.listener;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.features.deathban.DeathbanHandler;
import dev.lbuddyboy.legend.features.deathban.model.RespawnBlock;
import dev.lbuddyboy.legend.user.model.LegendUser;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

public class DeathbanListener implements Listener {

    private final DeathbanHandler deathbanHandler = LegendBukkit.getInstance().getDeathbanHandler();

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(event.getUniqueId());

        if (!user.isTimerActive("deathban")) return;
        if (this.deathbanHandler.isArenaSetup()) return;
        if (!SettingsConfig.SETTINGS_DEATHBANS.getBoolean()) return;
        if (user.getLives() > 0) return;

        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        event.setKickMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("deathban.kick-message")
                .replaceAll("%duration%", user.getRemaining("deathban"))
        ));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!SettingsConfig.SETTINGS_DEATHBANS.getBoolean()) return;

        Player player = event.getPlayer();
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (user.isCombatLoggerDied()) {
            this.deathbanHandler.handleRevive(player);
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("combat-logger.died-rejoin")));
            return;
        }

        if (!user.isTimerActive("deathban")) {
            if (!user.isDeathBanned()) return;

            this.deathbanHandler.handleRevive(player);
            return;
        }

        this.deathbanHandler.handleRejoin(player, true);
    }

    @EventHandler
    public void onCommandProcess(PlayerCommandPreprocessEvent event) {
        if (!SettingsConfig.SETTINGS_DEATHBANS.getBoolean()) return;

        Player player = event.getPlayer();
        String command = event.getMessage().toLowerCase();
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (!user.isDeathBanned()) return;
        if (player.isOp()) return;

        for (String whitelistedCommand : LegendBukkit.getInstance().getDeathbanHandler().getConfig().getStringList("allowed-commands")) {
            if (command.toLowerCase().startsWith(whitelistedCommand)) {
                return;
            }
        }

        event.setCancelled(true);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("deathban.disallowed-command")));
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!SettingsConfig.SETTINGS_DEATHBANS.getBoolean()) return;

        Player player = event.getPlayer();
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (!user.isDeathBanned()) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        this.deathbanHandler.handleDeathban(player, false);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        if (!SettingsConfig.SETTINGS_DEATHBANS.getBoolean()) {
            event.setRespawnLocation(LegendBukkit.getInstance().getSpawnLocation());
            return;
        }

        Player player = event.getPlayer();
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (!user.isDeathBanned()) {
            event.setRespawnLocation(LegendBukkit.getInstance().getSpawnLocation());
            return;
        }

        if (this.deathbanHandler.getTeam() != null && this.deathbanHandler.getTeam().getHome() != null) {
            event.setRespawnLocation(this.deathbanHandler.getTeam().getHome());
        } else {
            event.setRespawnLocation(LegendBukkit.getInstance().getSpawnLocation());
        }

        this.deathbanHandler.handleRejoin(player, false);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!SettingsConfig.SETTINGS_DEATHBANS.getBoolean()) return;

        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        if (this.deathbanHandler.getSafeZone() != null && this.deathbanHandler.getSafeZone().contains(player.getLocation())) {
            event.setCancelled(true);
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());
            if (!user.isDeathBanned()) return;

            event.setCancelled(true);
            return;
        }

        boolean dead = player.getHealth() - event.getFinalDamage() <= 0;
        if (!dead) return;

        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());
        if (!user.isDeathBanned()) return;

        event.setCancelled(true);
        event.setDamage(0);
        this.deathbanHandler.handleDeath(player);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!SettingsConfig.SETTINGS_DEATHBANS.getBoolean()) return;

        Player player = event.getPlayer();
        Block block = event.getBlock();
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (!user.isDeathBanned()) return;
        if (block.getType() != this.deathbanHandler.getBreakMaterial()) return;

        if (user.getLastBrokenAt() > System.currentTimeMillis()) {
            event.setCancelled(true);
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("deathban.credits.delay")));
            return;
        }

        if (user.getLastBrokenBlock() != null) {
            if (user.getLastBrokenBlock().getBlockX() == block.getLocation().getBlockX()
                    && user.getLastBrokenBlock().getBlockY() == block.getLocation().getBlockY()
                    && user.getLastBrokenBlock().getBlockZ() == block.getLocation().getBlockZ()
            ) {
                event.setCancelled(true);
                player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("deathban.credits.same-block")));
                return;
            }
        }

        if (user.getLastBrokenLocation() != null) {
            if (user.getLastBrokenLocation().getBlockX() == player.getLocation().getBlockX()
                    && user.getLastBrokenLocation().getBlockY() == player.getLocation().getBlockY()
                    && user.getLastBrokenLocation().getBlockZ() == player.getLocation().getBlockZ()
            ) {
                event.setCancelled(true);
                player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("deathban.credits.same-location")));
                return;
            }
        }

        user.setCredits(user.getCredits() + this.deathbanHandler.getCreditReward());
        user.setLastBrokenLocation(player.getLocation());
        user.setLastBrokenBlock(block.getLocation());
        user.setLastBrokenAt(System.currentTimeMillis() + (this.deathbanHandler.getBreakDelay() * 1000L));

        if (user.getCredits() >= this.deathbanHandler.getCreditsNeeded()) {
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("deathban.credits.reached")));
            this.deathbanHandler.handleRevive(player);
        } else {
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("deathban.credits.mined")
                    .replaceAll("%goal%", APIConstants.formatNumber(this.deathbanHandler.getCreditsNeeded() - user.getCredits()))
            ));
        }

        event.setCancelled(true);
        Tasks.run(() -> {
            block.setType(Material.BEDROCK);
            this.deathbanHandler.getRespawnBlocks().add(new RespawnBlock(block.getLocation(), System.currentTimeMillis()));
        });
    }

}
