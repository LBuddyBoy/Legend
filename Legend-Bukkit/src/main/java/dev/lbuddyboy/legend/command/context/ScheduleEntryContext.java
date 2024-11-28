package dev.lbuddyboy.legend.command.context;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.kitmap.kit.Kit;
import dev.lbuddyboy.legend.features.schedule.ScheduleEntry;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project lPractice
 * @file dev.lbuddyboy.practice.command.context
 * @since 5/3/2024
 */
public class ScheduleEntryContext implements ContextResolver<ScheduleEntry, BukkitCommandExecutionContext> {

    @Override
    public ScheduleEntry getContext(BukkitCommandExecutionContext arg) throws InvalidCommandArgument {
        String source = arg.popFirstArg();
        ScheduleEntry entry = LegendBukkit.getInstance().getScheduleHandler().getScheduleEntry(source);

        if (entry != null) {
            return entry;
        }

        throw new InvalidCommandArgument(CC.translate("&cThat schedule entry does not exist."));
    }

}
