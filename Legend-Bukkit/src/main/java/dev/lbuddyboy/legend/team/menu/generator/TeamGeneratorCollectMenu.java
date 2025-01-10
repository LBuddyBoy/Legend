package dev.lbuddyboy.legend.team.menu.generator;

import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.IMenu;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.generator.GeneratorData;
import dev.lbuddyboy.legend.util.model.DocumentedItemStack;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class TeamGeneratorCollectMenu extends IPagedMenu {

    private static final int[] BUTTON_SLOTS = new int[] {
            10, 11, 12, 13, 14, 15, 16, 17, 18,
            19, 20, 21, 22, 23, 24, 25, 26, 27,
            28, 29, 30, 31, 32, 33, 34, 35, 36,
            37, 38, 39, 40, 41, 42, 43, 44, 45,
            46, 47, 48, 49, 50, 51, 52, 53, 54
    };

    private final Team team;

    @Override
    public String getPageTitle(Player player) {
        return "Generator Items";
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();
        GeneratorData data = this.team.getGeneratorData();

        for (int index = 0; index < data.getGeneratedItems().size(); index++) {
            buttons.add(new ClaimButton(index, data.getGeneratedItems().get(index)));
        }

        return buttons;
    }

    @Override
    public int getSize(Player player) {
        return 54;
    }

    @Override
    public IMenu fallbackMenu(Player player) {
        return new TeamGeneratorMenu(this.team);
    }

    @Override
    public Map<Integer, IButton> getGlobalButtons(Player player) {
        Map<Integer, IButton> buttons = new HashMap<>();

        buttons.put(5, new ClaimAllButton());

        return buttons;
    }

    @Override
    public boolean fillerItems(Player player) {
        return false;
    }

    @Override
    public int getPreviousButtonSlot() {
        return 1;
    }

    @Override
    public int getNextPageButtonSlot() {
        return 9;
    }

    @Override
    public int[] getButtonSlots() {
        return BUTTON_SLOTS;
    }

    @Override
    public int getMaxPageButtons() {
        return BUTTON_SLOTS.length;
    }

    public class ClaimAllButton extends IButton {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(Material.BEACON).displayName("<blend:&6;&e>&lCLAIM ALL</>").build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            GeneratorData data = team.getGeneratorData();

            if (data.getGeneratedItems().isEmpty()) {
                player.sendMessage(CC.translate("&cYou have no items to claim."));
                return;
            }

            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(CC.translate("&cYour inventory is full."));
                return;
            }

            int amount = 0;

            for (ItemStack content : player.getInventory().getStorageContents()) {
                if (content == null || content.getType() == Material.AIR) amount++;
            }

            for (int i = 0; i < amount; i++) {
                if (data.getGeneratedItems().isEmpty()) break;

                player.getInventory().addItem(data.getGeneratedItems().getFirst().getItemStack());
                data.getGeneratedItems().removeFirst();
            }

            updateMenu(player, true);
        }
    }

    @AllArgsConstructor
    public class ClaimButton extends IButton {

        private final int index;
        private final DocumentedItemStack item;

        @Override
        public ItemStack getItem(Player player) {
            return this.item.getItemStack();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(CC.translate("&cYour inventory is full."));
                return;
            }

            ItemUtils.tryFit(player, this.item.getItemStack(), false);
            team.getGeneratorData().getGeneratedItems().remove(this.index);
            updateMenu(player, true);
        }

    }

}
