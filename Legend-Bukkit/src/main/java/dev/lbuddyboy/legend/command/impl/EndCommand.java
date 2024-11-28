package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.LocationUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandAlias("end|endinfo")
public class EndCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        List<String> info = LegendBukkit.getInstance().getLanguage().getStringList("end-info");

        for (String s : info) {
            if (s.contains("%creeper-locations%")) {
                if (getCreeperLocations().isEmpty()) {
                    sender.sendMessage(CC.translate(s.replaceAll("%creeper-locations%", "&cNone")));
                    continue;
                }
                for (Location location : getCreeperLocations()) {
                    sender.sendMessage(CC.translate(s.replaceAll("%creeper-locations%", LocationUtils.toString(location))));
                }
                continue;
            }

            sender.sendMessage(CC.translate(s
                    .replaceAll("%end-exit%", LocationUtils.toString(getExitLocation()))
                    .replaceAll("%end-exit-end%", LocationUtils.toString(getExitEndLocation()))
                    .replaceAll("%end-entrance%", LocationUtils.toString(getEntranceLocation()))
            ));
        }

    }

    @Subcommand("set exit")
    @Description("Sets the exit players are tped leaving end.")
    @CommandPermission("legend.command.end")
    public void setExit(Player sender) {
        LegendBukkit.getInstance().getSettings().set("end.exit", LocationUtils.serializeString(sender.getLocation()));
        LegendBukkit.getInstance().getSettings().save();
        sender.sendMessage(CC.translate("&aSuccessfully set the overworld end exit!"));
    }

    @Subcommand("set exitend")
    @Description("Sets the exit players need to navigate toward to leave the end.")
    @CommandPermission("legend.command.end")
    public void setExitEnd(Player sender) {
        LegendBukkit.getInstance().getSettings().set("end.exit-end", LocationUtils.serializeString(sender.getLocation()));
        LegendBukkit.getInstance().getSettings().save();
        sender.sendMessage(CC.translate("&aSuccessfully set the end exit!"));
    }

    @Subcommand("set entrance")
    @Description("Sets the entrance players are tped to when going to end.")
    @CommandPermission("legend.command.end")
    public void setExitEntrance(Player sender) {
        LegendBukkit.getInstance().getSettings().set("end.exit-entrance", LocationUtils.serializeString(sender.getLocation()));
        LegendBukkit.getInstance().getSettings().save();
        sender.sendMessage(CC.translate("&aSuccessfully set the end entrance!"));
    }

    @Subcommand("creeperlocs add")
    @Description("Adds a location that creepers can be found.")
    @CommandPermission("legend.command.end")
    public void setcreeperloc(Player sender) {
        LegendBukkit.getInstance().getSettings().set("end.creepers", new ArrayList<Location>(getCreeperLocations()){{
            add(sender.getLocation());
        }}.stream().map(LocationUtils::serializeString).collect(Collectors.toList()));
        LegendBukkit.getInstance().getSettings().save();
        sender.sendMessage(CC.translate("&aSuccessfully updated the creeper locations!"));
    }

    @Subcommand("creeperlocs clear")
    @Description("Adds a location that creepers can be found.")
    @CommandPermission("legend.command.end")
    public void setcreeperlocClear(Player sender) {
        LegendBukkit.getInstance().getSettings().set("end.creepers", Collections.emptyList());
        LegendBukkit.getInstance().getSettings().save();
        sender.sendMessage(CC.translate("&aSuccessfully cleared the creeper locations!"));
    }

    public List<Location> getCreeperLocations() {
        return LegendBukkit.getInstance().getSettings().getStringList("end.creepers").stream().map(LocationUtils::deserializeString).collect(Collectors.toList());
    }

    public Location getExitLocation() {
        return LocationUtils.deserializeString(LegendBukkit.getInstance().getSettings().getString("end.exit"));
    }

    public Location getExitEndLocation() {
        return LocationUtils.deserializeString(LegendBukkit.getInstance().getSettings().getString("end.exit-end"));
    }

    public Location getEntranceLocation() {
        return LocationUtils.deserializeString(LegendBukkit.getInstance().getSettings().getString("end.entrance"));
    }

}
