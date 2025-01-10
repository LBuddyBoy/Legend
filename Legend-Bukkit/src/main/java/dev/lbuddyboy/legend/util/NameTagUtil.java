package dev.lbuddyboy.legend.util;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.nametag.Nametag;
import com.lunarclient.apollo.module.nametag.NametagModule;
import dev.lbuddyboy.arrow.ArrowPlugin;
import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.crates.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.listener.UHCListener;
import dev.lbuddyboy.legend.user.model.nametag.NameTagColorFormat;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.user.model.LegendUser;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NameTagUtil {

    public static void updateTargetsForViewer(Player viewer, List<Player> players) {
        players.forEach(target -> updateNameTag(viewer, target));
    }

    public static void updateTargetForViewers(Player target, List<Player> viewers) {
        viewers.forEach(viewer -> updateNameTag(viewer, target));
    }


    public static void updateTargetsForViewers(List<Player> targets, List<Player> viewers) {
        viewers.forEach(viewer -> targets.forEach(target -> updateNameTag(viewer, target)));
    }

    public static void updateTargetsForViewer(List<Player> targets, Player viewer) {
        targets.forEach(target -> updateNameTag(viewer, target));
    }

    public static void updateNameTag(Player viewer, Player target) {
        if (true) {
//            CommonsPlugin.getInstance().getNameTagHandler().update(target, viewer);
            return;
        }
        if (viewer.equals(target)) return;

        NameTagColorFormat format = null;

        for (NameTagColorFormat colorFormat : NameTagColorFormat.values()) {
            if (colorFormat.getPredicate().test(viewer, target)) {
                format = colorFormat;
                break;
            }
        }

        if (format != null) {
            NameTagColorFormat finalFormat = format;
            Tasks.runAsync(() -> Apollo.getPlayerManager().getPlayer(viewer.getUniqueId()).ifPresent(viewerPlayer -> {
                NametagModule module = Apollo.getModuleManager().getModule(NametagModule.class);

                module.overrideNametag(viewerPlayer, target.getUniqueId(), Nametag.builder()
                        .lines(getLunarTag(finalFormat, viewer, target).stream().map(CC::translateComponent).map(c -> ((Component)c)).toList())
                        .build());
            }));
        }
    }

    public static List<String> getLunarTag(NameTagColorFormat format, Player viewer, Player target) {
        List<String> tags = new ArrayList<>();
        boolean vanished = ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isVanished(target);
        boolean staffMode = ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isInStaffMode(target);
        String addition = "";
        Team targetTeam = LegendBukkit.getInstance().getTeamHandler().getTeam(target.getUniqueId()).orElse(null);

        if (SettingsConfig.SETTINGS_UHC_MODE.getBoolean()) {
            tags.add("&f" + UHCListener.getRawAmountOfHearts(target) + " &4❤");
        }

        tags.add(format.getScoreboardEntryType().getColor().toString() + target.getName());

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

}
