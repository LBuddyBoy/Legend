package dev.lbuddyboy.legend.nametag;

import dev.lbuddyboy.arrow.ArrowPlugin;
import dev.lbuddyboy.arrow.staffmode.StaffModeConstants;
import dev.lbuddyboy.commons.nametag.NameTagImpl;
import dev.lbuddyboy.commons.nametag.model.NameVisibility;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.LegendConstants;
import dev.lbuddyboy.legend.features.leaderboard.impl.KillLeaderBoardStat;
import dev.lbuddyboy.legend.listener.UHCListener;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.timer.impl.ArcherTagTimer;
import dev.lbuddyboy.legend.timer.impl.InvincibilityTimer;
import dev.lbuddyboy.legend.timer.server.SOTWTimer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class LegendNametagProvider implements NameTagImpl {

    @Override
    public List<String> getLunarTag(Player viewer, Player target) {
        String info = this.getTag(viewer, target);
        List<String> tags = new ArrayList<>();
        boolean vanished = ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isVanished(target);
        boolean staffMode = ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isInStaffMode(target);
        String addition = "";
        Team targetTeam = LegendBukkit.getInstance().getTeamHandler().getTeam(target.getUniqueId()).orElse(null);


        if (LegendBukkit.getInstance().getSettings().getBoolean("server.uhc-mode", false)) {
            tags.add("&f" + UHCListener.getRawAmountOfHearts(target) + " &4❤");
        }

        tags.add(info + target.getName());

        if (targetTeam != null) {
            tags.add(targetTeam.applyPlaceholders(LegendBukkit.getInstance().getLanguage().getString("nametags.lunar"), viewer));
        }

        if (LegendBukkit.getInstance().getLanguage().getBoolean("nametags.staff-lines")) {
            if (vanished || staffMode) {
                if (vanished) addition += "&a[Hidden]";
                if (staffMode) addition += "&d[Staff Mode]";
                if (vanished && staffMode) addition = "&a[V] &d[Staff Mode]";

                tags.add(addition);
            }
        }

        return tags;
    }

    @Override
    public String getTag(Player viewer, Player target) {
        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(viewer).orElse(null);
        SOTWTimer sotwTimer = LegendBukkit.getInstance().getTimerHandler().getServerTimer(SOTWTimer.class);
        ArcherTagTimer archerTagTimer = LegendBukkit.getInstance().getTimerHandler().getTimer(ArcherTagTimer.class);
        InvincibilityTimer invincibilityTimer = LegendBukkit.getInstance().getTimerHandler().getTimer(InvincibilityTimer.class);
        String fancyPlace = LegendBukkit.getInstance().getLeaderBoardHandler().getFancyPlace(target.getUniqueId(), KillLeaderBoardStat.class);

        if (target.hasPotionEffect(PotionEffectType.INVISIBILITY) && viewer.hasMetadata(StaffModeConstants.STAFF_MODE_META_DATA)) {
            return createTeam(viewer, target, "staff", "", "&b", NameVisibility.ALWAYS);
        }

        if (target.hasMetadata(StaffModeConstants.STAFF_MODE_META_DATA)) {
            return createTeam(viewer, target, "staff", "", "&b", NameVisibility.ALWAYS);
        } else if (team != null && team.isMember(target.getUniqueId()) || viewer.equals(target)) {
            return createTeam(viewer, target, "team", "", "&2", NameVisibility.ALWAYS);
        } else if (target.hasPotionEffect(PotionEffectType.INVISIBILITY) && !viewer.hasMetadata(StaffModeConstants.STAFF_MODE_META_DATA)) {
            return createTeam(viewer, target, "invis", "", "", NameVisibility.NEVER);
        } else if (team != null && team.isAlly(target)) {
            return createTeam(viewer, target, "allies", "", "&3", NameVisibility.ALWAYS);

        } else if (sotwTimer.isActive() && !sotwTimer.isEnabled(target)) {
            return createTeam(viewer, target, "sotw", "", "&6", NameVisibility.ALWAYS);

        } else if (archerTagTimer.isActive(target.getUniqueId())) {
            return createTeam(viewer, target, "tags", "", "&4", NameVisibility.ALWAYS);

        } else if (team != null && (team.getFocusedTeam() != null && team.getFocusedTeam().isMember(target.getUniqueId()) || team.getFocusedPlayer() != null && team.getFocusedPlayer().equals(target.getUniqueId()))) {
            return createTeam(viewer, target, "focused", "", "&d", NameVisibility.ALWAYS);

        } else if (invincibilityTimer.isActive(target.getUniqueId())) {
            return createTeam(viewer, target, "invinc", "", "&6", NameVisibility.ALWAYS);
        }

        return createTeam(viewer, target, "enemies", "", "&c", NameVisibility.ALWAYS);
/*        boolean staffViewing = false;

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

        if (target.hasPotionEffect(PotionEffectType.INVISIBILITY) && friendly) {
            visibility = NameVisibility.FRIENDLY_INVIS;
        }

        return getTag(visibility.name() + "_" + name, prefix, suffix, color, visibility);*/
    }

    @Override
    public boolean isLunarSupported() {
        return true;
    }

}