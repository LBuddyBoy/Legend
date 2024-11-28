package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.LocationUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.leaderboard.ILeaderBoardStat;
import dev.lbuddyboy.legend.features.leaderboard.model.LeaderBoardHologram;
import dev.lbuddyboy.legend.features.leaderboard.model.RotatingHologram;
import dev.lbuddyboy.legend.features.leaderboard.menu.LeaderBoardMenu;
import org.bukkit.entity.Player;

import java.util.Optional;

@CommandAlias("leaderboards|leaderboard|lb")
public class LeaderBoardCommand extends BaseCommand {

    @Default
    public void leaderboard(Player sender) {
        new LeaderBoardMenu().openMenu(sender);
    }

    @Subcommand("set")
    @CommandPermission("legend.command.leaderboard")
    @CommandCompletion("@leaderboard-types")
    public void set(Player sender, @Name("leaderboard") String type) {
        Optional<ILeaderBoardStat> statOpt = LegendBukkit.getInstance().getLeaderBoardHandler().getStatById(type);

        if (!statOpt.isPresent()) {
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
    @CommandPermission("legend.command.leaderboard")
    @CommandCompletion("@leaderboard-types")
    public void delete(Player sender, @Name("leaderboard") String type) {
        Optional<ILeaderBoardStat> statOpt = LegendBukkit.getInstance().getLeaderBoardHandler().getStatById(type);

        if (!statOpt.isPresent()) {
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
    @CommandPermission("legend.command.leaderboard")
    public void set(Player sender) {
        RotatingHologram hologram = LegendBukkit.getInstance().getLeaderBoardHandler().getRotatingHologram();

        hologram.getLeaderBoardHologram().setLocation(sender.getLocation());
        hologram.getLeaderBoardHologram().update();
        LegendBukkit.getInstance().getLeaderBoardHandler().getConfig().set("rotating-hologram", LocationUtils.serializeString(sender.getLocation()));
        LegendBukkit.getInstance().getLeaderBoardHandler().getConfig().save();
    }

    @Subcommand("delrotating")
    @CommandPermission("legend.command.leaderboard")
    public void delrotating(Player sender) {
        RotatingHologram hologram = LegendBukkit.getInstance().getLeaderBoardHandler().getRotatingHologram();

        hologram.getLeaderBoardHologram().despawn();
        LegendBukkit.getInstance().getLeaderBoardHandler().getConfig().set("rotating-hologram", "world;0;100;0;0;0;");
        LegendBukkit.getInstance().getLeaderBoardHandler().getConfig().save();
    }

    @Subcommand("rotate")
    @CommandPermission("legend.command.leaderboard")
    public void rotate(Player sender) {
        RotatingHologram hologram = LegendBukkit.getInstance().getLeaderBoardHandler().getRotatingHologram();

        hologram.rotate();
    }

}