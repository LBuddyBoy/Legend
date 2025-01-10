package dev.lbuddyboy.legend.team.model.generator;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import dev.lbuddyboy.commons.loottable.AbstractLootTable;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.features.crystals.model.CrystalShopItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Objects;

public class GeneratorLootTable extends AbstractLootTable<GeneratorItem> {

    public GeneratorLootTable() {
        super("generators", "generatorItems", GeneratorItem.class);
    }

    @Override
    public void loadItem(String s, ConfigurationSection configurationSection) {
        this.items.put(s, new GeneratorItem(configurationSection));
    }

    @Override
    public GeneratorItem addItem(String s, ItemStack itemStack) {
        GeneratorItem generatorItem = new GeneratorItem(this.name, s, itemStack);

        this.items.put(generatorItem.getId(), generatorItem);

        return generatorItem;
    }

    @Override
    public GeneratorItem getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        Player sender = context.getPlayer();
        String source = context.popFirstArg();

        for (GeneratorItem item : this.items.values()) {
            if (Objects.equals(item.getId(), source)) return item;
        }

        throw new InvalidCommandArgument(CC.translate("&cInvalid generator item id."));
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        return this.items.keySet();
    }
}
