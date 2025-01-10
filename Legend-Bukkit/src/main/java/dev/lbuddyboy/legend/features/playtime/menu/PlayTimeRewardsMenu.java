package dev.lbuddyboy.legend.features.playtime.menu;

import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.playtime.model.PlayTimeReward;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayTimeRewardsMenu extends IPagedMenu {

    @Override
    public String getPageTitle(Player player) {
        return "PlayTime Rewards";
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();

        for (PlayTimeReward reward : LegendBukkit.getInstance().getPlayTimeGoalHandler().getRewards()) {
            buttons.add(new RewardButton(reward));
        }

        return buttons;
    }

    @AllArgsConstructor
    public class RewardButton extends IButton {

        private final PlayTimeReward reward;

        @Override
        public ItemStack getItem(Player player) {
            Material displayMaterial = Material.getMaterial(reward.getDisplayMaterial());
            ItemFactory factory = new ItemFactory(displayMaterial);
            boolean claimed = this.reward.isClaimed(player.getUniqueId());
            List<String> lore = new ArrayList<>();

            if (displayMaterial == Material.PLAYER_HEAD) factory = new ItemFactory(reward.getDisplayTexture());
            if (claimed) factory = new ItemFactory("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWZkMjQwMDAwMmFkOWZiYmJkMDA2Njk0MWViNWIxYTM4NGFiOWIwZTQ4YTE3OGVlOTZlNGQxMjlhNTIwODY1NCJ9fX0=");

            lore.add(" ");
            lore.add("&fRequired Play Time&7: &d" + this.reward.getRequiredPlayTime().fancy());
            lore.add(" ");
            lore.add("<blend:&6;&e>&lRewards</>");
            lore.addAll(this.reward.getRewards());
            lore.addAll(Arrays.asList(
                    "",
                    "&7&o(( Click to claim this playtime reward ))",
                    ""
            ));

            return factory
                    .displayName(reward.getDisplayName())
                    .lore(lore)
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            if (this.reward.isClaimed(player.getUniqueId())) {
                player.sendMessage(CC.translate("<blend:&4;&c>You already claimed this reward.</>"));
                return;
            }

            if (!this.reward.isAvailable(player.getUniqueId())) {
                player.sendMessage(CC.translate("<blend:&4;&c>You do not meet the playtime requirements for this reward.</>"));
                return;
            }

            this.reward.claim(player);
            updateMenu(player, true);
        }
    }

}
