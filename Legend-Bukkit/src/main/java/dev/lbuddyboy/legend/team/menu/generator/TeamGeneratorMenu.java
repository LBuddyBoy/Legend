package dev.lbuddyboy.legend.team.menu.generator;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.IMenu;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamMember;
import dev.lbuddyboy.legend.user.model.LegendUser;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class TeamGeneratorMenu extends IMenu {

    private final Team team;

    @Override
    public String getTitle(Player player) {
        return "Team Generator";
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

        buttons.put(5, new InfoButton());

        buttons.put(21, new InventoryButton());
        buttons.put(23, new TiersButton());
        buttons.put(25, new UpgradesButton());

        return buttons;
    }

    public class InfoButton extends IButton {

        @Override
        public ItemStack getItem(Player player) {
            TeamMember leader = team.getLeader().orElse(null);
            LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(leader.getUuid());

            return new ItemFactory(user.getHeadTextureValue())
                    .displayName(CC.blend(team.getName() + "'s Generator Info", "&3", "&b", "&l"))
                    .lore(
                            " ",
                            "&fNext Production&7: &d" + TimeUtils.formatIntoDetailedString(team.getGeneratorData().getNextProduction()),
                            "&fCurrent Tier&7: &e" + team.getGeneratorData().getTier() + "/3",
                            "&fCurrent XP&7: &e" + APIConstants.formatNumber(team.getGeneratorData().getExperience()) + "/" + APIConstants.formatNumber(team.getGeneratorData().getExperienceRequired()),
                            " "
                    )
                    .build();
        }
    }

    public class TiersButton extends IButton {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(Material.BUNDLE)
                    .displayName(CC.blend("Generator Tiers", "&6", "&e", "&l"))
                    .lore("&7&o(( Click to view the generator tiers ))")
                    .addItemFlags(ItemFlag.values())
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            new GeneratorTiersMenu(TeamGeneratorMenu.this).openMenu(player);
        }
    }

    public class InventoryButton extends IButton {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(Material.CHEST)
                    .displayName("<blend:&6;&e>&lItem Collection</>")
                    .lore("&7&o(( Click to view all " + team.getGeneratorData().getGeneratedItems().size() + " items ))")
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            new TeamGeneratorCollectMenu(team).openMenu(player);
        }
    }

    public class UpgradesButton extends IButton {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(Material.ANVIL)
                    .displayName("<blend:&6;&e>&lGenerator Upgrades</>")
                    .lore(
                            "&7&o(( Click to view all generator upgrades ))"
                    )
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            new TeamGeneratorUpgradeMenu(team).openMenu(player);
        }

    }

}
