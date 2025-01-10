package dev.lbuddyboy.legend.features.playtime.model;

import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.settings.Setting;
import dev.lbuddyboy.legend.user.model.LegendUser;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@Getter
public class PlayTimeReward {

    private final String id, displayName, displayMaterial, displayTexture;
    private final List<String> rewards, commands;
    private final TimeDuration requiredPlayTime;

    public PlayTimeReward(String key, ConfigurationSection section) {
        this.id = key;
        this.displayName = section.getString("displayName");
        this.displayMaterial = section.getString("displayMaterial");
        this.displayTexture = section.getString("displayTexture");
        this.requiredPlayTime = new TimeDuration(section.getString("requiredPlayTime"));
        this.rewards = section.getStringList("rewards");
        this.commands = section.getStringList("commands");
    }

    public boolean isAvailable(UUID playerUUID) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(playerUUID);

        return user.getCurrentPlayTime() >= this.requiredPlayTime.transform();
    }

    public boolean isClaimed(UUID playerUUID) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(playerUUID);

        return user.getClaimedPlayTimeRewards().contains(this.id);
    }

    public void claim(Player player) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());
        boolean broadcast = LegendBukkit.getInstance().getPlayTimeGoalHandler().getRewardsConfig().getBoolean("broadcast.enabled");

        if (broadcast) {
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!Setting.PLAYTIME_REWARD_MESSAGES.isToggled(player.getUniqueId())) continue;

                other.sendMessage(CC.translate(LegendBukkit.getInstance().getPlayTimeGoalHandler().getRewardsConfig().getString("broadcast.message")
                        .replaceAll("%player%", player.getName())
                        .replaceAll("%reward_display_name%", this.displayName)
                ));
            }
        }

        user.getClaimedPlayTimeRewards().add(this.id);
        this.commands.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("%player%", player.getName())));
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }

}
