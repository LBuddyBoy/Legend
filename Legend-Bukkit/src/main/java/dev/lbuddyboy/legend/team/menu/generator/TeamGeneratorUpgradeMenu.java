package dev.lbuddyboy.legend.team.menu.generator;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.IMenu;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.generator.GeneratorData;
import dev.lbuddyboy.legend.team.model.generator.upgrades.AbstractGeneratorUpgrade;
import dev.lbuddyboy.legend.user.model.LegendUser;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class TeamGeneratorUpgradeMenu extends IMenu {

    private final Team team;

    @Override
    public String getTitle(Player player) {
        return "Generator Upgrades";
    }

    @Override
    public int getSize(Player player) {
        return 27;
    }

    @Override
    public Map<Integer, IButton> getButtons(Player player) {
        return new HashMap<>() {{
            int index = 13;
            for (AbstractGeneratorUpgrade upgrade : LegendBukkit.getInstance().getTeamHandler().getGeneratorUpgrades()) {
                put(index++, new UpgradeButton(upgrade));
            }
        }};
    }

    @Override
    public IMenu fallbackMenu(Player player) {
        return new TeamGeneratorMenu(this.team);
    }

    @AllArgsConstructor
    public class UpgradeButton extends IButton {

        private final AbstractGeneratorUpgrade upgrade;

        @Override
        public ItemStack getItem(Player player) {
            List<String> lore = new ArrayList<>();
            GeneratorData data = team.getGeneratorData();
            int level = data.getGeneratorUpgrades().getOrDefault(this.upgrade.getId(), 0);
            double nextCost = this.upgrade.getInitialCost() + (this.upgrade.getCostPerLevel() * level);

            lore.add(" ");

            for (String line : this.upgrade.getDescription()) {
                lore.add("&7" + line);
            }

            lore.add(" ");

            lore.add("&fLevel&7: " + this.upgrade.getSecondaryColor() + data.getGeneratorUpgrades().getOrDefault(this.upgrade.getId(), 0) + "/" + this.upgrade.getMaxLevel());
            if (level < this.upgrade.getMaxLevel()) {
                lore.add("&fNext Level Cost&7: " + this.upgrade.getSecondaryColor() + APIConstants.formatNumber(nextCost) + " crystals");
            }
            lore.add(" ");
            lore.add("&7&o(( Click to purchase the next upgrade level ))");
            lore.add(" ");

            return new ItemFactory(this.upgrade.getDisplayMaterial())
                    .displayName(this.upgrade.getDisplayName() + " &8[Lvl " + level + "]")
                    .lore(lore)
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            GeneratorData data = team.getGeneratorData();
            int level = data.getGeneratorUpgrades().getOrDefault(this.upgrade.getId(), 0);
            double nextCost = this.upgrade.getInitialCost() + (this.upgrade.getCostPerLevel() * level);
            LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

            if (level >= this.upgrade.getMaxLevel()) {
                player.sendMessage(CC.translate("<blend:&4;&c>That generator upgrade is already max level.</>"));
                return;
            }

            if (user.getCrystals() < nextCost) {
                player.sendMessage(CC.translate("<blend:&4;&c>You need " + APIConstants.formatNumber(nextCost) + " crystals to upgrade the generator.</>"));
                return;
            }

            user.setCrystals(user.getCrystals() - nextCost);
            data.getGeneratorUpgrades().put(this.upgrade.getId(), level + 1);
            team.flagSave();
            team.sendMessage("<blend:&6;&e>&lGENERATORS</> &7» &a" + player.getName() + " has upgraded " + this.upgrade.getDisplayName() + "&a to level " + (level + 1) + "!");
            updateMenu(player, true);
        }
    }

}
