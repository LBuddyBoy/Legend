package dev.lbuddyboy.legend.api.providers;

import dev.lbuddyboy.api.ArrowAPI;
import dev.lbuddyboy.api.rank.Rank;
import dev.lbuddyboy.api.spoof.SpoofPlayer;
import dev.lbuddyboy.api.user.model.User;
import dev.lbuddyboy.arrow.Arrow;
import dev.lbuddyboy.arrow.staffmode.StaffModeConstants;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.util.CC;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "legend";
    }

    @Override
    public String getAuthor() {
        return "LBuddyBoy";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }


    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player != null) {

            List<UUID> names = getPlayerUUIDList(player);

            for (int slot = 1; slot <= 80; slot++) {
                int index = slot - 1;

                if (params.equalsIgnoreCase("player_" + slot)) {
                    if (names.size() <= index) return "";

                    UUID uuid = names.get(index);
                    Rank defaultRank = Arrow.getInstance().getRankHandler().getDefaultRank();

                    if (defaultRank != null && Arrow.getInstance().getSpoofHandler().getPlayers().containsKey(uuid)) {
                        return defaultRank.getPrefix() + Arrow.getInstance().getSpoofHandler().getPlayers().get(uuid).getName();
                    }

                    User arrowUser = Arrow.getInstance().getUserHandler().getOrCreateUser(uuid);
                    Rank rank = arrowUser.getHighestRank();


                    return CC.translate(rank.getPrefix() + (rank.getPrefix() != null && !rank.getPrefix().isEmpty() ? "" : " ") + arrowUser.getColoredName() + rank.getSuffix());
                }
            }
        }

        return null;
    }

    public static List<UUID> getPlayerUUIDList(CommandSender sender) {
        List<UUID> players = new ArrayList<>(Bukkit.getOnlinePlayers().stream().filter((p) -> sender.hasPermission("arrow.staff") || !p.hasMetadata(StaffModeConstants.VANISH_META_DATA)).map(OfflinePlayer::getUniqueId).toList());

        players.addAll(Arrow.getInstance().getSpoofHandler().getLocalPlayers().stream().map(SpoofPlayer::getUuid).toList());

        return players.stream().sorted(new ListComparator()).toList();
    }

    public static class ListComparator implements Comparator<UUID> {

        public int compareTo(@NotNull UUID uuid) {
            if (ArrowAPI.getInstance().getSpoofHandler().getPlayers().containsKey(uuid)) {
                SpoofPlayer player = ArrowAPI.getInstance().getSpoofHandler().getPlayers().get(uuid);
                Rank rank = player.retrieveRank();

                if (rank == null) {
                    return Integer.MAX_VALUE;
                }

                return rank.getWeight();
            }

            User user = Arrow.getInstance().getUserHandler().getOrCreateUser(uuid);
            Rank rank = user.getActiveGrant().retrieveRank();

            return rank == null ? Integer.MAX_VALUE : rank.getWeight();
        }

        @Override
        public int compare(UUID o1, UUID o2) {
            return Integer.compare(this.compareTo(o1), this.compareTo(o2));
        }
    }

}