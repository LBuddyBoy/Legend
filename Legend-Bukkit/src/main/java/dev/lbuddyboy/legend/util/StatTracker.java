package dev.lbuddyboy.legend.util;

import de.tr7zw.nbtapi.NBT;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LangConfig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class StatTracker {

    private static final String STAT_HEADER_NBT = "STAT_HEADER";

    public static ItemStack updateLore(ItemStack stack, Player victim, Player killer) {
        boolean header = NBT.get(stack, (tag) -> {
            return tag.hasTag(STAT_HEADER_NBT);
        });

        if (!header) {
            NBT.modify(stack, (tag) -> {
                tag.setBoolean(STAT_HEADER_NBT, true);
            });
        }

        ItemFactory factory = new ItemFactory(stack);

        if (!header) {
            factory.addToLore(" ");
            factory.addToLore(LangConfig.STAT_TRACKER_HEADER.getString());
        }

        if (killer == null) {
            factory.addToLore("&4" + victim.getName() + "&c died");
        } else {
            factory.addToLore("&4" + victim.getName() + "&c was slain by &4" + killer.getName());
        }

        return factory.build();
    }

    public static boolean shouldBeKillTracked(ItemStack item) {
        return item != null && (
                item.getType().name().endsWith("_BOW") ||
                item.getType().name().endsWith("_SWORD") ||
                item.getType().name().equalsIgnoreCase("CROSSBOW") ||
                item.getType().name().equalsIgnoreCase("TRIDENT") ||
                item.getType().name().endsWith("_AXE")
                );
    }

    public static boolean shouldBeDeathTracked(ItemStack item) {
        return item != null && (
                item.getType().name().endsWith("_HELMET") ||
                item.getType().name().endsWith("_CHESTPLATE") ||
                item.getType().name().endsWith("_LEGGINGS") ||
                item.getType().name().endsWith("_BOOTS")
                );
    }

}
