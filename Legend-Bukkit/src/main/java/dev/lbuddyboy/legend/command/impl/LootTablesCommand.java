package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.loottable.AbstractItem;
import dev.lbuddyboy.commons.loottable.AbstractLootTable;
import dev.lbuddyboy.commons.loottable.menu.LootTablePreviewMenu;
import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.IMenu;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.legend.team.menu.generator.GeneratorTiersMenu;
import lombok.AllArgsConstructor;
import net.minecraft.world.level.storage.loot.LootTable;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CommandAlias("loottables|ltables|loot")
public class LootTablesCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        new LootTablesMenu().openMenu(sender);
    }

    public class LootTablesMenu extends IMenu {

        @Override
        public String getTitle(Player player) {
            return "Loot Tables";
        }

        @Override
        public int getSize(Player player) {
            return 27;
        }

        @Override
        public Map<Integer, IButton> getButtons(Player player) {
            Map<Integer, IButton> buttons = new HashMap<>();
            List<Integer> slots = getCenteredSlots(player);

            buttons.put(slots.get(0), new LootTableButton(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2IyMjQ0NDRiNDQ3Y2I1MmVhNjE3NjdmYTViODU0ODc0YzQ5NmNlMTNlMzk5MTI1MTNiMzg4ZDY3OTQ4NWQwIn19fQ==",
                    "<blend:&5;&d>&lCitadel</>",
                    "CITADEL"
            ));

            buttons.put(slots.get(1), new IButton() {

                @Override
                public ItemStack getItem(Player player) {
                    return new ItemFactory("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWI0MjVhYTNkOTQ2MThhODdkYWM5Yzk0ZjM3N2FmNmNhNDk4NGMwNzU3OTY3NGZhZDkxN2Y2MDJiN2JmMjM1In19fQ==")
                            .displayName("<blend:&6;&e>&lGenerators</>&7 (Click)")
                            .build();
                }

                @Override
                public void action(Player player, ClickType clickType, int slot) {
                    new GeneratorTiersMenu(LootTablesMenu.this).openMenu(player);
                }

            });

            buttons.put(slots.get(2), new LootTableButton(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I3NjMwMmVkNmRiYjk2ZTA0NGQxNTZlMTk5YjliZGNmNDY4NjA2NWI4OTYxNjgwMjU2MjdkZDVmNjg2ZWFjNCJ9fX0=",
                    "<blend:&3;&b>&lAbility Barrels</>",
                    "ability-box"
            ));

            buttons.put(slots.get(3), new LootTableButton(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWUyNWRiZTQ3NjY3ZDBjZTIzMWJhYTIyM2RlZTk1M2JiZmM5Njk2MDk3Mjc5ZDcyMzcwM2QyY2MzMzk3NjQ5ZSJ9fX0=",
                    "<blend:&3;&b>&lLoot Hill</>",
                    "loothill"
            ));

            buttons.put(slots.get(4), new LootTableButton(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGUxYzJhNWM0ZDRjMzVmYzM3NTQyOTVmMzljMzMzZWY2NzhiZDJlZGFkOWM4OGYyZDE2ODBmMTQ5NjgyIn19fQ==",
                    "<blend:&6;&e>&lRoll Tickets</>",
                    "slots"
            ));

            buttons.put(slots.get(5), new LootTableButton(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjVmNmVmYjM3NmVlYjY4ZDgxNDQ5ZjQ1NDMwOTMwYWY2MDg5Mzk1NTdjNjNlNTgxOTJkNzg3MjIzYmQyZDQ5ZiJ9fX0=",
                    "<blend:&6;&e>&lChat Games</>",
                    "chatgames"
            ));

            buttons.put(slots.get(6), new LootTableButton(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTAwNTYwZWU4ZDQyNGNmMDhjZTVlODMwNGJjNjk2Y2I3NzE3ZWY5Yjk5NDMzZTkyY2EwMmNmOTQ0ZjE4ZTAxMyJ9fX0=",
                    "<blend:&4;&c>&lMini Games</>",
                    "minigames"
            ));

            return buttons;
        }
    }

    @AllArgsConstructor
    public class LootTableButton extends IButton {

        private final String headTexture, displayName, lootTableName;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(this.headTexture)
                    .displayName(this.displayName + " &7(Click)")
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            AbstractLootTable lootTable = CommonsPlugin.getInstance().getLootTableHandler().getLootTables().get(this.lootTableName);

            if (lootTable == null) {
                player.sendMessage(CC.translate("<blend:&4;&c>Error finding this loottable.</>"));
                return;
            }

            new LootTablePreviewMenu(lootTable).openMenu(player);
        }
    }

}
