package dev.lbuddyboy.legend.command.impl.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.kitmap.kit.Kit;
import dev.lbuddyboy.legend.features.kitmap.kit.menu.EditKitMenu;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.timer.impl.CombatTimer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("ckit|lkit|kitadmin")
public class KitCommand extends BaseCommand {

    @Subcommand("edit")
    @CommandCompletion("@kits")
    public void edit(Player sender, @Name("kit") Kit kit) {
        CombatTimer combatTimer = LegendBukkit.getInstance().getTimerHandler().getTimer(CombatTimer.class);

        if (combatTimer.isActive(sender.getUniqueId())) {
            return;
        }

        if (!TeamType.SPAWN.appliesAt(sender.getLocation())) {
            return;
        }

        new EditKitMenu(kit).openMenu(sender);
    }

    @Subcommand("create")
    @CommandCompletion("@kits")
    @CommandPermission("legend.command.kit")
    public void create(Player sender, @Name("name") String name) {
        if (LegendBukkit.getInstance().getKitMapHandler().getKits().containsKey(name.toLowerCase())) {
            sender.sendMessage(CC.translate("&cThat kit already exists."));
            return;
        }

        Kit kit = new Kit(name);
        LegendBukkit.getInstance().getKitMapHandler().getKits().put(kit.getName(), kit);
        kit.save();
        sender.sendMessage(CC.translate("&e[Kit Admin] Created a new kit with the name '" + kit.getName() + "'!"));
    }

    @Subcommand("delete")
    @CommandCompletion("@kits")
    @CommandPermission("legend.command.kit")
    public void delete(Player sender, @Name("kit") Kit kit) {
        kit.delete();
        sender.sendMessage(CC.translate("&e[Kit Admin] Successfully deleted the " + kit.getName() + " kit!"));
    }

    @Subcommand("setdisplayname")
    @CommandCompletion("@kits")
    @CommandPermission("legend.command.kit")
    public void setdisplayname(Player sender, @Name("kit") Kit kit, @Name("name") String displayName) {
        kit.setDisplayName(displayName);
        kit.save();
        sender.sendMessage(CC.translate("&e[Kit Admin] Successfully updated the display name of the " + kit.getName() + " kit!"));
    }

    @Subcommand("set")
    @CommandCompletion("@kits")
    @CommandPermission("legend.command.kit")
    public void set(Player sender, @Name("kit") Kit kit) {
        kit.setArmor(sender.getInventory().getArmorContents());
        kit.setContents(sender.getInventory().getStorageContents());
        kit.setExtras(sender.getInventory().getExtraContents());
        kit.save();
        sender.sendMessage(CC.translate("&e[Kit Admin] Successfully updated the armor, inventory, and extras of the " + kit.getName() + " kit!"));
    }

    @Subcommand("setarmor")
    @CommandCompletion("@kits")
    @CommandPermission("legend.command.kit")
    public void setArmor(Player sender, @Name("kit") Kit kit) {
        kit.setArmor(sender.getInventory().getArmorContents());
        kit.save();
        sender.sendMessage(CC.translate("&e[Kit Admin] Successfully updated the armor of the " + kit.getName() + " kit!"));
    }

    @Subcommand("setinventory")
    @CommandCompletion("@kits")
    @CommandPermission("legend.command.kit")
    public void setinventory(Player sender, @Name("kit") Kit kit) {
        kit.setContents(sender.getInventory().getStorageContents());
        kit.save();
        sender.sendMessage(CC.translate("&e[Kit Admin] Successfully updated the inventory of the " + kit.getName() + " kit!"));
    }

    @Subcommand("seteditoritems")
    @CommandCompletion("@kits")
    @CommandPermission("legend.command.kit")
    public void seteditoritems(Player sender, @Name("kit") Kit kit) {
        kit.setEditorItems(sender.getInventory().getStorageContents());
        kit.save();
        sender.sendMessage(CC.translate("&e[Kit Admin] Successfully updated the editor items of the " + kit.getName() + " kit!"));
    }

    @Subcommand("geteditoritems")
    @CommandCompletion("@kits")
    @CommandPermission("legend.command.kit")
    public void geteditoritems(Player sender, @Name("kit") Kit kit) {
        sender.getInventory().setStorageContents(kit.getEditorItems());
        sender.sendMessage(CC.translate("&e[Kit Admin] Successfully loaded the editor items of the " + kit.getName() + " kit!"));
    }

    @Subcommand("setextras")
    @CommandCompletion("@kits")
    @CommandPermission("legend.command.kit")
    public void setextras(Player sender, @Name("kit") Kit kit) {
        kit.setExtras(sender.getInventory().getExtraContents());
        kit.save();
        sender.sendMessage(CC.translate("&e[Kit Admin] Successfully updated the off hand items of the " + kit.getName() + " kit!"));
    }

    @Subcommand("apply")
    @CommandCompletion("@kits")
    @CommandPermission("legend.command.kit")
    public void apply(CommandSender sender, @Name("kit") Kit kit, @Name("player") @Optional OnlinePlayer target) {
        if (target == null && sender instanceof Player senderPlayer) target = new OnlinePlayer(senderPlayer);

        if (target == null) {
            sender.sendMessage(CC.translate("&cPlease provide a players name."));
            return;
        }

        if (!TeamType.SPAWN.appliesAt(target.getPlayer().getLocation())) {
            sender.sendMessage(CC.translate("&cThat player is not in spawn."));
            return;
        }

        kit.apply(target.getPlayer());
    }

}
