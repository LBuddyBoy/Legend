package dev.lbuddyboy.legend.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Optional;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.PagedItem;
import dev.lbuddyboy.legend.LangConfig;
import dev.lbuddyboy.legend.LegendBukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("help|?")
public class HelpCommand extends BaseCommand {

    @Default
    public static void def(Player sender, @Name("page") @Optional Integer page) {
        if (page == null) page = 1;

        send(sender, page);
    }

    public static int getMaxItemsPerPage() {
        return LangConfig.HELP_ITEMS_AMOUNT_PER_PAGE.getInt();
    }

    public static int getMaxPages() {
        return LangConfig.HELP_ITEMS.getStringList().size() / getMaxItemsPerPage() + 1;
    }

    public static void send(CommandSender sender, int page) {
        if (page > getMaxPages()) {
            sender.sendMessage(CC.translate("&cThat page is not within the bounds of " + getMaxPages() + "."));
        } else {
            for (String s : LangConfig.HELP_FORMAT.getStringList()) {
                if (s.contains("%items%")) {
                    for (int i = (page * getMaxItemsPerPage()) - getMaxItemsPerPage(); i < page * getMaxItemsPerPage(); ++i) {
                        if (LangConfig.HELP_ITEMS.getStringList().size() > i) {
                            sender.sendMessage(CC.translate(LangConfig.HELP_ITEMS.getStringList().get(i)));
                        }
                    }
                    continue;
                }
                sender.sendMessage(CC.translate(s.replaceAll("%page%", String.valueOf(page)).replaceAll("%max-pages%", String.valueOf(getMaxPages()))));
            }

        }
    }

}
