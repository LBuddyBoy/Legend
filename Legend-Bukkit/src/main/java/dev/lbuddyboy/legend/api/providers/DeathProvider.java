package dev.lbuddyboy.legend.api.providers;

import dev.lbuddyboy.api.user.model.KillTag;
import dev.lbuddyboy.api.user.model.User;
import dev.lbuddyboy.arrow.Arrow;
import dev.lbuddyboy.commons.deathmessage.DeathMessageProvider;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.EntityUtils;
import dev.lbuddyboy.legend.LangConfig;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.features.settings.Setting;
import dev.lbuddyboy.legend.user.model.LegendUser;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import javax.annotation.Nullable;
import java.util.UUID;

public class DeathProvider implements DeathMessageProvider {

    @Override
    public boolean shouldSendDeathMessage(Player player, PlayerDeathEvent playerDeathEvent) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        return !user.isTimerActive("deathban") && !user.isDeathBanned() && !CitizensAPI.getNPCRegistry().isNPC(player);
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

        if (SettingsConfig.SETTINGS_LEGACY_KILL_MESSAGE.getBoolean()) {
            coloredName = "&c" + user.getName();
            coloredKills = "&7[&f" + user.getKills() + "&7]";
        }

        return CC.translate(coloredName + coloredKills);
    }

    @Override
    public String getMobFormat(@Nullable EntityType type) {
        String coloredName = CC.blend(EntityUtils.getName(type), "&4", "&c");

        if (SettingsConfig.SETTINGS_LEGACY_KILL_MESSAGE.getBoolean()) {
            coloredName = "&c" + EntityUtils.getName(type);
        }

        return CC.translate(coloredName);
    }

    @Override
    public String getDeathMessageFormat(@Nullable String deathMessage) {
        if (deathMessage == null) return "";

        return SettingsConfig.SETTINGS_LEGACY_KILL_MESSAGE.getBoolean() ? CC.translate("&f" + deathMessage) : CC.blend(deathMessage, "&7", "&f");
    }

    @Override
    public String getKillMessage(UUID playerUUID) {
        User user = Arrow.getInstance().getUserHandler().getOrCreateUser(playerUUID);

        return "was " + user.getActiveKillTag().map(KillTag::getText).orElse("slain by");
    }

    @Override
    public String getDeathMessage(String deathMessage) {
        return CC.translate(LangConfig.DEATH_MESSAGE_PREFIX.getString()) + CC.translate(deathMessage);
    }
}
