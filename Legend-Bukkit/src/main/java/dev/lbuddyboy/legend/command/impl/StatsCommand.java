package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.annotation.Optional;
import dev.lbuddyboy.api.user.model.User;
import dev.lbuddyboy.api.user.model.UserMetadata;
import dev.lbuddyboy.arrow.Arrow;
import dev.lbuddyboy.arrow.ArrowLocale;
import dev.lbuddyboy.arrow.command.context.UUIDContext;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.user.model.LegendUser;
import dev.lbuddyboy.legend.util.UUIDUtils;
import dev.lbuddyboy.rollback.Rollback;
import dev.lbuddyboy.rollback.rollback.PlayerDeath;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@CommandAlias("stats")
public class StatsCommand extends BaseCommand {

	@Default
	@CommandCompletion("@players")
	public static void stats(Player sender, @Name("player") @Optional UUID uuid) {
		if (uuid == null) uuid = sender.getUniqueId();
		LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(uuid);

		LegendBukkit.getInstance().getLanguage().getStringList("leaderboards.stats").forEach(s -> {
			sender.sendMessage(CC.translate(user.applyPlaceholders(s)));
		});
	}

	@Subcommand("admin realkills")
	@CommandPermission("legend.command.stats.update")
	@CommandCompletion("@players")
	public void realkills(CommandSender sender, @Name("player") UUID player) {
		Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(player).orElse(null);

		if (team == null) {
			return;
		}

		sender.sendMessage(CC.translate("&aLoading " + UUIDUtils.name(player) + "'s kill report..."));

		Rollback.getInstance().getRollbackHandler().getKills(player).whenCompleteAsync((kills, throwable) -> {
			if (throwable != null) {
				throwable.printStackTrace();
				return;
			}

			int totalKills = kills.size();
			Map<UUID, Integer> dupesFound = new HashMap<>();
			Map<UUID, Integer> historicalMembersFound = new HashMap<>();

			for (int index = 0; index < kills.size(); index++) {
				PlayerDeath currentDeath = kills.get(index);
				int countedDupes = 1;

				if (team.getHistoricalMembers().stream().anyMatch(historicalMember -> historicalMember.getPlayerUUID().equals(currentDeath.getVictim()))) {
					historicalMembersFound.put(currentDeath.getVictim(), historicalMembersFound.getOrDefault(currentDeath.getVictim(), 0) + 1);
				}

				for (int nextIndex = index; nextIndex <= kills.size(); nextIndex++) {
					PlayerDeath nextDeath = kills.get(nextIndex);

					if (!nextDeath.getVictim().equals(currentDeath.getVictim())) break;

					countedDupes++;
					index++;
				}

				if (countedDupes > 2) {
					totalKills -= countedDupes;
				}

				if (countedDupes == 1) continue;

				dupesFound.put(currentDeath.getVictim(), dupesFound.getOrDefault(currentDeath.getVictim(), 0) + countedDupes);
			}

			int historicalMemberSum = historicalMembersFound.values()
					.stream()
					.mapToInt(Integer::intValue)
					.sum();

			sender.sendMessage(" ");
			sender.sendMessage(CC.translate("&d&l" + UUIDUtils.name(player) + "'s Kill Report"));
			sender.sendMessage(CC.translate("&fTotal Kills&7: &e" + kills.size()));
			sender.sendMessage(CC.translate("&fReal Kills&7: &e" + totalKills));
			sender.sendMessage(CC.translate("&fHistorical Member Kills&7: &e" + historicalMemberSum));
			sender.sendMessage(" ");
			sender.sendMessage(CC.translate("&d&lHistorical Member Summary"));
			if (historicalMembersFound.isEmpty()) {
				sender.sendMessage(CC.translate("&cNone"));
			} else {
				historicalMembersFound.forEach((uuid, value) -> sender.sendMessage(CC.translate("&e" + UUIDUtils.name(uuid) + "&7: &c" + value)));
			}
			sender.sendMessage(" ");
			sender.sendMessage(CC.translate("&d&lDupe Kill Summary"));
			if (dupesFound.isEmpty()) {
				sender.sendMessage(CC.translate("&cNone"));
			} else {
				dupesFound.forEach((uuid, value) -> sender.sendMessage(CC.translate("&e" + UUIDUtils.name(uuid) + "&7: &c" + value)));
			}
			sender.sendMessage(" ");

		});

	}

	@Subcommand("admin setkills")
	@CommandPermission("legend.command.stats.update")
	@CommandCompletion("@players")
	public void setKills(CommandSender sender, @Name("player") UUID player, @Name("amount") int amount) {
		LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player);

