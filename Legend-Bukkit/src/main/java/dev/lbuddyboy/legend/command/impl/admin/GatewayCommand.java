package dev.lbuddyboy.legend.command.impl.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.gateways.GatewayHandler;
import dev.lbuddyboy.legend.features.gateways.model.Gateway;
import dev.lbuddyboy.legend.features.gateways.model.Selection;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("gateway|gateways|gw")
@CommandPermission("legend.command.gateway")
public class GatewayCommand extends BaseCommand {

    private final GatewayHandler gatewayHandler = LegendBukkit.getInstance().getGatewayHandler();

    @Subcommand("create")
    public void create(CommandSender sender, @Name("name") String name) {
        if (this.gatewayHandler.getGateways().containsKey(name.toLowerCase())) {
            sender.sendMessage(CC.translate("&cThat gateway already exists."));
            return;
        }

        Gateway gateway = new Gateway(name.toLowerCase());

        this.gatewayHandler.getGateways().put(gateway.getId(), gateway);
        sender.sendMessage(CC.translate("&aGateway created."));
    }

    @Subcommand("delete")
    @CommandCompletion("@gateways")
    public void delete(CommandSender sender, @Name("gateway") Gateway gateway) {
        gateway.delete();
        this.gatewayHandler.getGateways().remove(gateway.getId());
        sender.sendMessage(CC.translate("&cGateway deleted."));
    }

    @Subcommand("setentrance region")
    @CommandCompletion("@gateways")
    public void setEntranceRegion(Player sender, @Name("gateway") Gateway gateway) {
        Selection selection = Selection.getOrCreateSelection(sender);

        if (selection == null) {
            sender.sendMessage(CC.translate("&cYou need to make a selection first. /gateway wand"));
            return;
        }

        if (selection.getLoc1() == null) {
            sender.sendMessage(CC.translate("&cYou need to make a left click selection. /gateway wand"));
            return;
        }

        if (selection.getLoc2() == null) {
            sender.sendMessage(CC.translate("&cYou need to make a right click selection. /gateway wand"));
            return;
        }

        gateway.setEntranceRegion(selection.getCuboid());
        gateway.save();
        sender.sendMessage(CC.translate("&aSuccessfully updated " + gateway.getId() + "'s entrance region."));
        sender.removeMetadata(Selection.SELECTION_METADATA_KEY, LegendBukkit.getInstance());
    }

    @Subcommand("setentrance location")
    @CommandCompletion("@gateways")
    public void setEntranceLocation(Player sender, @Name("gateway") Gateway gateway) {
        if (gateway.getEntranceRegion() != null && gateway.getEntranceRegion().contains(sender.getLocation())) {
            sender.sendMessage(CC.translate("&cIdek how you set it here without it teleporting you."));
            return;
        }

        gateway.setEntranceLocation(sender.getLocation().clone());
        gateway.save();
        sender.sendMessage(CC.translate("&aSuccessfully updated " + gateway.getId() + "'s entrance location."));
    }

    @Subcommand("setexit region")
    @CommandCompletion("@gateways")
    public void setExitRegion(Player sender, @Name("gateway") Gateway gateway) {
        Selection selection = Selection.getOrCreateSelection(sender);

        if (selection == null) {
            sender.sendMessage(CC.translate("&cYou need to make a selection first. /gateway wand"));
            return;
        }

        if (selection.getLoc1() == null) {
            sender.sendMessage(CC.translate("&cYou need to make a left click selection. /gateway wand"));
            return;
        }

        if (selection.getLoc2() == null) {
            sender.sendMessage(CC.translate("&cYou need to make a right click selection. /gateway wand"));
            return;
        }

        gateway.setExitRegion(selection.getCuboid());
        gateway.save();
        sender.sendMessage(CC.translate("&aSuccessfully updated " + gateway.getId() + "'s exit region."));
        sender.removeMetadata(Selection.SELECTION_METADATA_KEY, LegendBukkit.getInstance());
    }

    @Subcommand("setexit location")
    @CommandCompletion("@gateways")
    public void setExitLocation(Player sender, @Name("gateway") Gateway gateway) {
        if (gateway.getExitRegion() != null && gateway.getExitRegion().contains(sender.getLocation())) {
            sender.sendMessage(CC.translate("&cIdek how you set it here without it teleporting you."));
            return;
        }

        gateway.setExitLocation(sender.getLocation().clone());
        gateway.save();
        sender.sendMessage(CC.translate("&aSuccessfully updated " + gateway.getId() + "'s exit location."));
    }

    @Subcommand("wand")
    public void wand(Player sender) {
        sender.getInventory().addItem(Selection.SELECTION_WAND);
    }

}
