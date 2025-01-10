package dev.lbuddyboy.legend.features.crystals.model;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import dev.lbuddyboy.commons.loottable.AbstractLootTable;
import dev.lbuddyboy.commons.util.CC;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Objects;

public class CrystalShopLootTable extends AbstractLootTable<CrystalShopItem> {

    public CrystalShopLootTable() {
        super("crystal-shop", "crystalShopItems", CrystalShopItem.class);
    }

    @Override
    public void loadItem(String s, ConfigurationSection configurationSection) {
        this.items.put(s, new CrystalShopItem(configurationSection));
    }

    @Override
    public CrystalShopItem addItem(String s, ItemStack itemStack) {
        CrystalShopItem crystalShopItem = new CrystalShopItem(this.name, s, itemStack);

        this.items.put(crystalShopItem.getId(), crystalShopItem);

        return crystalShopItem;
    }

    @Override
    public CrystalShopItem getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        Player sender = context.getPlayer();
        String source = context.popFirstArg();

        for (CrystalShopItem item : this.items.values()) {
            if (Objects.equals(item.getId(), source)) return item;
        }

        throw new InvalidCommandArgument(CC.translate("&cInvalid crystal shop item id."));
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        return this.items.keySet();
    }
}
