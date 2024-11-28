package dev.lbuddyboy.legend.settings;

import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.IMenu;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project LifeSteal
 * @file dev.lbuddyboy.samurai.chat.settings
 * @since 1/18/2024
 */
public class SettingsMenu extends IMenu {

    @Override
    public String getTitle(Player player) {
        return "Your Settings";
    }

    @Override
    public int getSize(Player player) {
        return 36;
    }

    @Override
    public boolean autoFills(Player player) {
        return true;
    }

    @Override
    public Map<Integer, IButton> getButtons(Player player) {
        Map<Integer, IButton> buttons = new HashMap<>();
        List<Integer> slots = getCenteredSlots(player);

        int i = 0;
        for (Setting setting : Setting.values()) {
            buttons.put(slots.get(i++), new SettingButton(setting));
        }

        return buttons;
    }

    @AllArgsConstructor
    public class SettingButton extends IButton {

        private final Setting setting;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(this.setting.getDisplayMaterial())
                    .displayName(CC.blend(this.setting.getName(), this.setting.getPrimaryColor(), this.setting.getSecondaryColor()))
                    .lore(this.setting.getDescription())
                    .addToLore(
                            " ",
                            "&fStatus&7: " + (this.setting.isToggled(player.getUniqueId()) ? "&a&lON" : "&c&lOFF"),
                            " "
                    )
                    .addItemFlags(ItemFlag.values())
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            this.setting.toggle(player.getUniqueId());

            updateMenu(player, false);
        }
    }

}
