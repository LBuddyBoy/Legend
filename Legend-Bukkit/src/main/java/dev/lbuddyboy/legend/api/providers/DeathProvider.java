package dev.lbuddyboy.legend.api.providers;

import dev.lbuddyboy.commons.deathmessage.DeathMessageProvider;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.EntityUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.settings.Setting;
import dev.lbuddyboy.legend.user.model.LegendUser;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import javax.annotation.Nullable;

public class DeathProvider implements DeathMessageProvider {

    @Override
    public boolean shouldSendDeathMessage(Player player, PlayerDeathEvent playerDeathEvent) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        return !user.isTimerActive("deathban");
    }

    @Override
    public boolean shouldReceiveDeathMessage(Player player) {
        return Setting.DEATH_MESSAGES.isToggled(player.getUniqueId());
    }

    @Override
    public String getPlayerFormat(Player player) {
        if (player == null) return "";

        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());
        String coloredName = CC.blend(user.getName(), "&4", "&c");
        String coloredKills = CC.blend("[" + user.getKills() + "]", "&7", "&f");

        return CC.translate(coloredName + coloredKills);
    }

    @Override
    public String getMobFormat(@Nullable EntityType type) {
        String coloredName = CC.blend(EntityUtils.getName(type), "&4", "&c");

        return CC.translate(coloredName);
    }

    @Override
    public String getDeathMessageFormat(@Nullable String deathMessage) {
        if (deathMessage == null) return "";

        return CC.blend(deathMessage, "&7", "&f");
    }

    @Override
    public String getDeathMessage(String deathMessage) {
        return CC.translate("<blend:&4;&c>&lDEATH</> &7» ") + CC.translate(deathMessage);
    }
}
