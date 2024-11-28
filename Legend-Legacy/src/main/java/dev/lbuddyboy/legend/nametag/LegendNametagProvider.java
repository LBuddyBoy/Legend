package dev.lbuddyboy.legend.nametag;

import dev.lbuddyboy.arrow.ArrowPlugin;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.nametag.NametagProvider;
import dev.lbuddyboy.commons.nametag.model.NametagInfo;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.LegendConstants;
import dev.lbuddyboy.legend.features.leaderboard.LeaderBoardUser;
import dev.lbuddyboy.legend.features.leaderboard.impl.KillLeaderBoardStat;
import dev.lbuddyboy.legend.team.model.Team;
import net.minecraft.server.v1_8_R3.EnumChatFormat;
import net.minecraft.server.v1_8_R3.ScoreboardTeamBase;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class LegendNametagProvider extends NametagProvider {

    public LegendNametagProvider() {
        super("Legend", 1000);
    }

    @Override
    public NametagInfo fetchNametag(Player target, Player viewer) {
        boolean staffViewing = false;
        boolean invisible = target.hasPotionEffect(PotionEffectType.INVISIBILITY);

        if (viewer.hasPermission("arrow.staff")) {
            staffViewing = ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isVanished(viewer) || ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isInStaffMode(viewer);
        }

        NameTagColorFormat format = null;
        Map<UUID, LeaderBoardUser> leaderboard = LegendBukkit.getInstance().getLeaderBoardHandler().getLeaderBoard(KillLeaderBoardStat.class);
        LeaderBoardUser leaderBoardUser = leaderboard.get(target.getUniqueId());
        String prefix = leaderBoardUser != null ? leaderBoardUser.getFancyPlace() + " " : "";

        for (NameTagColorFormat nameTagColorFormat : NameTagColorFormat.values()) {
            if (nameTagColorFormat.getPredicate().test(viewer, target)) {
                format = nameTagColorFormat;
                break;
            }
        }

        ScoreboardTeamBase.EnumNameTagVisibility visibility = ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS;

        if (invisible) {
            visibility = ScoreboardTeamBase.EnumNameTagVisibility.NEVER;
        }

        if (format != null && (format == NameTagColorFormat.TEAMMATE || format == NameTagColorFormat.ALLY || staffViewing)) {
            visibility = ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS;
        }

        if (format != null) {
            return createNametag(prefix + format.getColor().toString(), "", visibility, EnumChatFormat.valueOf(format.getColor().name()));
        }

        return createNametag(CC.translate(prefix + "&c"), "", visibility, EnumChatFormat.RED);
    }

    @Override
    public List<String> getLunarLines(Player target, Player viewer) {
        NametagInfo playerInfo = this.fetchNametag(target, viewer);
        List<String> tags = new ArrayList<>();
        boolean vanished = ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isVanished(target);
        boolean staffMode = ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isInStaffMode(target);
        String addition = "";
        Team targetTeam = LegendBukkit.getInstance().getTeamHandler().getTeam(target.getUniqueId()).orElse(null);

        tags.add(playerInfo.getPrefix() + target.getName() + playerInfo.getSuffix());

        if (targetTeam != null) {
            tags.add(targetTeam.getFancyPlace() + " &7[" + targetTeam.getName(viewer) + " &7| " + targetTeam.getDTRColor() + LegendConstants.KDR_FORMAT.format(targetTeam.getDeathsUntilRaidable()) + targetTeam.getDTRSymbol() + "&7]");
        }

        if (vanished || staffMode) {
            if (vanished) addition += "&a[Hidden]";
            if (staffMode) addition += "&d[Staff Mode]";
            if (vanished && staffMode) addition = "&a[V] &d[Staff Mode]";

            tags.add(addition);
        }

        return tags;
    }
}