		user.setKills(amount);
		sender.sendMessage(CC.translate("&aSuccessfully set " + user.getName() + "'s kills statistic to " + amount + "!"));
	}

	@Subcommand("admin setdeaths")
	@CommandPermission("legend.command.stats.update")
	@CommandCompletion("@players")
	public void setDeaths(CommandSender sender, @Name("player") UUID player, @Name("amount") int amount) {
		LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player);

		user.setDeaths(amount);
		sender.sendMessage(CC.translate("&aSuccessfully set " + user.getName() + "'s deaths statistic to " + amount + "!"));
	}

	@Subcommand("admin setkillstreak")
	@CommandPermission("legend.command.stats.update")
	@CommandCompletion("@players")
	public void setKillStreak(CommandSender sender, @Name("player") UUID player, @Name("amount") int amount) {
		LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player);

		user.setKillStreak(amount);
		sender.sendMessage(CC.translate("&aSuccessfully set " + user.getName() + "'s killstreak statistic to " + amount + "!"));
	}

	@Subcommand("admin sethighestkillstreak")
	@CommandPermission("legend.command.stats.update")
	@CommandCompletion("@players")
	public void setHigheastKillStreak(CommandSender sender, @Name("player") UUID player, @Name("amount") int amount) {
		LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player);

		user.setHighestKillStreak(amount);
		sender.sendMessage(CC.translate("&aSuccessfully set " + user.getName() + "'s highest killstreak statistic to " + amount + "!"));
	}

	@Subcommand("admin populate")
	@CommandPermission("legend.command.stats.populate")
	@CommandCompletion("@players")
	public void populate(CommandSender sender) {
		List<String> names = Arrays.asList(
				"Test",
				"Friend",
				"Dead",
				"Travis",
				"LBuddyBoy",
				"Premieres",
				"Clickii",
				"Arrow",
				"Snowball",
				"Egg",
				"Legend",
				"Land",
				"Free",
				"Fat",
				"Crime"
		);

        for (String name : names) {
            UUID uuid = UUIDUtils.fetchUUID(name);
			LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(uuid);

            setKills(sender, uuid, ThreadLocalRandom.current().nextInt(100));
            setDeaths(sender, uuid, ThreadLocalRandom.current().nextInt(100));
            setHigheastKillStreak(sender, uuid, ThreadLocalRandom.current().nextInt(100));
            setKillStreak(sender, uuid, ThreadLocalRandom.current().nextInt(100));
			user.setBalance(ThreadLocalRandom.current().nextDouble(1000D, 200_000D));
			user.setPlayTime(ThreadLocalRandom.current().nextLong(10_000L, 500_000L));
        }
	}

	@Subcommand("admin summarize")
	@CommandPermission("legend.command.summarize")
	public void summarize(CommandSender sender) {
		if (sender instanceof Player) {
			sender.sendMessage(CC.translate("&cConsole only."));
			return;
		}

		List<OfflinePlayer> players = Arrays.asList(Bukkit.getOfflinePlayers());
		long startedAt = System.currentTimeMillis();
		String serverName = SettingsConfig.SETTINGS_SERVER_NAME.getString();

		Tasks.runAsync(() -> {
			for (OfflinePlayer player : players) {
				LegendUser legendUser = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());
				User arrowUser = Arrow.getInstance().getUserHandler().getOrCreateUser(player.getUniqueId());
				UserMetadata metadata = arrowUser.getPersistentMetadata();

				metadata.setInteger("LEGEND_KILLS_" + serverName, metadata.getInteger("LEGEND_KILLS_" + serverName) + legendUser.getKills());
				metadata.setInteger("LEGEND_DEATHS_" + serverName, metadata.getInteger("LEGEND_DEATHS_" + serverName) + legendUser.getDeaths());
				metadata.setInteger("LEGEND_HIGHEST_KS_" + serverName, metadata.getInteger("LEGEND_HIGHEST_KS_" + serverName) + legendUser.getHighestKillStreak());
				metadata.setDouble("LEGEND_BALANCE_" + serverName, metadata.getDouble("LEGEND_BALANCE_" + serverName) + legendUser.getBalance());
				metadata.setLong("LEGEND_PLAYTIME_" + serverName, metadata.getLong("LEGEND_PLAYTIME_" + serverName) + legendUser.getPlayTime());
				metadata.set("LEGEND_PLAYTIME_FORMATTED_" + serverName, TimeUtils.formatIntoDetailedStringShort((int) (metadata.getLong("LEGEND_PLAYTIME_" + serverName) / 1000L)));

				arrowUser.save();
			}

			sender.sendMessage(CC.translate("&aSummarized " + players.size() + " players in " + (System.currentTimeMillis() - startedAt) + " ms."));
		});
	}

}