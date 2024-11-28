package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.kitmap.streak.KillStreak;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("killstreak|killstreaks|ks")
public class KillStreakCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        List<String> message = new ArrayList<>();

        for (String s : LegendBukkit.getInstance().getLanguage().getStringList("killstreak.info")) {
            if (s.contains("%killstreak-name%") || s.contains("%killstreak-kills%")) {
                for (KillStreak streak : LegendBukkit.getInstance().getKitMapHandler().getKillStreaks()) {
                    message.add(s
                            .replaceAll("%killstreak-name%", streak.getName())
                            .replaceAll("%killstreak-kills%", String.valueOf(streak.getNeededKills()))
                    );
                }
                continue;
            }
            message.add(s);
        }

        message.forEach(s -> sender.sendMessage(CC.translate(s)));
    }

}
