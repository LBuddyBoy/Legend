package dev.lbuddyboy.legend.command.impl.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.api.ArrowAPI;
import dev.lbuddyboy.api.leaderboard.model.LeaderboardDataEntry;
import dev.lbuddyboy.api.user.model.Notification;
import dev.lbuddyboy.arrow.Arrow;
import dev.lbuddyboy.arrow.ArrowPlugin;
import dev.lbuddyboy.arrow.command.impl.CoinShopCommand;
import dev.lbuddyboy.arrow.command.impl.admin.NotificationCommand;
import dev.lbuddyboy.arrow.packet.UserCoinsUpdatePacket;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.leaderboard.ILeaderBoardStat;
import dev.lbuddyboy.legend.features.settings.Setting;
import dev.lbuddyboy.legend.listener.PacketListener;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamMember;
import dev.lbuddyboy.legend.team.model.TopEntry;
import dev.lbuddyboy.legend.team.model.TopType;
import dev.lbuddyboy.legend.team.model.claim.Claim;
import dev.lbuddyboy.legend.timer.server.SOTWTimer;
import dev.lbuddyboy.legend.user.model.LegendUser;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CommandAlias("legend|core")
public class LegendCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        sender.sendMessage(CC.translate("&5&lLegend &7[v1.0.0] &eby LBuddyBoy"));
    }

    @Subcommand("rewardtop")
    @CommandPermission("legend.command.core")
    public void rewardTop(CommandSender sender) {
        if (sender instanceof Player player) {
            player.sendMessage(CC.translate("&cConsole only."));
            return;
        }

        Map<Integer, TopEntry> teams = LegendBukkit.getInstance().getTeamHandler().getTopTeams().get(TopType.POINTS);

        LegendBukkit.getInstance().getTeamHandler().getTeamById(teams.get(1).getTeamUUID()).ifPresent(team -> {
            int amount = 3000;
            List<TeamMember> members = team.getMembers();
            int amountPer = amount / members.size();

            for (TeamMember member : members) {
                Notification notification = new Notification(
                        null,
                        "teamtop-" + UUID.randomUUID(),
                        "<blend:&6;&e>&lTeam Top Reward</>",
                        "&fYou were rewarded &e" + amountPer + "⛁ coins&f for being the &6#1 Team&f on team top! Use these coins by doing &e/coinshop",
                        new TimeDuration("24h").transform(),
                        false
                );

                new UserCoinsUpdatePacket(member.getUuid(), Arrow.getInstance().getUserHandler().getOrCreateUser(member.getUuid()).getCoins() + amountPer).send();
                ArrowAPI.getInstance().getUserHandler().sendNotification(member.getUuid(), notification);
            }
        });

        LegendBukkit.getInstance().getTeamHandler().getTeamById(teams.get(2).getTeamUUID()).ifPresent(team -> {
            int amount = 2000;
            List<TeamMember> members = team.getMembers();
            int amountPer = amount / members.size();

            for (TeamMember member : members) {
                Notification notification = new Notification(
                        null,
                        "teamtop-" + UUID.randomUUID(),
                        "<blend:&6;&e>&lTeam Top Reward</>",
                        "&fYou were rewarded &e" + amountPer + "⛁ coins&f for being the &e#2 Team&f on team top! Use these coins by doing &e/coinshop",
                        new TimeDuration("24h").transform(),
                        false
                );

                new UserCoinsUpdatePacket(member.getUuid(), Arrow.getInstance().getUserHandler().getOrCreateUser(member.getUuid()).getCoins() + amountPer).send();
                ArrowAPI.getInstance().getUserHandler().sendNotification(member.getUuid(), notification);
            }
        });

        LegendBukkit.getInstance().getTeamHandler().getTeamById(teams.get(3).getTeamUUID()).ifPresent(team -> {
            int amount = 1000;
            List<TeamMember> members = team.getMembers();
            int amountPer = amount / members.size();

            for (TeamMember member : members) {
                Notification notification = new Notification(
                        null,
                        "teamtop-" + UUID.randomUUID(),
                        "<blend:&6;&e>&lTeam Top Reward</>",
                        "&fYou were rewarded &e" + amountPer + "⛁ coins&f for being the &b#3 Team&f on team top! Use these coins by doing &e/coinshop",
                        new TimeDuration("24h").transform(),
                        false
                );

                new UserCoinsUpdatePacket(member.getUuid(), Arrow.getInstance().getUserHandler().getOrCreateUser(member.getUuid()).getCoins() + amountPer).send();
                ArrowAPI.getInstance().getUserHandler().sendNotification(member.getUuid(), notification);
            }
        });

    }

    @Subcommand("reset")
    @CommandPermission("legend.command.core")
    public void reset(CommandSender sender) {
        if (sender instanceof Player player) {
            player.sendMessage(CC.translate("&cConsole only."));
            return;
        }

        for (ILeaderBoardStat stat : LegendBukkit.getInstance().getLeaderBoardHandler().getLeaderBoardStats()) {
            LeaderboardDataEntry leaderboardDataEntry = LegendBukkit.getInstance().getDataEntry(stat.getId());

            leaderboardDataEntry.wipeLeaderboards(true);
        }

        for (Team team : LegendBukkit.getInstance().getTeamHandler().getTeams()) {
            for (Claim claim : team.getClaims()) {
                LegendBukkit.getInstance().getTeamHandler().getClaimHandler().removeClaim(claim);
            }
            team.getClaims().clear();
            team.getBrewData().getBrewingMaterials().clear();
            team.getBrewData().getBrewedPotions().clear();
            team.getBrewData().getStartedBrewing().clear();
            team.setDtrRegen(0L);
            team.setHome(null);
            team.setDeathsUntilRaidable(team.getMaxDTR());
        }

        LegendBukkit.getInstance().getUserHandler().getUserCache().invalidateAll();
        LegendBukkit.getInstance().getUserHandler().getUserCache().cleanUp();
        LegendBukkit.getInstance().getUserHandler().getCollection().drop();

        sender.sendMessage(CC.translate("&aReset users & leaderboards."));
    }

    @Subcommand("reload")
    @CommandPermission("legend.command.core")
    public void reload(CommandSender sender) {
        LegendBukkit.getInstance().getModules().forEach(IModule::reload);
        LegendBukkit.getInstance().loadConfigs();
        sender.sendMessage(CC.translate("&aLegend reloaded all configuration files successfully!"));
    }

    @Subcommand("packetlog")
    @CommandPermission("legend.command.core")
    public void packetlog(CommandSender sender) {
        PacketListener.LOG_PACKETS = !PacketListener.LOG_PACKETS;
    }

    @Subcommand("exportteams")
    @CommandPermission("legend.command.core")
    public void exportTeams(CommandSender sender, @Name("output") String outputFileName) {
        MongoCollection<Document> collection = LegendBukkit.getInstance().getTeamHandler().getCollection();
        JsonArray jsonArray = new JsonArray();

        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                jsonArray.add(JsonParser.parseString(cursor.next().toJson()).getAsJsonObject());
            }
        }

        try (FileWriter file = new FileWriter(outputFileName)) {
            file.write(APIConstants.GSON.toJson(jsonArray));
            file.flush();
            sender.sendMessage(CC.translate("&a" + jsonArray.size() + " teams exported successfully."));
        } catch (IOException e) {
            e.printStackTrace();
            sender.sendMessage(CC.translate("&cAn error occurred while exporting teams: " + e.getMessage()));
        }
    }

    @Subcommand("importteams")
    @CommandPermission("legend.command.core")
    public void importTeams(CommandSender sender, @Name("input") String inputFileName) {
        MongoCollection<Document> collection = LegendBukkit.getInstance().getTeamHandler().getCollection();

        collection.drop();
        LegendBukkit.getInstance().getTeamHandler().getTeamNames().clear();
        LegendBukkit.getInstance().getTeamHandler().getPlayerTeams().clear();
        LegendBukkit.getInstance().getTeamHandler().getTeamIds().clear();

        try (FileReader reader = new FileReader(inputFileName)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            if (!jsonElement.isJsonArray()) {
                sender.sendMessage(CC.translate("&cError: The provided file does not contain a valid JSON array."));
                return;
            }

            JsonArray jsonArray = jsonElement.getAsJsonArray();
            int imported = 0;
            for (JsonElement element : jsonArray) {
                if (!element.isJsonObject()) {
                    sender.sendMessage(CC.translate("&cError: Invalid JSON object found in the array."));
                    continue;
                }

                Document document = Document.parse(element.toString());
                Team team = new Team(document);

                collection.insertOne(document);
                LegendBukkit.getInstance().getTeamHandler().cacheTeam(team);
                imported++;
            }

            sender.sendMessage(CC.translate("&a" + imported + " teams imported successfully."));
        } catch (IOException e) {
            e.printStackTrace();
            sender.sendMessage(CC.translate("&cAn error occurred while importing teams: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(CC.translate("&cA JSON parsing error occurred: " + e.getMessage()));
        }
    }

    private SparkTask sparkTask;
    private AutoMessagerTask autoMessagerTask;

    @Subcommand("automessager start")
    @CommandPermission("legend.command.core")
    public void automessagerStart(CommandSender sender) {
        if (sender instanceof Player player) {
            player.sendMessage(CC.translate("&cConsole only."));
            return;
        }

        if (autoMessagerTask != null) {
            sender.sendMessage(CC.translate("&cThere's already a auto message task active."));
            return;
        }

        autoMessagerTask = new AutoMessagerTask();
        autoMessagerTask.runTaskTimerAsynchronously(LegendBukkit.getInstance(), 20, 20 * 60 * 5);
    }

    @Subcommand("automessager stop")
    @CommandPermission("legend.command.core")
    public void automessagerStop(CommandSender sender) {
        if (sender instanceof Player player) {
            player.sendMessage(CC.translate("&cConsole only."));
            return;
        }

        if (autoMessagerTask == null) {
            sender.sendMessage(CC.translate("&cThere's no auto message task active."));
            return;
        }

        this.autoMessagerTask.cancel();
        this.autoMessagerTask = null;
    }

    @Subcommand("sparkreports start")
    @CommandPermission("legend.command.core")
    public void sparkReports(CommandSender sender, @Name("secondsBetweenReports") int seconds, @Name("onlyTicksOver") boolean onlyTicksOver) {
        if (sender instanceof Player player) {
            player.sendMessage(CC.translate("&cConsole only."));
            return;
        }

        if (sparkTask != null) {
            sender.sendMessage(CC.translate("&cThere's already a spark report task active."));
            return;
        }

        sparkTask = new SparkTask(seconds, onlyTicksOver);
        sparkTask.runTaskTimerAsynchronously(LegendBukkit.getInstance(), 20, 20);
    }

    @Subcommand("sparkreports stop")
    @CommandPermission("legend.command.core")
    public void sparkReportsStop(CommandSender sender) {
        if (sender instanceof Player player) {
            player.sendMessage(CC.translate("&cConsole only."));
            return;
        }

        if (sparkTask == null) {
            sender.sendMessage(CC.translate("&cThere's no spark report task active."));
            return;
        }

        this.sparkTask.cancel();
        this.sparkTask = null;
    }

    @Subcommand("fixteams")
    @CommandPermission("legend.command.core")
    public void fixTeams(CommandSender sender) {
        if (sender instanceof Player player) {
            player.sendMessage(CC.translate("&cConsole only."));
            return;
        }

        for (Document document : LegendBukkit.getInstance().getTeamHandler().getCollection().find()) {
            document.remove("deathsUntilRaidable");
            document.remove("balance");
            LegendBukkit.getInstance().getTeamHandler().getCollection().replaceOne(Filters.eq("id", document.getString("id")), document, new ReplaceOptions().upsert(true));
        }

        FindIterable<Document> documents = LegendBukkit.getInstance().getTeamHandler().getCollection().find();
        int success = 0, failed = 0;

        for (Document document : documents) {
            try {
                LegendBukkit.getInstance().getTeamHandler().cacheTeam(new Team(document));
                success++;
            } catch (Exception e) {
                e.printStackTrace();
                failed++;
            }
        }

        LegendBukkit.getInstance().getLogger().info("Loaded " + success + " teams successfully, " + failed + " failures.");

    }

    @Subcommand("fixptimes")
    @CommandPermission("legend.command.core")
    public void fixptimes(CommandSender sender) {
        if (sender instanceof Player player) {
            player.sendMessage(CC.translate("&cConsole only."));
            return;
        }
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

            user.setPlayTime((player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 60) * 1000L);
            user.save(true);
        }
    }

    @RequiredArgsConstructor
    public class SparkTask extends BukkitRunnable {

        private final int secondsBetweenReports;
        private final boolean onlyTicksOver;
        private long lastSent;

        @Override
        public void run() {
            if (lastSent + (this.secondsBetweenReports * 1000L) > System.currentTimeMillis()) return;

            this.dispatchCommand();
            this.lastSent = System.currentTimeMillis();
        }

        public void dispatchCommand() {
            Tasks.run(() -> {
                if (this.onlyTicksOver) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spark profiler stop --comment Auto Spark Profiler (onlyTicksOver) - Legend");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spark profiler --timeout 600 --only-ticks-over 100");
                    return;
                }

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spark profiler stop --comment Auto Spark Profiler (Regular) - Legend");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spark profiler start");
            });
        }

    }

    public class AutoMessagerTask extends BukkitRunnable {

        private int index = 0;
        private final List<String> messages = Arrays.asList(
                "<blend:&4;&c>&lOWNER</> <blend:&4;&c>LBuddyBoy</>&f: &cCurious when an event is gonna occur? Check out our server scheduler in /schedule",
                "<blend:&4;&c>&lOWNER</> <blend:&4;&c>LBuddyBoy</>&f: &cClaim a free rank by liking play.minesurge.org on NameMC! /freerank",
                "<blend:&4;&c>&lOWNER</> <blend:&4;&c>LBuddyBoy</>&f: &cThere is currently a 25% Sale active on our store to celebrate the New Year. Purchase coins & use them in the /coinshop!",
                "<blend:&4;&c>&lOWNER</> <blend:&4;&c>hahasike</>&f: &cCurious when an event is gonna occur? Check out our server scheduler in /schedule",
                "<blend:&4;&c>&lOWNER</> <blend:&4;&c>hahasike</>&f: &cClaim a free rank by liking play.minesurge.org on NameMC! /freerank",
                "<blend:&4;&c>&lOWNER</> <blend:&4;&c>hahasike</>&f: &cThere is currently a 25% Sale active on our store to celebrate the New Year. Purchase coins & use them in the /coinshop!"
        );
        private int timesExecuted = 0;

        @Override
        public void run() {
            if (!LegendBukkit.getInstance().getTimerHandler().getServerTimer(SOTWTimer.class).isActive()) {
                cancel();
                return;
            }

            if (timesExecuted >= 12) {
                for (int i = 0; i < 3; i++) {
                    Bukkit.broadcastMessage(CC.translate(
                            "<blend:&4;&c>&lOWNER</> <blend:&4;&c>LBuddyBoy</>&f: &cNext 3 people to purchase coins will get them doubled!"
                    ));
                }

                return;
            }

            if (index >= messages.size()) {
                index = 0;
            }

            for (int i = 0; i < 3; i++) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!Setting.AUTOMATED_SOTW_MESSAGES.isToggled(player)) continue;

                    player.sendMessage(CC.translate(messages.get(index)));
                }
            }

            index++;
            timesExecuted++;
        }

    }

}
