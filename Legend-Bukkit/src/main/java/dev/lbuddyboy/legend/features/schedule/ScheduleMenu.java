package dev.lbuddyboy.legend.features.schedule;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.IMenu;
import dev.lbuddyboy.commons.menu.button.FillButton;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.legend.LegendBukkit;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ScheduleMenu extends IMenu {

    @Override
    public String getTitle(Player player) {
        return "Server Schedule";
    }

    @Override
    public int getSize(Player player) {
        return 27;
    }

    @Override
    public Map<Integer, IButton> getButtons(Player player) {
        Map<Integer, IButton> buttons = new HashMap<>();
        ScheduleEntry nextEntry = LegendBukkit.getInstance().getScheduleHandler().getNextEvent(player);

        ItemFactory timeFactory = new ItemFactory(Material.CLOCK)
                .displayName("<blend:&7;&f>It is currently: " + APIConstants.SDF.format(APIConstants.getCalendar().getTime()) + "</> &7[" + APIConstants.getCalendar().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, APIConstants.LOCALE) + "&7]");

        if (nextEntry != null) {
            timeFactory.lore("&fUp Next&7: " + nextEntry.getDisplayName() + " &fat &7" + APIConstants.SDF.format(nextEntry.getDate()));
        }

        buttons.put(5, new FillButton('i', timeFactory.build()));

        buttons.put(11, new DayButton(2, "<blend:&f;&7>&lMonday</>", Material.WHITE_CANDLE));
        buttons.put(12, new DayButton(3, "<blend:&8;&7>&lTuesday</>", Material.GRAY_CANDLE));
        buttons.put(13, new DayButton(4, "<blend:&4;&c>&lWednesday</>", Material.RED_CANDLE));
        buttons.put(14, new DayButton(5, "<blend:&e;&f>&lThursday</>", Material.YELLOW_CANDLE));
        buttons.put(15, new DayButton(6, "<blend:&2;&a>&lFriday</>", Material.GREEN_CANDLE));
        buttons.put(16, new DayButton(7, "<blend:&5;&d>&lSaturday</>", Material.PURPLE_CANDLE));
        buttons.put(17, new DayButton(1, "<blend:&6;&e>&lSunday</>", Material.ORANGE_CANDLE));

        return buttons;
    }

    @Override
    public boolean autoFills(Player player) {
        return true;
    }

    @AllArgsConstructor
    public class DayButton extends IButton {

        private int dayOfTheWeek;
        private String displayName;
        private Material material;

        @Override
        public ItemStack getItem(Player player) {
            List<String> lore = new ArrayList<>();
            List<ScheduleEntry> entries = LegendBukkit.getInstance().getScheduleHandler().getScheduleEntries(this.dayOfTheWeek, player);

            lore.add(" ");

            if (!entries.isEmpty()) {
                for (ScheduleEntry entry : entries) {
                    lore.add("&7» " + entry.getDisplayName() + " &fat &7" + APIConstants.SDF.format(entry.getDate()));
                }
            } else {
                lore.add("<blend:&7;&f>Nothing is scheduled on this day...</>");
            }

            lore.add(" ");

            ItemFactory factory = new ItemFactory(this.material)
                    .displayName(this.displayName)
                    .lore(lore);

            if (APIConstants.getCalendar().get(Calendar.DAY_OF_WEEK) == this.dayOfTheWeek) {
                factory.enchant(Enchantment.UNBREAKING, 10);
                factory.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            return factory.build();
        }
    }

}
