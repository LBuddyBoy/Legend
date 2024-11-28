package dev.lbuddyboy.legend.nametag;

import dev.lbuddyboy.arrow.ArrowPlugin;
import dev.lbuddyboy.commons.nametag.NameTagImpl;
import dev.lbuddyboy.commons.nametag.model.NameTagInfo;
import dev.lbuddyboy.commons.nametag.model.NameVisibility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class LegendNametagProvider implements NameTagImpl {

    @Override
    public NameTagInfo getTag(Player viewer, Player target) {
/*        if (Samurai.getInstance().getFeatureHandler().isDisabled(Feature.NORMAL_NAMETAGS)) {
            return new NameTagInfo("DISABLED", "", "", ChatColor.GRAY, target.hasPotionEffect(PotionEffectType.INVISIBILITY) ? NameVisibility.NEVER : NameVisibility.ALWAYS);
        }*/

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

        boolean friendly = nameTagColorFormat == NameTagColorFormat.TEAMMATE || nameTagColorFormat == NameTagColorFormat.ALLY || staffViewing;
        ChatColor color = nameTagColorFormat == null ? ChatColor.RED : nameTagColorFormat.getColor();
        String prefix = "";
        String suffix = "";
        NameVisibility visibility = NameVisibility.ALWAYS;
        String name = nameTagColorFormat == null ? "DEFAULT" : nameTagColorFormat.name();

        if (target.hasPotionEffect(PotionEffectType.INVISIBILITY) && !friendly && target != viewer) {
            visibility = NameVisibility.ALWAYS;
        } else if (target.hasPotionEffect(PotionEffectType.INVISIBILITY) && friendly) {
            visibility = NameVisibility.FRIENDLY_INVIS;
            name = "VIEW_INVIS";
        }

        return getTag(name, prefix, suffix, color, visibility);
    }

    @Override
    public List<String> getLunarTag(Player viewer, Player target) {

        NameTagInfo playerInfo = getTag(viewer, target);
        List<String> tags = new ArrayList<>();
        boolean vanished = ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isVanished(target);
        boolean staffMode = ArrowPlugin.getInstance().getArrowAPI().getStaffModeHandler().isInStaffMode(target);
        String addition = "";

        tags.add(playerInfo.getPrefix() + playerInfo.getColor() + target.getName() + playerInfo.getSuffix());

/*        if (targetTeam != null) {
            tags.add(targetTeam.getFancyPlace() + " &7[" + targetTeam.getName(viewer) + " &7" + SymbolUtil.STICK + " " + targetTeam.getDTRColor() + targetTeam.getDTR() + targetTeam.getDTRSuffix() + "&7]");
        }*/

        if (vanished || staffMode) {
            if (vanished) addition += "&a[Hidden]";
            if (staffMode) addition += "&d[Staff Mode]";
            if (vanished && staffMode) addition = "&a[V] &d[Staff Mode]";

            tags.add(addition);
        }

        return tags;
    }

    @Override
    public boolean isLunarSupported() {
        return true;
    }
}