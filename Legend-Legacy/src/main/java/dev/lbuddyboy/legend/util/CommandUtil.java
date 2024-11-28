package dev.lbuddyboy.legend.util;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.HelpEntry;
import dev.lbuddyboy.commons.component.FancyBuilder;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.FancyPagedItem;
import dev.lbuddyboy.commons.util.PagedItem;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandUtil {

    public static final String CHAT_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 45);

    public static void generateCommandHelp(CommandHelp help) {
        int page = help.getPage();
        List<String> header = CC.translate(Arrays.asList(
                CHAT_BAR,
                "&6&lCommand Help &f[%page%/%max-pages%] &7(Hover for more info)"
        ));

        List<FancyBuilder> entries = new ArrayList<>();

        for (HelpEntry entry : help.getHelpEntries()) {
            if (entry.getCommand().contains(" help ")) continue;
            FancyBuilder builder = new FancyBuilder("&e/" + entry.getCommand() + " " + entry.getParameterSyntax());

            if (help.getIssuer().isPlayer()) {
                builder.hover((entry.getDescription().isEmpty() ? "&7None" : " &7- " + entry.getDescription()));
            } else {
                builder.append((entry.getDescription().isEmpty() ? "" : " &7- " + entry.getDescription()));
            }

            entries.add(builder);
        }

        FancyPagedItem pagedItem = new FancyPagedItem(entries, header, 10);

        pagedItem.send(help.getIssuer().getIssuer(), page);

        help.getIssuer().sendMessage(" ");
        help.getIssuer().sendMessage(CC.translate("&7You can do /" + help.getCommandName() + " help <page>"));
        help.getIssuer().sendMessage(CHAT_BAR);
    }

}
