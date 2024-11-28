package dev.lbuddyboy.legend.command.impl.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.commons.Commons;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import org.bukkit.command.CommandSender;

@CommandAlias("legend|core")
@CommandPermission("legend.command.core")
public class LegendCommand extends BaseCommand {

    @Subcommand("togglekitmap")
    public void toggleKitMap(CommandSender sender) {
        boolean kitMapMode = LegendBukkit.getInstance().getSettings().getBoolean("kitmap.enabled", false);

        LegendBukkit.getInstance().getSettings().set("kitmap.enabled", !kitMapMode);
        LegendBukkit.getInstance().getSettings().save();
        sender.sendMessage(CC.translate("&aYou have just turned kitmap mode " + (kitMapMode ? "on" : "off")));
    }

    @Subcommand("setreleased")
    public void setReleased(CommandSender sender) {
        LegendBukkit.getInstance().getSettings().set("server.released-at", System.currentTimeMillis());
        LegendBukkit.getInstance().getSettings().save();
        LegendBukkit.getInstance().loadPlaceholders();
        sender.sendMessage(CC.translate("&aRelease date updated as well as the global placeholder!"));
    }

    @Subcommand("setmaplength")
    public void setMapLength(CommandSender sender, @Name("duration") TimeDuration duration) {
        LegendBukkit.getInstance().getSettings().set("server.map-length", duration.getInput());
        LegendBukkit.getInstance().getSettings().save();
        LegendBukkit.getInstance().loadPlaceholders();
        sender.sendMessage(CC.translate("&aMap Length updated as well as the global placeholder!"));
    }

}
