package dev.lbuddyboy.legend.features.deathban;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.util.*;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.features.deathban.listener.DeathbanListener;
import dev.lbuddyboy.legend.features.deathban.model.RespawnBlock;
import dev.lbuddyboy.legend.features.deathban.thread.DeathbanThread;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.timer.impl.CombatTimer;
import dev.lbuddyboy.legend.user.model.LegendUser;
import dev.lbuddyboy.legend.util.Cuboid;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class DeathbanHandler implements IModule {

    private Config config;
    @Setter private Cuboid safeZone;
    private List<RespawnBlock> respawnBlocks;

    public DeathbanHandler() {
        this.respawnBlocks = new ArrayList<>();
    }

    @Override
    public void load() {
        reload();

        if (!SettingsConfig.SETTINGS_DEATHBANS.getBoolean()) return;

        new DeathbanThread().start();

        Bukkit.getPluginManager().registerEvents(new DeathbanListener(), LegendBukkit.getInstance());

        Tasks.run(() -> {
            LegendBukkit.getInstance().getLogger().warning(" ");
            LegendBukkit.getInstance().getLogger().warning(" ======== Deathban Arena ========");

            getSafeZone();

            if (!isArenaSetup()) {
                LegendBukkit.getInstance().getLogger().warning(CC.translate("&cThe deathban arena is not setup!"));
                LegendBukkit.getInstance().getLogger().warning("Missing:");
                if (getTeam() == null) {
                    LegendBukkit.getInstance().getLogger().warning("- A team under the name 'Deathban'");
                }
                if (getTeam() == null || getTeam() != null && getTeam().getClaims().isEmpty()) {
                    LegendBukkit.getInstance().getLogger().warning("- The 'Deathban' team is missing a claim");
                }
                if (getTeam() == null || getTeam() != null && getTeam().getHome() == null) {
                    LegendBukkit.getInstance().getLogger().warning("- The 'Deathban' team is missing a home");
                }
                if (!this.config.contains("safezone.a") || !this.config.contains("safezone.b")) {
                    LegendBukkit.getInstance().getLogger().warning("- You need to setup point a & b /deathban safezone seta|setb");
                }
                if (!this.config.contains("kit.armor") || !this.config.contains("kit.inventory")) {
                    LegendBukkit.getInstance().getLogger().warning("- You need to setup the kit contents /deathban kit setarmor|setinventory");
                }
            } else {
                LegendBukkit.getInstance().getLogger().warning(CC.translate("&aDeathban arena is fully setup!"));
            }

            LegendBukkit.getInstance().getLogger().warning(" ");
        });

        Tasks.runTimer(this::respawnBlocks, 20, 20);
    }

    @Override
    public void unload() {
        this.respawnBlocks.forEach(RespawnBlock::respawn);
        this.respawnBlocks.clear();
    }

    @Override
    public void reload() {
        this.config = new Config(LegendBukkit.getInstance(), "deathban");
    }

    public void respawnBlocks() {
        this.respawnBlocks.stream().filter(RespawnBlock::shouldRespawn).forEach(RespawnBlock::respawn);
        this.respawnBlocks.removeIf(RespawnBlock::shouldRespawn);
    }

    public ItemStack[] getKitArmor() {
        if (!this.config.contains("kit.armor")) return new ItemStack[4];

        return ItemUtils.itemStackArrayFromBase64(this.config.getString("kit.armor"));
    }

    public ItemStack[] getKitInventory() {
        if (!this.config.contains("kit.inventory")) return new ItemStack[36];

        return ItemUtils.itemStackArrayFromBase64(this.config.getString("kit.inventory"));
    }

    public boolean isSafeZoneSetup() {
        return this.safeZone != null;
    }

    public Cuboid getSafeZone() {
        if (this.safeZone != null) return safeZone;
        if (!this.config.contains("safezone.a") || !this.config.contains("safezone.b")) return null;

        this.safeZone = new Cuboid(LocationUtils.deserializeString(this.config.getString("safezone.a")), LocationUtils.deserializeString(this.config.getString("safezone.b")));
        return safeZone;
    }

    public Map<String, Long> getDeathbanTimes() {
        return this.config.getConfigurationSection("ranks").getKeys(false).stream().collect(Collectors.toMap(
                s -> "deathban." + s,
                s -> new TimeDuration(this.config.getString("ranks." + s)).transform()
        ));
    }

    public boolean isArenaSetup() {
        if (!this.config.contains("safezone.a") || !this.config.contains("safezone.b")) return false;

        return LegendBukkit.getInstance().getTeamHandler().getTeam("Deathban").map(t -> !t.getClaims().isEmpty() && t.getHome() != null).orElse(false) && isSafeZoneSetup();
    }

    public Team getTeam() {
        return LegendBukkit.getInstance().getTeamHandler().getTeam("Deathban").orElse(null);
    }

    public int getCreditReward() {
        return this.config.getInt("mining.credit-per-block", 1);
    }

    public int getRespawnDelay() {
        return this.config.getInt("mining.break-delay", 5);
    }

    public int getBreakDelay() {
        return this.config.getInt("mining.break-delay", 1);
    }

    public int getCreditsNeeded() {
        return this.config.getInt("mining.needed-credits", 500);
    }

    public Material getBreakMaterial() {
        return Material.getMaterial(this.config.getString("mining.credit-block", "COAL_ORE"));
    }

    public void handleRejoin(Player player, boolean checkLives) {
        if (!SettingsConfig.SETTINGS_DEATHBANS.getBoolean()) return;

        Team team = getTeam();
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (team == null) {
            if (user.getLives() > 0 && checkLives) {
                user.setLives(user.getLives() - 1);
                handleRevive(player);
                player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("deathban.revived.sender")
                        .replaceAll("%target%", "yourself")
                ));
                return;
            }

            player.kickPlayer(CC.translate(LegendBukkit.getInstance().getLanguage().getString("deathban.kick-message")
                    .replaceAll("%duration%", user.getRemaining("deathban"))
            ));
            return;
        }

        player.teleport(team.getHome());
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setCanPickupItems(true);
    }

    public void handleRevive(Player player) {
        if (!SettingsConfig.SETTINGS_DEATHBANS.getBoolean()) return;

        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        LegendBukkit.getInstance().getTimerHandler().getTimer(CombatTimer.class).remove(player.getUniqueId());
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getActivePotionEffects().forEach(p -> player.removePotionEffect(p.getType()));
        player.teleport(LegendBukkit.getInstance().getSpawnLocation());
        user.removeTimer("deathban");
        user.setCredits(0);
        user.setDeathBanned(false);
        user.setCombatLoggerDied(false);
        user.applyTimer("invincibility", 60_000L * 30);
    }

    public void handleDeathban(Player player, boolean force) {
        if (!SettingsConfig.SETTINGS_DEATHBANS.getBoolean()) return;
        if (!force && player.hasPermission("legend.deathban.bypass")) return;

        LegendBukkit.getInstance().getTimerHandler().getTimer(CombatTimer.class).remove(player.getUniqueId());
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());
        long duration = this.getDeathbanTime(player);
        duration = Math.min(duration, user.getTotalPlayTime());

        user.applyTimer("deathban", duration);
        user.setDeathBanned(true);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("deathban.died")
                .replaceAll("%duration%", TimeUtils.formatIntoDetailedString(duration))
        ));
        player.setCanPickupItems(false);
    }

    public long getDeathbanTime(Player player) {
        long time = Long.MAX_VALUE;

        for (Map.Entry<String, Long> entry : getDeathbanTimes().entrySet()) {
            if (player.hasPermission(entry.getKey().toLowerCase())) {
                if (entry.getValue() < time) time = entry.getValue();
            }
        }

        return time;
    }

    public List<Player> getPlayers() {
        if (isArenaSetup()) {
            Team team = getTeam();
            List<Player> players = new ArrayList<>();

            team.getClaims().forEach(c -> players.addAll(c.getBounds().getPlayers()));

            return players;
        }

        return new ArrayList<>();
    }

    public void handleDeath(Player player) {
        if (!SettingsConfig.SETTINGS_DEATHBANS.getBoolean()) return;

        if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) player.getLastDamageCause();
            Player damager = null;

            if (event.getDamager() instanceof Player) damager = (Player) event.getDamager();
            if (event.getDamager() instanceof Projectile && ((Projectile)event.getDamager()).getShooter() instanceof Player) damager = (Player) ((Projectile) event.getDamager()).getShooter();

            for (Player p : getPlayers()) {
                p.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("deathban.arena-death-message." + (damager == null ? "none" : "killer"))
                        .replaceAll("%killer%", (damager == null ? "N/A" : damager.getName()))
                        .replaceAll("%victim%", player.getName())
                ));
            }

            if (damager != null) {
                LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(damager.getUniqueId());

                user.decreaseTimer("deathban", new TimeDuration(this.config.getString("kill-reduction")).transform());
            }

            player.teleport(getTeam().getHome());
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setHealth(20);
            player.setFoodLevel(20);
        }
    }

}
