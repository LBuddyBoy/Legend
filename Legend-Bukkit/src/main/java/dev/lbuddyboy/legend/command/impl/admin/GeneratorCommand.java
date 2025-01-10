package dev.lbuddyboy.legend.command.impl.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.crystals.model.CrystalShopItem;
import dev.lbuddyboy.legend.features.crystals.model.CrystalShopLootTable;
import dev.lbuddyboy.legend.team.model.generator.GeneratorItem;
import dev.lbuddyboy.legend.team.model.generator.GeneratorLootTable;
import org.bukkit.entity.Player;

@CommandAlias("generator|gen|gens")
public class GeneratorCommand extends BaseCommand {

/*    @Subcommand("settier")
    @CommandCompletion("@generatorItems")
    @CommandPermission("loottables.admin")
    public void chance(Player sender, @Name("item") GeneratorItem item, @Name("minimumTier") int minimumTier) {
        GeneratorLootTable lootTable = LegendBukkit.getInstance().getTeamHandler().getGeneratorLootTable();

        item.setRequiredTier(minimumTier);
        lootTable.save();

        sender.sendMessage(CC.translate("&aThe " + item.getDisplayName() + "&a minimum tier has been set to " + minimumTier + "."));
    }*/

}
