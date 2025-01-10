package dev.lbuddyboy.legend.features.crystals.menu;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.IMenu;
import dev.lbuddyboy.commons.menu.button.FillButton;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.crystals.model.CrystalShopItem;
import dev.lbuddyboy.legend.user.model.LegendUser;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrystalShopMenu extends IPagedMenu {

    private static final int[] ROTATION_SLOTS = new int[]{
            11,
            20,
            29
    };

    private static final int[] NORMAL_SLOTS = new int[]{
            13, 14, 15, 16, 17,
            22, 23, 24, 25, 26,
            31, 32, 33, 34, 35
    };

    @Override
    public String getPageTitle(Player player) {
        return "Crystal Shop";
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();

        for (CrystalShopItem item : LegendBukkit.getInstance().getCrystalHandler().getLootTable().getSortedItems()) {
            if (item.isRotation()) continue;

            buttons.add(new ItemButton(item));
        }

        return buttons;
    }

    @Override
    public int getSize(Player player) {
        return 45;
    }

    @Override
    public boolean autoFills(Player player) {
        return true;
    }

    @Override
    public boolean autoUpdating(Player player) {
        return true;
    }

    @Override
    public int[] getButtonSlots() {
        return NORMAL_SLOTS;
    }

    @Override
    public int getMaxPageButtons() {
        return NORMAL_SLOTS.length;
    }

    @Override
    public int getNextPageButtonSlot() {
        return 8;
    }

    @Override
    public int getPreviousButtonSlot() {
        return 4;
    }

    @Override
    public boolean fillerItems(Player player) {
        return false;
    }

    @Override
    public Map<Integer, IButton> getGlobalButtons(Player player) {
        Map<Integer, IButton> buttons = new HashMap<>();

        buttons.put(19, new FillButton('f', new ItemFactory(Material.CLOCK).displayName("<blend:&6;&e>Rotating Items</>")
                .lore("&fRotating in&7: &a" + TimeUtils.formatIntoDetailedString(LegendBukkit.getInstance().getCrystalHandler().getNextRotation()))
                .build()));

        int rotationIndex = 0;

        for (CrystalShopItem item : LegendBukkit.getInstance().getCrystalHandler().getLootTable().getSortedItems()) {
            if (!item.isActive()) continue;

            buttons.put(ROTATION_SLOTS[rotationIndex++], new ItemButton(item));
        }

        return buttons;
    }

    @AllArgsConstructor
    public class ItemButton extends IButton {

        private final CrystalShopItem item;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(this.item.getItem().clone())
                    .displayName(ItemUtils.getName(this.item.getItem()) + " &8[&b" + APIConstants.formatNumber(this.item.getPrice()) + " Crystals&8]")
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

            if (user.getCrystals() < this.item.getPrice()) {
                player.sendMessage(CC.translate("&cYou do not have enough crystals to purchase this."));
                return;
            }

            item.reward(player);
            user.setCrystals(user.getCrystals() - this.item.getPrice());
        }
    }

}
