package dev.lbuddyboy.legend.command.topic;

import co.aikar.commands.*;
import dev.lbuddyboy.commons.component.FancyBuilder;
import dev.lbuddyboy.legend.util.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.help.GenericCommandHelpTopic;

import java.util.ArrayList;
import java.util.List;

public class LegendHelpTopic  extends GenericCommandHelpTopic {

    public LegendHelpTopic(BukkitCommandManager manager, BukkitRootCommand command) {
        super(command);
        final List<String> messages = new ArrayList();

        LegendCommandIssuer captureIssuer = new LegendCommandIssuer(manager, Bukkit.getConsoleSender()) {
            public void sendMessageInternal(String message) {
                messages.add(message);
            }
        };

        CommandHelp commandHelp = manager.generateCommandHelp(captureIssuer, command);

        CommandUtil.generateCommandHelp(commandHelp);
        this.fullText = ACFUtil.join(messages, "\n");
    }

}

