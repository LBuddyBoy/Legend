package dev.lbuddyboy.legend.settings;

import dev.lbuddyboy.api.ArrowAPI;
import dev.lbuddyboy.api.user.model.User;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.model.LegendUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
public enum Setting {

    DEATH_MESSAGES("death-messages", "Death Messages", "&4", "&c", Material.PLAYER_HEAD, Arrays.asList(
            "&7Ability to see death messages",
            "&7that are announced in chat."
    ), true),

    GLOBAL_CHAT("globalchat", "Global Chat", "&6", "&e", Material.BEACON, Arrays.asList(
            "&7Ability to see chat messages",
            "&7that are sent in global chat."
    ), true),

    MESSAGES("receivingmessages", "Private Messages", "&d", "&7", Material.CHEST, Arrays.asList(
            "&7Ability to see messages that",
            "&7personally sent to you."
    ), true),

    ADDITIONAL_TEAM_INFO("extrateaminfo", "Informational Team Info", "&d", "&5", Material.BOOK, Arrays.asList(
            "&7Ability to hover over texts for more",
            "&7information in /team info."
    ), true),

    DIAMOND_ALERT("diamond-alert", "Diamond Alerts", "&b", "&3", Material.DIAMOND, Arrays.asList(
            "&7Ability to see the message sent",
            "&7when a player finds diamond."
    ), true),

    CLAIM_ENTER_LEAVE("claim-messages", "Enter/Leave Claim Messages", "&a", "&2", Material.OAK_SIGN, Arrays.asList(
            "&7Ability to see the message sent",
            "&7when you leave/enter a claim."
    ), true),

    CLAIM_ENTER_LEAVE_TITLE("claim-title", "Enter/Leave Claim Title", "&6", "&e", Material.PAINTING, Arrays.asList(
            "&7Ability to see the title sent",
            "&7when you leave/enter a claim."
    ), true),

    COBBLESTONE("cobblestone", "Cobblestone Pickup", "&8", "&7", Material.COBBLESTONE, Arrays.asList(
            "&7Ability to pickup cobblestone &",
            "&7it's alike stones."
    ), true);

    private final String id, name, primaryColor, secondaryColor;
    private final Material displayMaterial;
    private final List<String> description;
    private final boolean defaultValue;

    public void toggle(UUID player) {
        if (this == GLOBAL_CHAT || this == MESSAGES) {
            User user = ArrowAPI.getInstance().getUserHandler().getOrCreateUser(player);

            user.getPersistentMetadata().toggle(this.id, this.defaultValue);
            return;
        }

        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player);

        if (!user.getSettings().containsKey(this)) {
            user.getSettings().put(this, this.defaultValue);
        }

        user.getSettings().put(this, !user.getSettings().get(this));
    }

    public boolean isToggled(Player player) {
        return isToggled(player.getUniqueId());
    }

    public boolean isToggled(UUID player) {
        if (this == GLOBAL_CHAT || this == MESSAGES) {
            User user = ArrowAPI.getInstance().getUserHandler().getOrCreateUser(player);

            return user.getPersistentMetadata().getBooleanOrDefault(this.id, this.defaultValue);
        }

        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player);

        return user.getSettings().getOrDefault(this, this.defaultValue);
    }

}
