package dev.lbuddyboy.legend.user.listener;

import com.mojang.authlib.properties.Property;
import dev.lbuddyboy.crates.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.user.model.nametag.ScoreboardEntryType;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.user.model.LegendUser;
import dev.lbuddyboy.legend.util.BukkitUtil;
import dev.lbuddyboy.legend.util.NameTagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.UUID;

public class UserListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        String name = event.getName();

        try {
            LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(uuid);

            user.setName(name);
            user.setJoinedAt(System.currentTimeMillis());

            LegendBukkit.getInstance().getUserHandler().getUserCache().put(uuid, user);
        } catch (Exception e) {
            LegendBukkit.getInstance().getUserHandler().getUserCache().put(uuid, new LegendUser(uuid, name));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());
        Property property = ((CraftPlayer)player).getProfile().getProperties().get("textures").stream().findFirst().orElse(null);

        if (property != null) {
            user.setHeadTextureValue(property.value());
        }

        if (!user.isPlayedBefore()) {
            user.setBalance(SettingsConfig.SETTINGS_STARTING_BALANCE.getDouble());
            user.setPlayedBefore(true);

            if (!SettingsConfig.KITMAP_ENABLED.getBoolean()) {
                user.applyTimer("invincibility", 60_000L * 60);
            }

            player.teleport(LegendBukkit.getInstance().getSpawnLocation());
        }

        if (user.isTimerActive("invincibility")) {
            user.resumeTimer("invincibility");
            if (TeamType.SPAWN.appliesAt(player.getLocation())) {
                user.pauseTimer("invincibility");
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (user.isTimerActive("invincibility")) {
            user.pauseTimer("invincibility");
        }

        user.setPlayTime(user.getPlayTime() + user.getActivePlayTime());
        user.setJoinedAt(-1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (SettingsConfig.KITMAP_ENABLED.getBoolean()) return;

        if (user.isDeathBanned()) return;

        user.applyTimer("invincibility", 60_000L * 30);
        if (TeamType.SPAWN.appliesAt(event.getRespawnLocation())) {
            user.pauseTimer("invincibility");
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        LegendUser victimUser = LegendBukkit.getInstance().getUserHandler().getUser(victim.getUniqueId());

        for (Player player : Bukkit.getOnlinePlayers()) {
            LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, ((CraftWorld)player.getWorld()).getHandle());
            Location location = victim.getLocation();

            bolt.setVisualOnly(true);

            ((CraftPlayer)player).getHandle().connection.sendPacket(new ClientboundAddEntityPacket(bolt, 0, new BlockPos(
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ()
            )));
        }

        victimUser.setDeaths(victimUser.getDeaths() + 1);
        victimUser.setKillStreak(0);
        victimUser.removeTimer("invincibility");

        if (victim.getKiller() == null) return;

        LegendUser killerUser = LegendBukkit.getInstance().getUserHandler().getUser(victim.getKiller().getUniqueId());

        killerUser.setKillStreak(killerUser.getCurrentKillStreak() + 1);
        killerUser.setKills(killerUser.getKills() + 1);
        if (killerUser.getCurrentKillStreak() > killerUser.getHighestKillStreak()) killerUser.setHighestKillStreak(killerUser.getCurrentKillStreak());

    }

}
