package dev.lbuddyboy.legend.command.context;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.kitmap.kit.Kit;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project lPractice
 * @file dev.lbuddyboy.practice.command.context
 * @since 5/3/2024
 */
public class KitContext implements ContextResolver<Kit, BukkitCommandExecutionContext> {

    @Override
    public Kit getContext(BukkitCommandExecutionContext arg) throws InvalidCommandArgument {
        String source = arg.popFirstArg();
        Kit kit = LegendBukkit.getInstance().getKitMapHandler().getKits().get(source.toLowerCase());

        if (kit != null) {
            return kit;
        }

        throw new InvalidCommandArgument(CC.translate("&cThat kit does not exist."));
    }
}
