package dev.lbuddyboy.legend.tab;

import dev.lbuddyboy.commons.tab.TabProvider;
import dev.lbuddyboy.commons.tab.model.TabElement;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.util.PlaceholderUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LegendTabProvider implements TabProvider {

    @Override
    public String getHeader(Player player) {
        return CC.translate(PlaceholderUtil.applyAllPlaceholders(
                String.join("\n", LegendBukkit.getInstance().getTabHandler().getHeader()),
                player
        ));
    }

    @Override
    public String getFooter(Player player) {
        return CC.translate(PlaceholderUtil.applyAllPlaceholders(
                String.join("\n", LegendBukkit.getInstance().getTabHandler().getFooter()),
                player
        ));
    }

    @Override
    public List<TabElement> getSlots(Player player) {
        List<TabElement> elements = new ArrayList<>();

        for (TabElement baseElement : LegendBukkit.getInstance().getTeamHandler().getTeam(player).isPresent() ? LegendBukkit.getInstance().getTabHandler().getTeamElements() : LegendBukkit.getInstance().getTabHandler().getDefaultElements()) {
            TabElement element = new TabElement(
                    CC.translate(PlaceholderUtil.applyAllPlaceholders(baseElement.getText(), player)),
                    baseElement.getSlot(),
                    baseElement.getColumn(),
                    baseElement.getPing(),
                    baseElement.getSkin()
            );

            elements.add(element);
        }

        return elements;
    }
}