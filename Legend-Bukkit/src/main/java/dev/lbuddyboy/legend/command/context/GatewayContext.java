package dev.lbuddyboy.legend.command.context;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.gateways.model.Gateway;
import dev.lbuddyboy.legend.features.kitmap.kit.Kit;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project lPractice
 * @file dev.lbuddyboy.practice.command.context
 * @since 5/3/2024
 */
public class GatewayContext implements ContextResolver<Gateway, BukkitCommandExecutionContext> {

    @Override
    public Gateway getContext(BukkitCommandExecutionContext arg) throws InvalidCommandArgument {
        String source = arg.popFirstArg();
        Gateway gateway = LegendBukkit.getInstance().getGatewayHandler().getGateways().get(source.toLowerCase());

        if (gateway != null) {
            return gateway;
        }

        throw new InvalidCommandArgument(CC.translate("&cThat gateway does not exist."));
    }
}
