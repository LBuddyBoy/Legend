package dev.lbuddyboy.legend.nametag;

import dev.lbuddyboy.arrow.ArrowPlugin;
import dev.lbuddyboy.commons.nametag.NameTagImpl;
import dev.lbuddyboy.commons.nametag.model.NameTagInfo;
import dev.lbuddyboy.commons.nametag.model.NameVisibility;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.LegendConstants;
import dev.lbuddyboy.legend.features.leaderboard.impl.KillLeaderBoardStat;
import dev.lbuddyboy.legend.listener.UHCListener;
import dev.lbuddyboy.legend.team.model.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class LegendNametagProvider implements NameTagImpl {

    @Override
    public List<String> getLunarTag(Player viewer, Player target) {
        NameTagInfo playerInfo = this.getTag(viewer, target);
        List<String> tags = new ArrayList<>();
        boolean vanished = ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isVanished(target);
        boolean staffMode = ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isInStaffMode(target);
        String addition = "";
        Team targetTeam = LegendBukkit.getInstance().getTeamHandler().getTeam(target.getUniqueId()).orElse(null);

        if (LegendBukkit.getInstance().getSettings().getBoolean("server.uhc-mode", false)) {
            tags.add("&f" + UHCListener.getRawAmountOfHearts(target) + " &4❤");
        }

        tags.add(playerInfo.getPrefix() + playerInfo.getColor() + target.getName() + playerInfo.getSuffix());

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

    @Override
    public NameTagInfo getTag(Player viewer, Player target) {
        boolean staffViewing = false;

        if (viewer.hasPermission("arrow.staff")) {
            staffViewing = ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isVanished(viewer) || ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isInStaffMode(viewer);
        }

        NameTagColorFormat nameTagColorFormat = null;

        for (NameTagColorFormat colorFormat : NameTagColorFormat.values()) {
            if (!colorFormat.getPredicate().test(viewer, target)) continue;

            nameTagColorFormat = colorFormat;
            break;
        }

        String fancyPlace = LegendBukkit.getInstance().getLeaderBoardHandler().getFancyPlace(target.getUniqueId(), KillLeaderBoardStat.class);

        boolean friendly = nameTagColorFormat == NameTagColorFormat.TEAMMATE || nameTagColorFormat == NameTagColorFormat.ALLY || staffViewing;
        ChatColor color = nameTagColorFormat == null ? ChatColor.RED : nameTagColorFormat.getColor();
        String prefix = fancyPlace;
        String suffix = "";
        NameVisibility visibility = NameVisibility.ALWAYS;
        String name = nameTagColorFormat == null ? "DEFAULT" : nameTagColorFormat.name();

        if (target.hasPotionEffect(PotionEffectType.INVISIBILITY) && !friendly && target != viewer) {
            visibility = NameVisibility.NEVER;
        } else if (target.hasPotionEffect(PotionEffectType.INVISIBILITY) && friendly) {
            visibility = NameVisibility.FRIENDLY_INVIS;
            name = "VIEW_INVIS";
        }

        return getTag(name, prefix, suffix, color, visibility);
    }

    @Override
    public boolean isLunarSupported() {
        return true;
    }

}
