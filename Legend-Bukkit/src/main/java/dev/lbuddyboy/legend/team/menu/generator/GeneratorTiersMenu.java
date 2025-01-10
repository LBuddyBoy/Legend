package dev.lbuddyboy.legend.team.menu.generator;

import dev.lbuddyboy.commons.loottable.menu.LootTablePreviewMenu;
import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.IMenu;
import dev.lbuddyboy.commons.menu.button.ButtonModel;
import dev.lbuddyboy.commons.menu.button.FillButton;
import dev.lbuddyboy.commons.menu.settings.GUISettings;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.config.TeamConfig;
import dev.lbuddyboy.legend.team.model.generator.GeneratorTier;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GeneratorTiersMenu extends IMenu {

    private final GUISettings settings = TeamConfig.TIERS_MENU_SETTINGS.getMenuSettings();

    private final IMenu backMenu;

    public GeneratorTiersMenu(IMenu backMenu) {
        this.backMenu = backMenu;
    }

    @Override
    public String getTitle(Player player) {
        return this.settings.getTitle();
    }

    @Override
    public int getSize(Player player) {
        return this.settings.getSize();
    }

    @Override
    public boolean autoFills(Player player) {
        return this.settings.isAutoFill();
    }

    @Override
    public ItemStack getAutoFillItem(Player player) {
        return this.settings.getAutoFillItem();
    }

    @Override
    public Map<Integer, IButton> getButtons(Player player) {
        Map<Integer, IButton> buttons = new HashMap<>();
        ButtonModel tierModel = TeamConfig.TIER_BUTTON.getButtonModel();

        for (GeneratorTier tier : LegendBukkit.getInstance().getTeamHandler().getSortedTiers()) {
            buttons.put(tier.getMenuSlot(), new TierButton(tierModel, tier));
        }

        this.settings.getFillerItems().forEach((slot, i) -> buttons.put(slot, new FillButton('f', i)));

        return buttons;
    }

    @Override
    public IMenu fallbackMenu(Player player) {
        return this.backMenu;
    }

    @AllArgsConstructor
    public class TierButton extends IButton {

        private final ButtonModel model;
        private final GeneratorTier tier;

        @Override
        public ItemStack getItem(Player player) {
            return this.model.createItem(
                    tier.getPlaceHolders()
            );
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            new LootTablePreviewMenu(this.tier.getLootTable()).openMenu(player);
        }
    }

}
