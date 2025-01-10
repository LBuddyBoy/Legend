package dev.lbuddyboy.legend.api.providers;

import dev.lbuddyboy.arrow.staffmode.StaffModeConstants;
import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.scoreboard.MiniGameScoreboard;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.timer.impl.CombatTimer;
import dev.lbuddyboy.legend.timer.impl.InvincibilityTimer;
import dev.lbuddyboy.minigames.MiniGames;
import dev.lbuddyboy.minigames.api.MiniGameJoinEvent;
import dev.lbuddyboy.minigames.api.MiniGameLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class MiniGamesProvider implements Listener, IModule {

    public MiniGamesProvider() {
        load();
    }

    public boolean isInMiniGame(Player player) {
        return MiniGames.getInstance().getMiniGame(player).isPresent();
    }

    @EventHandler
    public void onJoin(MiniGameJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasMetadata(StaffModeConstants.VANISH_META_DATA) || player.hasMetadata(StaffModeConstants.STAFF_MODE_META_DATA)) {
            player.sendMessage(CC.translate("&cYou cannot join minigames whilst in staff mode."));
            return;
        }

        if (LegendBukkit.getInstance().getTimerHandler().getTimer(CombatTimer.class).isActive(player.getUniqueId())) {
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("combat-tagged.denied")));
            event.setCancelled(true);
            return;
        }

        if (LegendBukkit.getInstance().getTimerHandler().getTimer(InvincibilityTimer.class).isActive(player.getUniqueId())) {
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("invincibility.cannot-enter")
                    .replaceAll("%team%", "&eMiniGame")
            ));
            event.setCancelled(true);
            return;
        }

        if (!TeamType.SPAWN.appliesAt(player.getLocation()) && !LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getTeam(player.getLocation()).map(at -> at.isMember(player.getUniqueId())).orElse(false)) {
            player.sendMessage(CC.translate("&cYou need to be either in a safezone or in your claim to do this."));
            return;
        }

    }

    @EventHandler
    public void onQuit(MiniGameLeaveEvent event) {
        Player player = event.getPlayer();


        LegendBukkit.getInstance().getTimerHandler().getTimer(CombatTimer.class).remove(player.getUniqueId());
        player.teleport(LegendBukkit.getInstance().getSpawnLocation());
    }

    @Override
    public void load() {
        CommonsPlugin.getInstance().getScoreboardHandler().registerProvider(new MiniGameScoreboard());
        Bukkit.getPluginManager().registerEvents(this, LegendBukkit.getInstance());
    }

    @Override
    public void unload() {

    }
}
