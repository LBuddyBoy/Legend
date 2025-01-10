package dev.lbuddyboy.legend.team.model.generator.upgrades.impl;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.team.model.generator.upgrades.AbstractGeneratorUpgrade;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class FortuneUpgrade extends AbstractGeneratorUpgrade {

    @Override
    public String getId() {
        return "fortune";
    }

    @Override
    public String getPrimaryColor() {
        return "&3";
    }

    @Override
    public String getSecondaryColor() {
        return "&b";
    }

    @Override
    public String getDisplayName() {
        return CC.blend("Fortune", "&2", "&a", "&l");
    }

    @Override
    public Material getDisplayMaterial() {
        return Material.GOLD_NUGGET;
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                "Chance to find more items when an",
                "item is generated."
        );
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public double getInitialCost() {
        return 30;
    }

    @Override
    public double getCostPerLevel() {
        return 15;
    }

}
