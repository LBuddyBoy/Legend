package dev.lbuddyboy.samurai.api.impl;

import dev.lbuddyboy.commons.deathmessage.DeathMessageProvider;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.EntityUtils;
import dev.lbuddyboy.minigames.MiniGames;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.settings.Setting;
import dev.lbuddyboy.samurai.user.SamuraiUser;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import javax.annotation.Nullable;

public class DeathProvider implements DeathMessageProvider {

    @Override
    public boolean shouldSendDeathMessage(Player player, PlayerDeathEvent playerDeathEvent) {
        return !CitizensAPI.getNPCRegistry().isNPC(player)
                && !Samurai.getInstance().getTeamWarHandler().getPlayers().contains(player)
                && !Samurai.getInstance().getArenaHandler().isDeathbanned(player.getUniqueId())
                && player.getWorld() != MiniGames.getInstance().getWorld();
    }

    @Override
    public boolean shouldReceiveDeathMessage(Player player) {
        return Setting.DEATH_MESSAGES.isToggled(player.getUniqueId());
    }

    @Override
    public String getPlayerFormat(Player player) {
        if (player == null) return "";

        SamuraiUser user = Samurai.getInstance().getUserHandler().loadUser(player.getUniqueId());
        String coloredName = CC.blend(user.getName(), "&6", "&e");
        String coloredKills = CC.blend("[" + user.getKills() + "]", "&4", "&c");

        return CC.translate(coloredName + coloredKills);
    }

    @Override
    public String getMobFormat(@Nullable EntityType type) {
        String coloredName = CC.blend(EntityUtils.getName(type), "&6", "&e");

        return CC.translate(coloredName);
    }

    @Override
    public String getDeathMessageFormat(@Nullable String deathMessage) {
        if (deathMessage == null) return "";

        return CC.blend(deathMessage, "&7", "&f");
    }


}
