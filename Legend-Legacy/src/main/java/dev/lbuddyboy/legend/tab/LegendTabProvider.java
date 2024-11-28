package dev.lbuddyboy.legend.tab;

import dev.lbuddyboy.commons.tab.model.TabElement;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.util.PlaceholderUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LegendTabProvider implements dev.lbuddyboy.commons.tab.TabProvider {

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

        for (TabElement baseElement : LegendBukkit.getInstance().getTabHandler().getElements()) {
            TabElement element = new TabElement(
                    CC.translate(PlaceholderUtil.applyAllPlaceholders(baseElement.getText(), player)),
                    baseElement.getSlot(),
                    baseElement.getColumn(),
                    baseElement.getPing() == -1 ? player.getPing() : baseElement.getPing(),
                    baseElement.getSkin()
            );

            elements.add(element);
        }

        return elements;
    }

}
