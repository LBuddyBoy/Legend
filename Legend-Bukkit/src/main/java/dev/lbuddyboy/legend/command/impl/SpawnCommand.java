package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.timer.impl.CombatTimer;
import dev.lbuddyboy.legend.timer.impl.SpawnTimer;
import dev.lbuddyboy.legend.timer.server.SOTWTimer;
import org.bukkit.entity.Player;

@CommandAlias("spawn")
public class SpawnCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        boolean permission = sender.hasPermission("legend.command.spawn");
        SOTWTimer sotwTimer = LegendBukkit.getInstance().getTimerHandler().getServerTimer(SOTWTimer.class);
        CombatTimer combatTimer = LegendBukkit.getInstance().getTimerHandler().getTimer(CombatTimer.class);
        boolean kitMap = SettingsConfig.KITMAP_ENABLED.getBoolean();

        if (TeamType.SPAWN.appliesAt(sender.getLocation())) {
            sender.teleport(LegendBukkit.getInstance().getSpawnLocation());
            return;
        }

        if (permission) {
            sender.teleport(LegendBukkit.getInstance().getSpawnLocation());
            return;
        }

        if (sotwTimer.isActive() && !sotwTimer.isEnabled(sender)) {
            sender.teleport(LegendBukkit.getInstance().getSpawnLocation());
            return;
        }

        if (kitMap) {
            if (combatTimer.isActive(sender.getUniqueId())) {
                sender.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("combat-tagged.spawn")));
                return;
            }

            LegendBukkit.getInstance().getTimerHandler().getTimer(SpawnTimer.class).start(sender);
            return;
        }

        sender.sendMessage(CC.translate("&cNo permission."));
    }

}
