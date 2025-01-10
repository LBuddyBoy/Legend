package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import de.tr7zw.nbtapi.NBT;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.util.GradientUtils;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.model.LegendUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;

public class FixCommand extends BaseCommand {

    public static String UNFIXABLE_NBT_DATA = "ARROW_UNFIXABLE";

    @CommandAlias("fix|repair")
    public void fix(Player sender) {
        PlayerInventory inventory = sender.getInventory();
        ItemStack item = inventory.getItemInMainHand();
        boolean bypass = sender.hasPermission("arrow.command.fix");
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(sender.getUniqueId());
        double cost = 1000.0D;

        if (item.getType() == Material.AIR) {
            sender.sendMessage(CC.translate("&c[Fix Error] You need to have an item in your main hand."));
            return;
        }

        if (NBT.get(item, (tag) -> {
            return tag.hasTag(UNFIXABLE_NBT_DATA);
        })) {
            sender.sendMessage(CC.translate("&c[Fix Error] That item is unrepairable."));
            return;
        }

        if (!bypass) {
            if (user.getBalance() < cost) {
                sender.sendMessage(CC.translate("&c[Fix Error] You need at least $" + APIConstants.formatNumber(cost) + " to fix an item."));
                return;
            }
        }

        ItemStack fixed = fix(item);

        if (fixed == null) {
            sender.sendMessage(CC.translate("&c[Fix Error] That item cannot be repaired."));
            return;
        }

        sender.sendMessage(CC.blend("[Fix] Successfully fixed the item in your main hand!", "&a", "&7"));
        if (!bypass) {
            user.setBalance(user.getBalance() - cost);
        }
    }

    @CommandAlias("fixall|repairall")
    @CommandPermission("arrow.command.fixall")
    public void fixAll(Player sender) {
        PlayerInventory inventory = sender.getInventory();
        boolean bypass = sender.hasPermission("arrow.command.fixall");
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(sender.getUniqueId());
        double cost = 5000.0D;

        if (!bypass) {
            if (user.getBalance() < cost) {
                sender.sendMessage(CC.translate("&c[Fix Error] You need at least $" + APIConstants.formatNumber(cost) + " to fix all items in your inventory."));
                return;
            }
        }

        int i = -1;
        for (ItemStack content : inventory.getStorageContents()) {
            i++;

            if (content == null || content.getType() == Material.AIR) continue;
            if (NBT.get(content, (tag) -> {
                return tag.hasTag(UNFIXABLE_NBT_DATA);
            })) {
                continue;
            }

            ItemStack stack = fix(content);
            if (stack == null) continue;

            inventory.setItem(i, stack);
        }

        ItemStack[] armor = new ItemStack[inventory.getArmorContents().length];

        for (int index = 0; index < inventory.getArmorContents().length; index++) {
            ItemStack content = inventory.getArmorContents()[index];

            if (content == null || content.getType() == Material.AIR) {
                armor[index] = content;
                continue;
            }
            if (NBT.get(content, (tag) -> {
                return tag.hasTag(UNFIXABLE_NBT_DATA);
            })) {
                continue;
            }

            ItemStack stack = fix(content);
            if (stack == null) {
                armor[index] = content;
                continue;
            }

            armor[index] = content;
            i++;
        }

        inventory.setArmorContents(armor);
        sender.sendMessage(CC.blend("[Fix] Successfully fixed " + i + " items in your inventory!", "&a", "&7"));
        if (!bypass) {
            user.setBalance(user.getBalance() - cost);
        }
    }

    public ItemStack fix(ItemStack stack) {
        if (stack.getItemMeta() instanceof Damageable damageable && !(stack.getItemMeta().isUnbreakable())) {
            damageable.setDamage(0);
            stack.setItemMeta(damageable);
            return stack;
        }
        return null;
    }

}
