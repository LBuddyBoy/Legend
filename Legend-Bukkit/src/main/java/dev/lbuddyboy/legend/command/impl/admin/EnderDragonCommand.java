package dev.lbuddyboy.legend.command.impl.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@CommandAlias("enderdragon")
@CommandPermission("legend.command.enderdragon")
public class EnderDragonCommand extends BaseCommand {

    @Subcommand("spawn")
    public void spawn(Player sender, @Name("health") double health) {
        if (sender.getWorld().getEnvironment() != World.Environment.THE_END) {
            sender.sendMessage(CC.translate("&cYou must be in the end to do this."));
            return;
        }

        LegendBukkit.getInstance().getEnderDragonHandler().spawnDragon(sender.getLocation(), health);
        sender.sendMessage(CC.translate("&aSpawned the enderdragon."));
    }

    @Subcommand("despawn")
    public void despawn(CommandSender sender) {
        LegendBukkit.getInstance().getEnderDragonHandler().despawnDragon();
        sender.sendMessage(CC.translate("&aDespawned the enderdragon."));
    }

    @Subcommand("fix")
    public void fix(Player sender) {
        ((CraftWorld)sender.getWorld()).getHandle().getDragons().forEach(enderDragon -> {
            enderDragon.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
        });
        sender.sendMessage(CC.translate("&aDespawned the enderdragon."));
    }

}
