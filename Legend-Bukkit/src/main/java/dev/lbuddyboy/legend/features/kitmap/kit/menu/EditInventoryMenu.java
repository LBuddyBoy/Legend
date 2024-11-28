package dev.lbuddyboy.legend.features.kitmap.kit.menu;

import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.IMenu;
import dev.lbuddyboy.commons.menu.button.FillButton;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.kitmap.kit.EditedKit;
import dev.lbuddyboy.legend.features.kitmap.kit.Kit;
import dev.lbuddyboy.legend.user.model.LegendUser;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project lPractice
 * @file dev.lbuddyboy.practice.kit.menu
 * @since 5/4/2024
 */

@AllArgsConstructor
public class EditInventoryMenu extends IMenu {

    private final EditedKit editedKit;

    @Override
    public String getTitle(Player player) {
        return "Editing: " + editedKit.getName();
    }

    @Override
    public int getSize(Player player) {
        return 54;
    }

    @Override
    public Map<Integer, IButton> getButtons(Player player) {
        Map<Integer, IButton> buttons = new HashMap<>();
        Kit kit = editedKit.getKit();
        int deleteSlot = 7, resetSlot = 8, fillInvSlot = 9, backSlot = 9, editorItemsStart = 19;
        int[] armorSlots = new int[]{4, 3, 2, 1};
        int[] glassSlots = new int[]{5, 10, 11, 12, 13 ,14 ,15 ,16, 17, 18, 46, 47, 48, 49, 50 , 51, 52, 53, 54};


        int index = -1;
        for (ItemStack content : kit.getArmor()) {
            index++;
            if (content == null || content.getType() == Material.AIR) continue;

            buttons.put(armorSlots[index], new ArmorButton(content));
        }

        for (int slot = editorItemsStart; slot <= 45; slot++) {
            ItemStack item = kit.getEditorItems()[slot - editorItemsStart];
            if (item == null || item.getType() == Material.AIR) continue;

            buttons.put(slot, new EditorItemButton(item));
        }

        if (!LegendBukkit.getInstance().getSettings().getBoolean("server.uhc-mode", false)) {
            buttons.put(fillInvSlot, new FillInventoryButton());
        } else {
            deleteSlot++;
            resetSlot++;
        }

        buttons.put(deleteSlot, new DeleteButton());
        buttons.put(resetSlot, new LoadDefaultButton());

        for (int slot : glassSlots) {
            buttons.put(slot, new FillButton('g', new ItemFactory(Material.BLACK_STAINED_GLASS_PANE).displayName(" ").build()));
        }

        return buttons;
    }

    @Override
    public boolean allowBottomInventoryClicks() {
        return true;
    }

    @Override
    public void openMenu(Player player) {
        super.openMenu(player);

        player.setMetadata("previous_editor_inventory", new FixedMetadataValue(LegendBukkit.getInstance(), player.getInventory().getContents().clone()));
        player.getInventory().setStorageContents(editedKit.getInventoryContents());
    }

    @Override
    public void onClose(Player player) {
        super.onClose(player);

        editedKit.setInventoryContents(player.getInventory().getStorageContents().clone());
        player.getInventory().setContents((ItemStack[]) player.getMetadata("previous_editor_inventory").get(0).value());
        player.removeMetadata("previous_editor_inventory", LegendBukkit.getInstance());
    }

    @Override
    public IMenu fallbackMenu(Player player) {
        return new EditKitMenu(editedKit.getKit());
    }

    @AllArgsConstructor
    public class EditorItemButton extends IButton {

        private ItemStack stack;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(stack.clone())
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            ItemStack cursor = player.getItemOnCursor().clone();

            if (cursor.getType() != Material.AIR && cursor.isSimilar(this.stack)) {
                cursor.setAmount(cursor.getAmount() + 1);
            } else {
                cursor = this.stack;
            }

            player.setItemOnCursor(cursor);
        }

    }

    @AllArgsConstructor
    public class ArmorButton extends IButton {

        private ItemStack stack;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(stack.clone())
                    .lore("&eYou cannot edit this item.")
                    .build();
        }

    }

    @AllArgsConstructor
    public class DeleteButton extends IButton {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(Material.RED_DYE)
                    .displayName(CC.blend("Delete Kit", "&4", "&c", true) + " &7(Click)")
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

            user.removeEditedKit(editedKit.getKit(), editedKit);
            new EditKitMenu(editedKit.getKit()).openMenu(player);
        }
    }

    @AllArgsConstructor
    public class LoadDefaultButton extends IButton {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(Material.YELLOW_DYE)
                    .displayName(CC.blend("Load Default Kit", "&6", "&e", true) + " &7(Click)")
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            player.getInventory().setStorageContents(editedKit.getKit().getContents());
        }

    }

    @AllArgsConstructor
    public class FillInventoryButton extends IButton {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(Material.SPLASH_POTION)
                    .potionEffect(PotionType.STRONG_HEALING)
                    .addItemFlags(ItemFlag.values())
                    .displayName(CC.blend("Fill Inventory", "&6", "&e", true) + " &7(Click)")
                    .lore("&7Click to fill your inventory with health potions!")
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            for (int i = 0; i < 36; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item == null || item.getType() == Material.AIR) {
                    player.getInventory().setItem(i, new ItemFactory(Material.SPLASH_POTION).potionEffect(PotionType.STRONG_HEALING).build());
                }
            }
        }

    }

}
