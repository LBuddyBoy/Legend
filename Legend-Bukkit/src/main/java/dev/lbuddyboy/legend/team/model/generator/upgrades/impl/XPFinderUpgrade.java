package dev.lbuddyboy.legend.team.model.generator.upgrades.impl;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.team.model.generator.upgrades.AbstractGeneratorUpgrade;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class XPFinderUpgrade extends AbstractGeneratorUpgrade {

    @Override
    public String getId() {
        return "xp-finder";
    }

    @Override
    public String getPrimaryColor() {
        return "&2";
    }

    @Override
    public String getSecondaryColor() {
        return "&a";
    }

    @Override
    public String getDisplayName() {
        return CC.blend("XP Finder", "&2", "&a", "&l");
    }

    @Override
    public Material getDisplayMaterial() {
        return Material.EXPERIENCE_BOTTLE;
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                "Chance to find more generator experience",
                "when an item is generated."
        );
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    @Override
    public double getInitialCost() {
        return 20;
    }

    @Override
    public double getCostPerLevel() {
        return 15;
    }

}
