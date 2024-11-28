package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.PagedItem;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.playtime.PlayTimeGoalHandler;
import dev.lbuddyboy.legend.features.playtime.menu.PlayTimeGoalMenu;
import dev.lbuddyboy.legend.features.playtime.model.PlayTimeGoal;
import dev.lbuddyboy.legend.util.CommandUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@CommandAlias("playtime|ptime|pt")
public class PlayTimeCommand extends BaseCommand {

    private final PlayTimeGoalHandler playTimeGoalHandler = LegendBukkit.getInstance().getPlayTimeGoalHandler();

    @Default
    public void def(Player sender, @Name("player") @Optional UUID target) {
        if (target == null) target = sender.getUniqueId();

        LegendBukkit.getInstance().getUserHandler().loadAsync(target).thenAcceptAsync(user -> {
            long playTime = user.getTotalPlayTime();

            sender.sendMessage(CC.translate(
                    LegendBukkit.getInstance().getLanguage().getString("playtime.check")
                            .replaceAll("%playtime%", TimeUtils.formatIntoDetailedString(playTime))
                            .replaceAll("%player%", user.getName())
            ));
        });
    }

    @Subcommand("goals")
    public void goals(Player sender) {
        new PlayTimeGoalMenu().openMenu(sender);
    }

    @HelpCommand
    @Subcommand("help")
    public static void defp(CommandSender sender, @Name("help") @Optional CommandHelp help) {
        CommandUtil.generateCommandHelp(help);
    }

    @Subcommand("admin goal create")
    @Description("Creates a new playtime goal")
    @CommandPermission("legend.command.playtime.admin")
    public void startGoal(CommandSender sender, @Name("id") @Single String id, @Name("duration") TimeDuration duration, @Name("goal") TimeDuration goal, @Name("reward") String reward) {
        PlayTimeGoal playTimeGoal = new PlayTimeGoal(id, reward, duration.transform(), goal.transform());

        if (this.playTimeGoalHandler.getPlayTimeGoals().containsKey(id.toLowerCase())) {
            sender.sendMessage(CC.translate("&cThat playtime goal already exists."));
            return;
        }

        this.playTimeGoalHandler.registerPlayTimeGoal(playTimeGoal);
        sender.sendMessage(" ");
        sender.sendMessage(CC.translate("&aSuccessfully created a playtime goal!"));
        sender.sendMessage(CC.translate("&fID: " + id));
        sender.sendMessage(CC.translate("&fReward: " + reward));
        sender.sendMessage(CC.translate("&fDuration: " + duration.fancy()));
        sender.sendMessage(CC.translate("&fGoal: " + goal.fancy()));
        sender.sendMessage(" ");
        sender.sendMessage(CC.translate("&7Add commands by doing /pt admin goal command add|remove|list <id> <command>"));
        sender.sendMessage(" ");

    }

    @Subcommand("admin goal delete")
    @CommandCompletion("@playTimeGoals")
    @Description("Deletes an existing playtime goal")
    @CommandPermission("legend.command.playtime.admin")
    public void deleteGoal(CommandSender sender, @Name("id") PlayTimeGoal playTimeGoal) {

        this.playTimeGoalHandler.deletePlayTimeGoal(playTimeGoal);
        sender.sendMessage(CC.translate("&aSuccessfully deleted the " + playTimeGoal.getId() + " playtime goal!"));
    }

    @Subcommand("admin goal setmaterial")
    @CommandCompletion("@playTimeGoals")
    @Description("Adds a command to a playtime goal")
    @CommandPermission("legend.command.playtime.admin")
    public void setmaterial(Player sender, @Name("id") PlayTimeGoal playTimeGoal) {
        if (sender.getItemInHand() == null) {
            sender.sendMessage(CC.translate("&cPlease have an item in your hand."));
            return;
        }

        playTimeGoal.setMaterialData(sender.getItemInHand().getData());
        sender.sendMessage(CC.translate("&aSuccessfully set the display material to the " + playTimeGoal.getId() + " playtime goal!"));
    }

    @Subcommand("admin goal command add")
    @CommandCompletion("@playTimeGoals")
    @Description("Adds a command to a playtime goal")
    @CommandPermission("legend.command.playtime.admin")
    public void commandAdd(CommandSender sender, @Name("id") PlayTimeGoal playTimeGoal, @Name("command") String command) {
        playTimeGoal.getCommands().add(command);
        sender.sendMessage(CC.translate("&aSuccessfully added a command to the " + playTimeGoal.getId() + " playtime goal!"));
    }

    @Subcommand("admin goal command remove")
    @CommandCompletion("@playTimeGoals @playTimeGoalCommands")
    @Description("Removes a command from a playtime goal")
    @CommandPermission("legend.command.playtime.admin")
    public void commandRemove(CommandSender sender, @Name("id") PlayTimeGoal playTimeGoal, @Name("command") String command) {
        playTimeGoal.getCommands().remove(command);
        sender.sendMessage(CC.translate("&aSuccessfully removed a command from the " + playTimeGoal.getId() + " playtime goal!"));
    }

    @Subcommand("admin goal command list")
    @CommandCompletion("@playTimeGoals")
    @Description("Lists commands of a playtime goal")
    @CommandPermission("legend.command.playtime.admin")
    public void commandList(CommandSender sender, @Name("id") PlayTimeGoal playTimeGoal) {
        sender.sendMessage(" ");
        sender.sendMessage(CC.translate("&2&l" + playTimeGoal.getId() + "'s Commands"));
        for (String command : playTimeGoal.getCommands()) {
            sender.sendMessage(CC.translate("&f- " + command));
        }
        sender.sendMessage(" ");
    }

    @Subcommand("admin goal info")
    @CommandCompletion("@playTimeGoals")
    @Description("Shows all info of a playtime goal")
    @CommandPermission("legend.command.playtime.admin")
    public void commandInfo(CommandSender sender, @Name("id") PlayTimeGoal playTimeGoal) {
        sender.sendMessage(" ");
        sender.sendMessage(CC.translate("&2&l" + playTimeGoal.getId() + "'s Info"));
        sender.sendMessage(CC.translate("&fID: " + playTimeGoal.getId()));
        sender.sendMessage(CC.translate("&fReward: " + playTimeGoal.getReward()));
        sender.sendMessage(CC.translate("&fDuration: " + TimeUtils.formatIntoDetailedString(playTimeGoal.getDuration())));
        sender.sendMessage(CC.translate("&fGoal: " + TimeUtils.formatIntoDetailedString(playTimeGoal.getPlayTimeGoal())));
        sender.sendMessage(CC.translate("&fTime Left: " + TimeUtils.formatIntoDetailedString(playTimeGoal.getRemaining())));
        sender.sendMessage(CC.translate("&fProgress: " + TimeUtils.formatIntoDetailedString(playTimeGoal.getProgress())));
        for (String command : playTimeGoal.getCommands()) {
            sender.sendMessage(CC.translate("&f- " + command));
        }
        sender.sendMessage(" ");
    }

}
