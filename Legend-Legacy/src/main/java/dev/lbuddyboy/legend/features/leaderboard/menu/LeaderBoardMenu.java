package dev.lbuddyboy.samurai.map.leaderboard.menu;

import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.button.FillButton;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.leaderboard.ILeaderBoardStat;
import dev.lbuddyboy.samurai.map.leaderboard.LeaderBoardUser;
import dev.lbuddyboy.samurai.map.leaderboard.impl.KillLeaderBoardStat;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderBoardMenu extends IPagedMenu {

    private static final int[] USER_SLOTS = new int[] {
            31, 32, 33,
            38, 39, 40, 41, 42, 43, 44
    };

    private static final int[] TYPE_SLOTS = new int[] {
            11, 12, 13, 14, 15, 16, 17
    };

    private ILeaderBoardStat type = Samurai.getInstance().getLeaderBoardHandler().getStatByClass(KillLeaderBoardStat.class).get();

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

        for (ILeaderBoardStat stat : Samurai.getInstance().getLeaderBoardHandler().getLeaderBoardStats()) {
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
        List<LeaderBoardUser> users = Samurai.getInstance().getLeaderBoardHandler().getLeaderBoard(this.type).values().stream().toList();

        for (int slot : USER_SLOTS) {
            if (users.size() <= index) {
                buttons.put(slot, new FillButton('f', new ItemFactory(Material.SKELETON_SKULL).displayName("&cN/A").build()));
                continue;
            }

            buttons.put(slot, new UserButton(users.get(index)));
            index++;
        }

        for (LeaderBoardUser user : Samurai.getInstance().getLeaderBoardHandler().getLeaderBoard(this.type).values()) {
            buttons.put(USER_SLOTS[user.getPlace() - 1], new UserButton(user));
        }

        return buttons;
    }

    @AllArgsConstructor
    public class UserButton extends IButton {

        private LeaderBoardUser user;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(user.getTexture())
                    .displayName(CC.blend("#" + user.getPlace() + ") " + user.getName(), type.getPrimaryColor(), type.getSecondaryColor()))
                    .lore(
                            " ",
                            CC.blend("Place: #" + user.getPlace(), type.getPrimaryColor(), type.getSecondaryColor()),
                            CC.blend(type.getValueName() + ": " + type.format(user.getScore()), type.getPrimaryColor(), type.getSecondaryColor()),
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
            updateMenu(player, true);
        }
    }

}