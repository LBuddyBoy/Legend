package dev.lbuddyboy.legend.features.kitmap.kit.menu;

import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.IMenu;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ConversationBuilder;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.kitmap.kit.EditedKit;
import dev.lbuddyboy.legend.features.kitmap.kit.Kit;
import dev.lbuddyboy.legend.user.model.LegendUser;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project lPractice
 * @file dev.lbuddyboy.practice.kit.menu
 * @since 5/4/2024
 */

@AllArgsConstructor
public class EditKitMenu extends IMenu {

    private final Kit kit;

    @Override
    public String getTitle(Player player) {
        return "Editing: " + kit.getDisplayName();
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
    public Map<Integer, IButton> getButtons(Player player) {
        Map<Integer, IButton> buttons = new HashMap<>();
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());
        List<EditedKit> editedKits = user.getEditedKits(this.kit);
        int[] editSlots = new int[] {14};

        for (int i = 0; i < 1; i++) {
            if (editedKits.isEmpty()) {
                buttons.put(editSlots[i], new CreateButton());
                continue;
            }
            buttons.put(editSlots[i], new NameButton(editedKits.get(i)));
            buttons.put(editSlots[i] + 9, new ReNameButton(editedKits.get(i)));
            buttons.put(editSlots[i] + 18, new ContentsButton(editedKits.get(i)));
        }

        return buttons;
    }

    public class CreateButton extends IButton {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(Material.BOOK)
                    .displayName("&a&lClick to create an Edit")
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

            user.createEditedKit(kit, "Edited Kit #" + (user.getEditedKits(kit).size() + 1));
            updateMenu(player, true);
        }
    }

    @Override
    public void onClose(Player player) {
        super.onClose(player);
    }

    @AllArgsConstructor
    public class NameButton extends IButton {

        private EditedKit editedKit;

        @Override
        public ItemStack getItem(Player player) {
            int healthPots = ItemUtils.countStacksMatching(this.editedKit.getInventoryContents(), ItemUtils.INSTANT_HEAL_POTION_PREDICATE);
            return new ItemFactory(Material.ENCHANTED_BOOK)
                    .displayName(CC.blend(editedKit.getName(), "&6", "&e", true))
                    .lore(
                            CC.blend("Health Potions", "&d", "&7") + "&7: &f" + healthPots
                    )
                    .build();
        }

    }

    @AllArgsConstructor
    public class ReNameButton extends IButton {

        private EditedKit editedKit;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(Material.BIRCH_HANGING_SIGN)
                    .displayName(CC.blend("Rename", "&2", "&a") + " &7(Click)")
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            Conversation conversation = new ConversationBuilder(player)
                    .stringPrompt("&aType the name you'd like this edited kit to be. You can type 'cancel' to stop this process.", (context, response) -> {
                        if (!response.equalsIgnoreCase("cancel")) {
                            editedKit.setName(response);
                        }

                        openMenu(player);
                        return Prompt.END_OF_CONVERSATION;
                    })
                    .echo(false).build();

            player.beginConversation(conversation);
        }
    }

    @AllArgsConstructor
    public class ContentsButton extends IButton {

        private EditedKit editedKit;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(Material.CHEST)
                    .displayName(CC.blend("Edit Inventory", "&3", "&b") + " &7(Click)")
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            new EditInventoryMenu(this.editedKit).openMenu(player);
        }

    }

}
