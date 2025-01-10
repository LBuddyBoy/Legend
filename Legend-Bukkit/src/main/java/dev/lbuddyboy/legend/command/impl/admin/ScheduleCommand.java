package dev.lbuddyboy.legend.command.impl.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.api.user.model.Notification;
import dev.lbuddyboy.arrow.Arrow;
import dev.lbuddyboy.arrow.ArrowPlugin;
import dev.lbuddyboy.arrow.command.impl.CoinShopCommand;
import dev.lbuddyboy.arrow.command.impl.admin.NotificationCommand;
import dev.lbuddyboy.arrow.command.impl.admin.UserCommand;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.component.FancyBuilder;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.schedule.ScheduleEntry;
import dev.lbuddyboy.legend.features.schedule.ScheduleMenu;
import dev.lbuddyboy.legend.features.settings.Setting;
import dev.lbuddyboy.legend.timer.server.SOTWTimer;
import dev.lbuddyboy.legend.util.BukkitUtil;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@CommandAlias("schedule")
public class ScheduleCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        new ScheduleMenu().openMenu(sender);
    }

    @Subcommand("help")
    @CommandPermission("legend.command.schedule.admin")
    public void help(CommandSender sender) {
        sender.sendMessage(CC.blend("It is currently: " + APIConstants.SDF.format(APIConstants.getCalendar().getTime()), "&7", "&f"));
        Arrays.asList(
                "&fCurrent Day of Week&7: &e" + APIConstants.getCalendar().get(Calendar.DAY_OF_WEEK),
                "&fCurrent Hour&7: &e" + APIConstants.getCalendar().get(Calendar.HOUR_OF_DAY),
                "&fCurrent Minute&7: &e" + APIConstants.getCalendar().get(Calendar.MINUTE)
        ).forEach(s -> sender.sendMessage(CC.translate(s)));
    }

    @Subcommand("createraw")
    @CommandPermission("legend.command.schedule.admin")
    @Description("Creates a schedule entry with no prompt for a display name.")
    public void createPublic(CommandSender sender,
                             @Name("id") @Single String id,
                             @Name("dayOfTheWeek") int dayOfTheWeek,
                             @Name("hourOfTheDay") int hourOfTheDay,
                             @Name("minuteOfTheDay") int minuteOfTheDay,
                             @Name("broadcast") boolean broadcast,
                             @Name("adminOnly") boolean adminOnly,
                             @Name("removeAfterDone") boolean removeAfterExecute,
                             @Name("displayName") @Single String displayName,
                             @Name("command") String command
    ) {
        ScheduleEntry entry = LegendBukkit.getInstance().getScheduleHandler().createScheduleEntry(
                id,
                dayOfTheWeek,
                hourOfTheDay,
                minuteOfTheDay,
                command,
                displayName.replaceAll("-", " "),
                adminOnly,
                removeAfterExecute,
                broadcast
        );

        sender.sendMessage(CC.translate("<blend:&6;&e>[Schedule] A new schedule entry has been created.</>"));
        this.info(sender, entry);
    }

    @Subcommand("queue")
    @CommandPermission("legend.command.schedule.admin")
    @Description("Queues a schedule entry to execute after a set time.")
    public void queue(CommandSender sender,
                      @Name("id") @Single String id,
                      @Name("duration") TimeDuration duration,
                      @Name("broadcast") boolean broadcast,
                      @Name("command") String command
    ) {
        Calendar calendar = Calendar.getInstance(APIConstants.TIME_ZONE, APIConstants.LOCALE);
        Date date = new Date(System.currentTimeMillis() + duration.transform());

        calendar.setTime(date);

        this.create(sender, id, calendar.get(Calendar.DAY_OF_WEEK), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), broadcast, false, true, command);
    }

    @Subcommand("create public")
    @CommandPermission("legend.command.schedule.admin")
    @Description("Creates a schedule entry that is viewable by everyone.")
    public void createPublic(CommandSender sender,
                             @Name("id") @Single String id,
                             @Name("dayOfTheWeek") int dayOfTheWeek,
                             @Name("hourOfTheDay") int hourOfTheDay,
                             @Name("minuteOfTheDay") int minuteOfTheDay,
                             @Name("broadcast") boolean broadcast,
                             @Name("removeAfterDone") boolean removeAfterExecute,
                             @Name("command") String command
    ) {
        this.create(sender, id, dayOfTheWeek, hourOfTheDay, minuteOfTheDay, broadcast, false, removeAfterExecute, command);
    }

    @Subcommand("create private")
    @CommandPermission("legend.command.schedule.admin")
    @Description("Creates a schedule entry that is viewable by only people with this command.")
    public void createPrivate(CommandSender sender,
                              @Name("id") @Single String id,
                              @Name("dayOfTheWeek") int dayOfTheWeek,
                              @Name("hourOfTheDay") int hourOfTheDay,
                              @Name("minuteOfTheDay") int minuteOfTheDay,
                              @Name("broadcast") boolean broadcast,
                              @Name("removeAfterDone") boolean removeAfterExecute,
                              @Name("command") String command
    ) {
        this.create(sender, id, dayOfTheWeek, hourOfTheDay, minuteOfTheDay, broadcast, true, removeAfterExecute, command);
    }

    @Subcommand("createms")
    @Private
    @CommandPermission("legend.command.schedule.admin")
    public void createMineSurge(CommandSender sender) {
        this.createPublic(sender, "two-ability-barrel-all", 7, 15, 15, true, false, true, "<blend:&3;&b>&l2X ABILITY BARREL ALL</>", "abilityitem giveboxall 2");
        this.createPublic(sender, "two-legend-key-all", 7, 15, 30, true, false, true, "<blend:&5;&d>&l2X LEGEND KEY ALL</>", "crate giveall Legend 2");
        this.createPublic(sender, "random-coins", 7, 15, 45, true, false, true, "<blend:&6;&e>&l1,000 COINS GIVEAWAY</>", "schedule giverandomcoins 1000");
        this.createPublic(sender, "random-minigame", 7, 16, 0, true, false, true, "<blend:&4;&c>&lRANDOM MINIGAME FOR 7D LEGEND RANK</>", "minigame startrandom");
        this.createPublic(sender, "two-legend-key-all", 7, 16, 30, true, false, true, "<blend:&5;&c>&l3X AMETHYST KEY ALL</>", "crate giveall Amethyst 3");
        this.createPublic(sender, "random-koth", 7, 17, 15, true, false, true, "<blend:&9;&b>&lRANDOM KOTH</>", "koth start Winter");

        this.createPublic(sender, "citadel", 0, 12, 0, true, false, true, "<blend:&5;&d>&lCITADEL</>", "citadel start Citadel");

        for (int day = 0; day <= 7; day++) {
            if (day == 7) continue;

            for (int hour = 0; hour <= 23; hour++) {
                if (hour == 12 && day == 0) continue;

                if (hour % 5 == 0) {
                    this.createPublic(sender, day + "-" + hour + "-two-amethyst-key-all", day, hour, 0, true, false, true, "<blend:&5;&c>&l2X AMETHYST KEY ALL</>", "crate giveall Amethyst 2");
                } else if (hour % 3 == 0) {
                    this.createPublic(sender, day + "-" + hour + "-one-legend-key-all", day, hour, 0, true, false, true, "<blend:&5;&d>&l1X LEGEND KEY ALL</>", "crate giveall Legend 1");
                }
            }
        }

    }

    @Subcommand("giverandomcoins")
    @Private
    @CommandPermission("legend.command.schedule.admin")
    public void giverandomcoins(CommandSender sender, @Name("amount") long amount) {
        List<Player> players = BukkitUtil.getOnlinePlayers();
        Player player = players.get(ThreadLocalRandom.current().nextInt(players.size()));

        Bukkit.dispatchCommand(sender, "coinshop coins add " + player.getName() + " " + amount);
        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage(CC.translate("&a" + player.getName() + "&f has won the &e" + APIConstants.formatNumber(amount) + " coins&f!"));
        Bukkit.broadcastMessage(" ");
    }

    public void create(CommandSender sender,
                       @Name("id") @Single String id,
                       @Name("dayOfTheWeek") int dayOfTheWeek,
                       @Name("hourOfTheDay") int hourOfTheDay,
                       @Name("minuteOfTheDay") int minuteOfTheDay,
                       @Name("broadcast") boolean broadcast,
                       @Name("adminViewOnly") boolean adminOnly,
                       @Name("removeAfterDone") boolean removeAfterExecute,
                       @Name("command") String command
    ) {
        if (LegendBukkit.getInstance().getScheduleHandler().getScheduleEntry(id) != null) {
            sender.sendMessage(CC.translate("<blend:&4;&c>[Schedule Error] A schedule entry with that id already exists.</>"));
            return;
        }
        if (!(sender instanceof Conversable conversable)) {
            sender.sendMessage(CC.translate("&cAn error occurred, contact LBuddyBoy!"));
            return;
        }

        Conversation conversation = new Conversation(ArrowPlugin.getInstance(), conversable, new CreatePrompt(id, command, dayOfTheWeek, hourOfTheDay, minuteOfTheDay, adminOnly, removeAfterExecute, broadcast));
        conversation.setLocalEchoEnabled(false);
        conversation.begin();
    }

    @Subcommand("delete")
    @CommandCompletion("@scheduleEntries")
    @CommandPermission("legend.command.schedule.admin")
    @Description("Deletes a schedule entry.")
    public void delete(CommandSender sender, @Name("entry") ScheduleEntry entry) {
        LegendBukkit.getInstance().getScheduleHandler().getScheduleEntries().remove(entry);
        LegendBukkit.getInstance().getScheduleHandler().saveSchedule();
        sender.sendMessage(CC.translate("<blend:&6;&e>[Schedule] The '" + entry.getId() + "' schedule entry has been deleted.</>"));
    }

    @Subcommand("list")
    @CommandPermission("legend.command.schedule.admin")
    @Description("Shows a list of all schedule entries.")
    public void list(CommandSender sender) {
        sender.sendMessage(" ");
        sender.sendMessage(CC.translate("<blend:&6;&e>&lSchedule Entries</>"));
        for (ScheduleEntry entry : LegendBukkit.getInstance().getScheduleHandler().getSortedSchedule()) {
            FancyBuilder builder = new FancyBuilder("&7- " + entry.getId() + " (" + entry.getDisplayName() + "&7) &7[Hover]");

            builder.hover(
                    "&fFinishes on&7: &e" + APIConstants.SDF.format(entry.getDate()) + " &7(" + TimeUtils.formatIntoDetailedString(entry.getTimeLeft()) + ")",
                    "&fCommand&7: &e" + entry.getCommand(),
                    "&fAdmin Only&7: " + (entry.isAdminOnly() ? "&aYes" : "&cNo"),
                    "&fRemove After Execute&7: " + (entry.isRemoveAfterExecute() ? "&aYes" : "&cNo")
            );

            builder.send(sender);
        }
        sender.sendMessage(" ");
    }


    @Subcommand("clear")
    @CommandPermission("legend.command.schedule.admin")
    public void clear(CommandSender sender) {
        LegendBukkit.getInstance().getScheduleHandler().getScheduleEntries().clear();
        LegendBukkit.getInstance().getScheduleHandler().saveSchedule();
        sender.sendMessage(CC.translate("<blend:&6;&e>[Schedule] Cleared the server schedule.</>"));
    }

    @Subcommand("fillrandom")
    @CommandPermission("legend.command.schedule.admin")
    @Description("Fills the schedule with random entries for developer purposes.")
    public void fillrandom(CommandSender sender) {
        if (sender instanceof Player) {
            sender.sendMessage(CC.translate("&cOnly console can run this command."));
            return;
        }

        int index = LegendBukkit.getInstance().getScheduleHandler().getScheduleEntries().size() + 1;

        for (int dayOfTheWeek = 1; dayOfTheWeek <= 7; dayOfTheWeek++) {
            for (int i = 0; i < 11; i++) {
                index++;
                ScheduleEntry entry = new ScheduleEntry(
                        dayOfTheWeek,
                        ThreadLocalRandom.current().nextInt(0, 24),
                        ThreadLocalRandom.current().nextInt(1, 60),
                        "fillrandomentry" + (index),
                        "crate giveall Kill 1",
                        "<blend:&6;&e>&lRandom Filler Entry #" + index + "</>",
                        false,
                        true,
                        true,
                        true
                );

                LegendBukkit.getInstance().getScheduleHandler().getScheduleEntries().add(entry);
            }
        }

        LegendBukkit.getInstance().getScheduleHandler().saveSchedule();
        sender.sendMessage(CC.translate("&aSuccessfully filled the schedule."));
    }

    @Subcommand("info")
    @CommandCompletion("@scheduleEntries")
    @CommandPermission("legend.command.schedule.admin")
    @Description("Shows the info of a specific schedule entry.")
    public void info(CommandSender sender, @Name("entry") ScheduleEntry entry) {
        sender.sendMessage(CC.translate(" "));
        sender.sendMessage(CC.translate(entry.getDisplayName() + "&f's Info"));
        sender.sendMessage(CC.translate("&fID&7: &e" + entry.getId()));
        sender.sendMessage(CC.translate("&fFinishes on&7: &e" + APIConstants.SDF.format(entry.getDate()) + " &7(" + TimeUtils.formatIntoDetailedString(entry.getTimeLeft()) + ")"));
        sender.sendMessage(CC.translate("&fCommand&7: &e" + entry.getCommand()));
        sender.sendMessage(CC.translate("&fAdmin Only&7: " + (entry.isAdminOnly() ? "&aYes" : "&cNo")));
        sender.sendMessage(" ");
    }

    @AllArgsConstructor
    public class CreatePrompt extends StringPrompt {

        private String id, command;
        private int dayOfTheWeek, hourOfTheDay, minuteOfTheDay;
        private boolean adminOnly, removeAfterExecute, broadcast;

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return CC.translate("&aPlease provide the display name you'd like for this schedule entry! Type 'cancel' to stop this process.");
        }

        @Nullable
        @Override
        public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
            Conversable conversable = context.getForWhom();

            if (input != null && !input.equalsIgnoreCase("cancel")) {
                ScheduleEntry entry = LegendBukkit.getInstance().getScheduleHandler().createScheduleEntry(
                        this.id,
                        this.dayOfTheWeek,
                        this.hourOfTheDay,
                        this.minuteOfTheDay,
                        this.command,
                        input,
                        this.adminOnly,
                        this.removeAfterExecute,
                        this.broadcast
                );

                conversable.sendRawMessage(CC.translate("<blend:&6;&e>[Schedule] A new schedule entry has been created.</>"));
                conversable.sendRawMessage(CC.translate(" "));
                conversable.sendRawMessage(CC.translate(entry.getDisplayName() + "&f's Info"));
                conversable.sendRawMessage(CC.translate("&fID&7: &e" + entry.getId()));
                conversable.sendRawMessage(CC.translate("&fFinishes on&7: &e" + APIConstants.SDF.format(entry.getDate()) + " &7(" + TimeUtils.formatIntoDetailedString(entry.getDate().getTime() - System.currentTimeMillis()) + ")"));
                conversable.sendRawMessage(CC.translate("&fCommand&7: &e" + entry.getCommand()));
                conversable.sendRawMessage(CC.translate("&fAdmin Only&7: " + (entry.isAdminOnly() ? "&aYes" : "&cNo")));
                conversable.sendRawMessage(" ");
            } else {
                conversable.sendRawMessage(CC.translate("&cProcess cancelled!"));
            }

            return END_OF_CONVERSATION;
        }
    }

}
