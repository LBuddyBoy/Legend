package dev.lbuddyboy.legend.command.impl.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

@CommandAlias("eotw|endoftheworld")
public class EOTWCommand extends BaseCommand {

    @Subcommand("start")
    @CommandPermission("legend.command.eotw")
    public void start(CommandSender sender) {
        Arrays.asList(
                " ",
                "&7███████",
                "&7█&4█████&7█ <blend:&4;&c>&lEOTW</>",
                "&7█&4█&7█████ ",
                "&7█&4████&7██ &cEOTW has commenced!",
                "&7█&4█&7█████ &cAll teams have been",
                "&7█&4█████&7█ &cset raidable.",
                "&7███████",
                " "
        ).forEach(s -> Bukkit.broadcastMessage(CC.translate(s)));

        for (Team team : LegendBukkit.getInstance().getTeamHandler().getTeams()) {
            team.setDeathsUntilRaidable(-1.0);
            team.applyDTRFreeze(new TimeDuration("1h").transform());
        }
    }

    @Subcommand("startffa")
    @CommandPermission("legend.command.eotw")
    public void startffa(CommandSender sender) {
        Arrays.asList(
                " ",
                "&7███████",
                "&7█&e█████&7█",
                "&7█&e█&7█████ <blend:&6;&e>&lEOTW FFA</>",
                "&7█&e███&7███ &eFFA has commenced!",
                "&7█&e█&7█████ &eFFA will start in 5 minutes",
                "&7█&e█&7█████",
                "&7███████",
                " "
        ).forEach(s -> Bukkit.broadcastMessage(CC.translate(s)));

        for (Team team : LegendBukkit.getInstance().getTeamHandler().getTeams()) {
            LegendBukkit.getInstance().getTeamHandler().disbandTeam(team, true);
        }
    }

    @Subcommand("invis")
    @CommandPermission("legend.command.eotw")
    public void invis(CommandSender sender) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 1));
        }
    }

    @Subcommand("teleport")
    @CommandPermission("legend.command.eotw")
    public void teleport(Player sender) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.equals(sender)) continue;

            player.teleport(sender);
        }
    }

}
