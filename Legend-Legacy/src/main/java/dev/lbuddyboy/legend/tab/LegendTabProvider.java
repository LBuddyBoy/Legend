package dev.lbuddyboy.legend.tab;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.util.PlaceholderUtil;
import io.github.nosequel.tab.shared.entry.TabElement;
import io.github.nosequel.tab.shared.entry.TabElementHandler;
import io.github.nosequel.tab.shared.entry.TabEntry;
import org.bukkit.entity.Player;

import java.util.Map;

public class LegendTabProvider implements TabElementHandler {

    @Override
    public TabElement getElement(Player player) {
        TabElement element = new TabElement();
        Map<Integer, TabEntry> elements = LegendBukkit.getInstance().getTabHandler().getDefaultElements();

        element.setHeader(CC.translate(PlaceholderUtil.applyAllPlaceholders(
                String.join("\n", LegendBukkit.getInstance().getTabHandler().getHeader()),
                player
        )));
        element.setFooter(CC.translate(PlaceholderUtil.applyAllPlaceholders(
                String.join("\n", LegendBukkit.getInstance().getTabHandler().getFooter()),
                player
        )));

        if (LegendBukkit.getInstance().getTeamHandler().getTeam(player).isPresent()) {
            elements = LegendBukkit.getInstance().getTabHandler().getTeamElements();
        }

        for (Map.Entry<Integer, TabEntry> baseEntry : elements.entrySet()) {
            TabEntry entry = new TabEntry(
                    baseEntry.getValue().getX(),
                    baseEntry.getValue().getY(),
                    CC.translate(PlaceholderUtil.applyAllPlaceholders(baseEntry.getValue().getText(), player)),
                    baseEntry.getValue().getPing()
            );

            if (baseEntry.getValue().getSkinData() != null) entry.setSkinData(baseEntry.getValue().getSkinData());

            element.add(entry);
        }

        return element;
    }
}