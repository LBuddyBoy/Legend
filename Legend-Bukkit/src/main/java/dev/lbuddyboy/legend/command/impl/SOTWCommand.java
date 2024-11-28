package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.timer.server.SOTWTimer;
import dev.lbuddyboy.legend.util.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("sotw|startoftheworld")
public class SOTWCommand extends BaseCommand {

    @Default
    @HelpCommand
    @Subcommand("help")
    public static void defp(CommandSender sender, @Name("help") @Optional CommandHelp help) {
        CommandUtil.generateCommandHelp(help);
    }

    @Subcommand("enable")
    public void enable(Player sender) {
        SOTWTimer timer = LegendBukkit.getInstance().getTimerHandler().getServerTimer(SOTWTimer.class);

        if (!timer.isActive()) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("sotw.inactive")));
            return;
        }

        if (timer.isEnabled(sender)) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("sotw.already-enabled")));
            return;
        }

        timer.getEnabledPlayers().add(sender.getUniqueId());
        sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("sotw.enabled")));
    }

    @Subcommand("start")
    @CommandPermission("legend.command.sotw")
    public void start(CommandSender sender, @Name("duration") TimeDuration duration) {
        SOTWTimer timer = LegendBukkit.getInstance().getTimerHandler().getServerTimer(SOTWTimer.class);

        timer.start(duration.transform());
        LegendBukkit.getInstance().getLanguage().getStringList("sotw.started").forEach(s -> Bukkit.broadcastMessage(CC.translate(s)));
    }

    @Subcommand("extend")
    @CommandPermission("legend.command.sotw")
    public void extend(CommandSender sender, @Name("duration") TimeDuration duration) {
        SOTWTimer timer = LegendBukkit.getInstance().getTimerHandler().getServerTimer(SOTWTimer.class);

        if (!timer.isActive()) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("sotw.inactive")));
            return;
        }

        timer.getTimer().setDuration(timer.getTimer().getDuration() + duration.transform());
        LegendBukkit.getInstance().getLanguage().getStringList("sotw.extended").forEach(s -> Bukkit.broadcastMessage(CC.translate(s
                .replaceAll("%duration%", duration.fancy())
        )));
    }

    @Subcommand("resume")
    @CommandPermission("legend.command.sotw")
    public void resume(CommandSender sender) {
        SOTWTimer timer = LegendBukkit.getInstance().getTimerHandler().getServerTimer(SOTWTimer.class);

        if (!timer.isActive()) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("sotw.inactive")));
            return;
        }

        timer.unpause();
        LegendBukkit.getInstance().getLanguage().getStringList("sotw.resumed").forEach(s -> Bukkit.broadcastMessage(CC.translate(s)));
    }

    @Subcommand("pause")
    @CommandPermission("legend.command.sotw")
    public void pause(CommandSender sender) {
        SOTWTimer timer = LegendBukkit.getInstance().getTimerHandler().getServerTimer(SOTWTimer.class);

        if (!timer.isActive()) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("sotw.inactive")));
            return;
        }

        timer.pause();
        LegendBukkit.getInstance().getLanguage().getStringList("sotw.paused").forEach(s -> Bukkit.broadcastMessage(CC.translate(s)));
    }

    @Subcommand("end")
    @CommandPermission("legend.command.sotw")
    public void end(CommandSender sender) {
        SOTWTimer timer = LegendBukkit.getInstance().getTimerHandler().getServerTimer(SOTWTimer.class);

        if (!timer.isActive()) {
            sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("sotw.inactive")));
            return;
        }

        timer.end();
        LegendBukkit.getInstance().getLanguage().getStringList("sotw.ended").forEach(s -> Bukkit.broadcastMessage(CC.translate(s)));
    }

}
