package dev.lbuddyboy.legend.features.leaderboard.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.api.cache.UUIDCache;
import dev.lbuddyboy.commons.util.LocationUtils;
import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.chat.enums.ChatMode;
import dev.lbuddyboy.legend.features.leaderboard.ILeaderBoardStat;
import dev.lbuddyboy.legend.features.leaderboard.LeaderBoardHologram;
import dev.lbuddyboy.legend.features.leaderboard.RotatingHologram;
import dev.lbuddyboy.legend.features.leaderboard.menu.LeaderBoardMenu;
import dev.lbuddyboy.legend.user.SamuraiUser;
import dev.lbuddyboy.legend.util.CC;
import dev.lbuddyboy.legend.util.PlayerUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@CommandAlias("leaderboards|leaderboard|lb")
public class LeaderBoardCommand extends BaseCommand {

    @Default
    public void leaderboard(Player sender) {
        new LeaderBoardMenu().openMenu(sender);
    }

    @Subcommand("update")
    @CommandPermission("hcf.command.leaderboard")
    public void update(CommandSender sender) {
        Tasks.runAsync(() -> {
            LegendBukkit.getInstance().getLeaderBoardHandler().update();
        });
    }

    @Subcommand("resetsstats")
    @CommandPermission("hcf.command.leaderboard")
    public void resetsstats(CommandSender sender) {

        Tasks.runAsync(() -> {
            for (Map.Entry<UUID, String> entry : UUIDCache.getUuidToNames().entrySet()) {
                String name = entry.getValue();
                UUID uuid = entry.getKey();
                if (name == null) continue;

                SamuraiUser user = LegendBukkit.getInstance().getUserHandler().loadUserAsync(uuid).join();

                user.getCooldowns().clear();
                user.getStats().clear();
                user.getToggles().clear();
                user.setPlayTime(0L);
                user.setFirstJoin(0L);
                user.setActivePower(null);
                user.setChatMode(ChatMode.PUBLIC);
                user.setBalance(1000);
                user.save(true);
            }
        });
    }

    @Subcommand("set")
    @CommandPermission("hcf.command.leaderboard")
    @CommandCompletion("@leaderboard-types")
    public void set(Player sender, @Name("leaderboard") String type) {
        Optional<ILeaderBoardStat> statOpt = LegendBukkit.getInstance().getLeaderBoardHandler().getStatById(type);

        if (statOpt.isEmpty()) {
            sender.sendMessage(CC.translate("&cInvalid leaderboard."));
            return;
        }

        LeaderBoardHologram hologram = LegendBukkit.getInstance().getLeaderBoardHandler().getHolograms().get(statOpt.get());

        hologram.setLocation(sender.getLocation());

        if (hologram.getHologram() != null) {
            hologram.despawn();
        }

        hologram.spawn();
        hologram.save();
    }

    @Subcommand("delete")
    @CommandPermission("hcf.command.leaderboard")
    @CommandCompletion("@leaderboard-types")
    public void delete(Player sender, @Name("leaderboard") String type) {
        Optional<ILeaderBoardStat> statOpt = LegendBukkit.getInstance().getLeaderBoardHandler().getStatById(type);

        if (statOpt.isEmpty()) {
            sender.sendMessage(CC.translate("&cInvalid leaderboard."));
            return;
        }

        LeaderBoardHologram hologram = LegendBukkit.getInstance().getLeaderBoardHandler().getHolograms().get(statOpt.get());

        hologram.setLocation(null);

        if (hologram.getHologram() != null) {
            hologram.despawn();
        }

        hologram.save();
    }

    @Subcommand("setrotating")
    @CommandPermission("hcf.command.leaderboard")
    public void set(Player sender) {
        RotatingHologram hologram = LegendBukkit.getInstance().getLeaderBoardHandler().getRotatingHologram();

        hologram.getLeaderBoardHologram().setLocation(sender.getLocation());
        hologram.getLeaderBoardHologram().update();
        LegendBukkit.getInstance().getLeaderBoardHandler().getConfig().set("rotating-hologram", LocationUtils.serializeString(sender.getLocation()));
        LegendBukkit.getInstance().getLeaderBoardHandler().getConfig().save();
    }

    @Subcommand("delrotating")
    @CommandPermission("hcf.command.leaderboard")
    public void delrotating(Player sender) {
        RotatingHologram hologram = LegendBukkit.getInstance().getLeaderBoardHandler().getRotatingHologram();

        hologram.getLeaderBoardHologram().despawn();
        LegendBukkit.getInstance().getLeaderBoardHandler().getConfig().set("rotating-hologram", "world;0;100;0;0;0;");
        LegendBukkit.getInstance().getLeaderBoardHandler().getConfig().save();
    }

    @Subcommand("rotate")
    @CommandPermission("hcf.command.leaderboard")
    public void rotate(Player sender) {
        RotatingHologram hologram = LegendBukkit.getInstance().getLeaderBoardHandler().getRotatingHologram();

        hologram.rotate();

    }

    @Subcommand("fillstats")
    @CommandPermission("op")
    public void fillstats(Player sender) {

        int left = 250;
        for (UUID uuid : UUIDCache.getUuidToNames().keySet()) {
            if (left <= 0) break;

            PlayerUtils.respawnOfflinePlayer(uuid);
            SamuraiUser user = LegendBukkit.getInstance().getUserHandler().loadUser(uuid);

            user.setBalance(ThreadLocalRandom.current().nextInt(0, 20_000));
            user.setKillStreak(ThreadLocalRandom.current().nextInt(0, 30));
            user.setStatistic("highest_killstreak", ThreadLocalRandom.current().nextInt(0, 50));
            user.setStatistic("kills", ThreadLocalRandom.current().nextInt(0, 50));
            user.setStatistic("deaths", ThreadLocalRandom.current().nextInt(0, 50));

            user.flag();
            left--;
        }
    }

}