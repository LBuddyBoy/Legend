package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EffectCommand extends BaseCommand {

    @CommandAlias("speed|sp")
    @CommandPermission("legend.command.speed")
    public void sp(Player sender) {
        if (sender.hasPotionEffect(PotionEffectType.SPEED)) {
            sender.removePotionEffect(PotionEffectType.SPEED);
            return;
        }

        sender.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 1));
    }

    @CommandAlias("fireresistance|fres|fr")
    @CommandPermission("legend.command.fireresistance")
    public void fr(Player sender) {
        if (sender.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
            sender.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
            return;
        }

        sender.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, PotionEffect.INFINITE_DURATION, 1));
    }

}
