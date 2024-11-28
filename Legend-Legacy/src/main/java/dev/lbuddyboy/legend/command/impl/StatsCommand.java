package dev.lbuddyboy.legend.features.leaderboard.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.model.LegendUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

	@Subcommand("setkills")
	@CommandPermission("samurai.command.stats.update")
	@CommandCompletion("@players")
	public void setKills(CommandSender sender, @Name("player") UUID player, @Name("amount") int amount) {
		LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player);

		user.setKills(amount);
		sender.sendMessage(CC.translate("&aSuccessfully set " + user.getName() + "'s kills statistic to " + amount + "!"));
	}

	@Subcommand("setdeaths")
	@CommandPermission("samurai.command.stats.update")
	@CommandCompletion("@players")
	public void setDeaths(CommandSender sender, @Name("player") UUID player, @Name("amount") int amount) {
		LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player);

		user.setDeaths(amount);
		sender.sendMessage(CC.translate("&aSuccessfully set " + user.getName() + "'s deaths statistic to " + amount + "!"));
	}

	@Subcommand("setkillstreak")
	@CommandPermission("samurai.command.stats.update")
	@CommandCompletion("@players")
	public void setKillStreak(CommandSender sender, @Name("player") UUID player, @Name("amount") int amount) {
		LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player);

		user.setKillStreak(amount);
		sender.sendMessage(CC.translate("&aSuccessfully set " + user.getName() + "'s killstreak statistic to " + amount + "!"));
	}

	@Subcommand("sethighestkillstreak")
	@CommandPermission("samurai.command.stats.update")
	@CommandCompletion("@players")
	public void setHigheastKillStreak(CommandSender sender, @Name("player") UUID player, @Name("amount") int amount) {
		LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player);

		user.setHighestKillStreak(amount);
		sender.sendMessage(CC.translate("&aSuccessfully set " + user.getName() + "'s highest killstreak statistic to " + amount + "!"));
	}

}