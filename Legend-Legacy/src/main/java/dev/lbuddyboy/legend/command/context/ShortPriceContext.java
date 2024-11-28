package dev.lbuddyboy.legend.command.context;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ShortPrice;
import dev.lbuddyboy.legend.team.model.TeamType;

public class ShortPriceContext implements ContextResolver<ShortPrice, BukkitCommandExecutionContext> {

    @Override
    public ShortPrice getContext(BukkitCommandExecutionContext arg) throws InvalidCommandArgument {
        String source = arg.popFirstArg();
        ShortPrice price = new ShortPrice(source);
        double converted = price.convert();

        if (converted > 0) {
            return price;
        }

        throw new InvalidCommandArgument(CC.translate("&cThat amount is invalid."));
    }

}
