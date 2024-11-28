package dev.lbuddyboy.legend.features.leaderboard.menu;

import dev.lbuddyboy.api.leaderboard.model.LeaderboardDataEntry;
import dev.lbuddyboy.arrow.Arrow;
import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.button.FillButton;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.leaderboard.ILeaderBoardStat;
import dev.lbuddyboy.legend.features.leaderboard.impl.KillLeaderBoardStat;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class LeaderBoardMenu extends IPagedMenu {

    private static final int[] USER_SLOTS = new int[] {
            31, 32, 33,
            38, 39, 40, 41, 42, 43, 44
    };

    private static final int[] TYPE_SLOTS = new int[] {
            11, 12, 13, 14, 15, 16, 17
    };

    private ILeaderBoardStat type = LegendBukkit.getInstance().getLeaderBoardHandler().getStatByClass(KillLeaderBoardStat.class);

    @Override
    public String getPageTitle(Player player) {
        return "Viewing: Top " + type.getValueName();
    }

    @Override
    public int getSize(Player player) {
        return 54;
    }

    @Override
    public boolean autoFills(Player player) {
        return true;
    }

    @Override
    public int[] getButtonSlots() {
        return TYPE_SLOTS;
    }

    @Override
    public int getMaxPageButtons() {
        return TYPE_SLOTS.length;
    }

    @Override
    public int getNextPageButtonSlot() {
        return 9;
    }

    @Override
    public int getPreviousButtonSlot() {
        return 1;
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();

        for (ILeaderBoardStat stat : LegendBukkit.getInstance().getLeaderBoardHandler().getLeaderBoardStats()) {
            if (stat == this.type) {
                buttons.add(new DefaultButton());
            } else {
                buttons.add(new TypeButton(stat));
            }
        }

        return buttons;
    }

    @Override
    public boolean fillerItems(Player player) {
        return false;
    }

    @Override
    public Map<Integer, IButton> getGlobalButtons(Player player) {
        Map<Integer, IButton> buttons = new HashMap<>();

        int index = 0;
        LeaderboardDataEntry leaderboardDataEntry = LegendBukkit.getInstance().getDataEntry(this.type.getId());
        List<Map.Entry<UUID, Integer>> users = leaderboardDataEntry.getLeaderBoards().entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .limit(10)
                .toList();

        for (int slot : USER_SLOTS) {
            if (users.size() <= index) {
                buttons.put(slot, new FillButton('f', new ItemFactory(Material.PLAYER_HEAD).displayName("&cN/A").build()));
                continue;
            }

            Map.Entry<UUID, Integer> playerEntry = users.get(index);

            buttons.put(slot, new UserButton(playerEntry.getKey(), playerEntry.getValue(), ++index));
        }

        return buttons;
    }

    @AllArgsConstructor
    public class UserButton extends IButton {

        private UUID playerUUID;
        private int value, place;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(this.playerUUID)
                    .displayName(CC.blend("#" + this.place + ") " + Arrow.getInstance().getUUIDCache().getName(this.playerUUID), type.getPrimaryColor(), type.getSecondaryColor()))
                    .lore(
                            " ",
                            CC.blend("Place: #" + this.place, type.getPrimaryColor(), type.getSecondaryColor()),
                            CC.blend(type.getValueName() + ": " + type.format((double) this.value), type.getPrimaryColor(), type.getSecondaryColor()),
                            " "
                    )
                    .build();
        }
    }

    public class DefaultButton extends IButton {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(Material.BARRIER).displayName(CC.blend("Currently Selected: " + type.getValueName(), "&a", "&f")).build();
        }
    }

    @AllArgsConstructor
    public class TypeButton extends IButton {

        private ILeaderBoardStat type;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(type.getMenuItem().clone()).build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            LeaderBoardMenu.this.type = type;
            openMenu(player);
        }
    }

}