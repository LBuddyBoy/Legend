package dev.lbuddyboy.legend.features.playtime.menu;

import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.IMenu;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.playtime.PlayTimeGoalHandler;
import dev.lbuddyboy.legend.features.playtime.model.PlayTimeGoal;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayTimeGoalMenu extends IPagedMenu {

    private final PlayTimeGoalHandler playTimeGoalHandler = LegendBukkit.getInstance().getPlayTimeGoalHandler();

    @Override
    public String getPageTitle(Player player) {
        return "PlayTime Goals";
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();

        for (PlayTimeGoal playTimeGoal : this.playTimeGoalHandler.getPlayTimeGoals().values()) {
            buttons.add(new PlayTimeGoalButton(playTimeGoal));
        }

        return buttons;
    }

    @Override
    public boolean autoUpdating(Player player) {
        return true;
    }

    @AllArgsConstructor
    public class PlayTimeGoalButton extends IButton {

        private final PlayTimeGoal playTimeGoal;

        @Override
        public ItemStack getItem(Player player) {
            List<String> lore = new ArrayList<>();

            lore.add(" ");
            if (playTimeGoal.isCompleted()) {
                lore.addAll(Arrays.asList(
                        "&fGoal: &e" + TimeUtils.formatIntoDetailedString(playTimeGoal.getPlayTimeGoal()),
                        "&fProgress: &a&lCOMPLETED"
                ));
            } else if (playTimeGoal.isExpired()) {
                lore.addAll(Arrays.asList(
                        "&fGoal: &e" + TimeUtils.formatIntoDetailedString(playTimeGoal.getPlayTimeGoal()),
                        "&fProgress: &c&lNOT REACHED"
                ));
            } else {
                lore.addAll(Arrays.asList(
                        "&fGoal: &a" + TimeUtils.formatIntoDetailedString(playTimeGoal.getPlayTimeGoal()),
                        "&fProgress: &e" + TimeUtils.formatIntoDetailedString(playTimeGoal.getProgress()),
                        "&fTime Left: &d" + TimeUtils.formatIntoDetailedString(playTimeGoal.getRemaining())
                ));
            }

            lore.addAll(Arrays.asList(
                    " ",
                    "&7The progress is added up by all the players",
                    "&7online playtimes combined.",
                    " "
            ));

            return new ItemFactory(playTimeGoal.getMaterialData().toItemStack(1))
                    .displayName(playTimeGoal.getReward())
                    .lore(lore)
                    .build();
        }

    }

}
