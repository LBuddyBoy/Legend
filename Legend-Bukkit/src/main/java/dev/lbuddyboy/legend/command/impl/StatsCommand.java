package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.api.user.User;
import dev.lbuddyboy.api.user.UserMetadata;
import dev.lbuddyboy.arrow.Arrow;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.model.LegendUser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

	@Subcommand("admin summarize")
	@CommandPermission("legend.command.summarize")
	public void summarize(CommandSender sender) {
		if (sender instanceof Player) {
			sender.sendMessage(CC.translate("&cConsole only."));
			return;
		}

		List<OfflinePlayer> players = Arrays.asList(Bukkit.getOfflinePlayers());
		long startedAt = System.currentTimeMillis();
		String serverName = LegendBukkit.getInstance().getSettings().getString("server.name");

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