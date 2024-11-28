package dev.lbuddyboy.legend.features.glowstone;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.commons.util.LocationUtils;
import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.glowstone.listener.GlowstoneListener;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.claim.Claim;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GlowstoneHandler implements IModule {

    private final List<Long> notified = new ArrayList<>();
    private List<Location> glowstoneLocations;
    private Config config;
    @Setter private int blocksLeft;

    @Override
    public void load() {
        this.glowstoneLocations = new ArrayList<>();
        this.config = new Config(LegendBukkit.getInstance(), "glowstone-mountain.yml");

        this.glowstoneLocations.addAll(this.config.getStringList("locations").stream().map(LocationUtils::deserializeString).collect(Collectors.toList()));
        this.runDebug();

        Tasks.runTimer(() -> {
            if (!isSetup()) return;

            if (getNextReset() <= 0) {
                respawn();
                return;
            }
            for (Long interval : getNotifyIntervals()) {
                if (getNextReset() > interval) continue;
                if (notified.contains(interval)) continue;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("glowstone.reset-soon")
                            .replaceAll("%time%", TimeUtils.formatIntoDetailedString(interval))
                    ));
                }

                notified.add(interval);
            }
        }, 20, 20);

        Bukkit.getPluginManager().registerEvents(new GlowstoneListener(), LegendBukkit.getInstance());
        respawn();
    }

    @Override
    public void unload() {

    }

    public long getNextReset() {
        return (getLastReset() + getResetEvery()) - System.currentTimeMillis();
    }

    public List<Long> getNotifyIntervals() {
        return this.config.getStringList("notify-intervals").stream().map(TimeDuration::new).map(TimeDuration::transform).collect(Collectors.toList());
    }

    public long getResetEvery() {
        return new TimeDuration(this.config.getString("resets-every")).transform();
    }

    public long getLastReset() {
        return this.config.getLong("last-reset");
    }

    public Team getTeam() {
        return LegendBukkit.getInstance().getTeamHandler().getTeam("Glowstone").orElse(null);
    }

    public void runDebug() {
        LegendBukkit.getInstance().getLogger().warning(" ");
        LegendBukkit.getInstance().getLogger().warning(" ======== Glowstone Mountain ========");

        if (!isSetup()) {
            LegendBukkit.getInstance().getLogger().warning(CC.translate("&cThe glowstone mountain team is not setup!"));
            LegendBukkit.getInstance().getLogger().warning("Missing:");
            debug().forEach(s -> LegendBukkit.getInstance().getLogger().warning(s));
        } else {
            LegendBukkit.getInstance().getLogger().warning(CC.translate("&aThe glowstone mountain team is fully setup!"));
        }

        LegendBukkit.getInstance().getLogger().warning(" ");
    }

    public void respawn() {
        this.glowstoneLocations.forEach(l -> l.getBlock().setType(Material.GLOWSTONE));
        this.blocksLeft = this.glowstoneLocations.size();
        this.notified.clear();
        this.config.set("last-reset", System.currentTimeMillis());
        this.config.save();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("glowstone.reset-announcement")));
        }
    }

    public void scanLocations(CommandSender sender) {
        if (!isSetup()) {
            this.runDebug();
            sender.sendMessage(CC.translate("&cGlowstone Mountain isn't fully setup, check console for more info."));
            return;
        }

        this.glowstoneLocations.clear();

        for (Claim claim : getTeam().getClaims()) {
            for (Block block : claim.getBounds()) {
                if (block.getType() != Material.GLOWSTONE) continue;

                this.glowstoneLocations.add(block.getLocation().clone());
            }
        }

        sender.sendMessage(CC.translate("&aSuccessfully scanned " + this.glowstoneLocations.size() + " glowstone locations!"));
        this.saveLocations();
    }

    public void saveLocations() {
        this.config.set("locations", this.glowstoneLocations.stream().map(LocationUtils::serializeString).collect(Collectors.toList()));
        this.config.save();
    }

    public boolean isSetup() {
        return getTeam() != null && !getTeam().getClaims().isEmpty() && getTeam().getHome() != null;
    }

    public List<String> debug() {
        List<String> debugs = new ArrayList<>();

        if (isSetup()) return debugs;

        if (getTeam() == null) debugs.add("Team with the name 'Glowstone' not created.");
        if (getTeam() == null || getTeam() != null && getTeam().getClaims().isEmpty()) debugs.add("Team with the name 'Glowstone' has no claims.");
        if (getTeam() == null || getTeam() != null && getTeam().getHome() == null) debugs.add("Team with the name 'Glowstone' has no home set.");

        return debugs;
    }

}
