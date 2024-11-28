package dev.lbuddyboy.legend.features.deathban;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.util.*;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.deathban.listener.DeathbanListener;
import dev.lbuddyboy.legend.features.deathban.model.RespawnBlock;
import dev.lbuddyboy.legend.features.deathban.thread.DeathbanThread;
import dev.lbuddyboy.legend.team.model.Team;
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
        this.config = new Config(LegendBukkit.getInstance(), "deathban");

        new DeathbanThread().start();

        Bukkit.getPluginManager().registerEvents(new DeathbanListener(), LegendBukkit.getInstance());

        if (!LegendBukkit.getInstance().getSettings().getBoolean("server.deathbans", true)) return;

        Tasks.run(() -> {
            LegendBukkit.getInstance().getLogger().warning(" ");
            LegendBukkit.getInstance().getLogger().warning(" ======== Deathban Arena ========");

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

            getSafeZone();
        });

        Tasks.runTimer(this::respawnBlocks, 20, 20);
    }

    @Override
    public void unload() {
        this.respawnBlocks.forEach(RespawnBlock::respawn);
        this.respawnBlocks.clear();
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
        if (!this.config.contains("kit.armor") || !this.config.contains("kit.inventory")) return null;

        this.safeZone = new Cuboid(LocationUtils.deserializeString(this.config.getString("safezone.a")), LocationUtils.deserializeString(this.config.getString("safezone.b")));
        return safeZone;
    }

    public Map<String, Long> getDeathbanTimes() {
        return this.config.getConfigurationSection("ranks").getKeys(false).stream().collect(Collectors.toMap(
                String::toLowerCase,
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

    public void handleRejoin(Player player) {
        if (!LegendBukkit.getInstance().getSettings().getBoolean("server.deathbans", true)) return;

        Team team = getTeam();
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (team == null) {
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
        if (!LegendBukkit.getInstance().getSettings().getBoolean("server.deathbans", true)) return;

        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getActivePotionEffects().forEach(p -> player.removePotionEffect(p.getType()));
        player.teleport(LegendBukkit.getInstance().getSpawnLocation());
        user.removeTimer("deathban");
        user.setCredits(0);
        user.setDeathBanned(false);
        user.applyTimer("pvp_timer", new TimeDuration("30m").transform());
    }

    public void handleDeathban(Player player) {
        if (!LegendBukkit.getInstance().getSettings().getBoolean("server.deathbans", true)) return;
        if (player.hasPermission("legend.deathban.bypass")) return;

        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());
        long duration = this.getDeathbanTime(player);
        duration = Math.min(duration, user.getCurrentPlayTime());

        user.applyTimer("deathban", duration);
        user.setDeathBanned(true);
        player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("deathban.died")
                .replaceAll("%duration%", TimeUtils.formatIntoDetailedString(duration))
        ));
        player.setCanPickupItems(false);
    }

    public long getDeathbanTime(Player player) {
        return getDeathbanTimes().entrySet().stream().filter(e -> player.hasPermission(e.getKey())).map(Map.Entry::getValue).findFirst().orElse(60_000L);
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
        if (!LegendBukkit.getInstance().getSettings().getBoolean("server.deathbans", true)) return;

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

            player.teleport(getTeam().getHome());
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setHealth(20);
            player.setFoodLevel(20);
        }
    }

}
