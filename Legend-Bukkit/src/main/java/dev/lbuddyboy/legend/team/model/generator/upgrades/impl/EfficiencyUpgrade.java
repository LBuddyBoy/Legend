package dev.lbuddyboy.legend.team.model.generator.upgrades.impl;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.team.model.generator.GeneratorData;
import dev.lbuddyboy.legend.team.model.generator.upgrades.AbstractGeneratorUpgrade;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class EfficiencyUpgrade extends AbstractGeneratorUpgrade {

    @Override
    public String getId() {
        return "efficiency";
    }

    @Override
    public String getPrimaryColor() {
        return "&6";
    }

    @Override
    public String getSecondaryColor() {
        return "&e";
    }

    @Override
    public String getDisplayName() {
        return CC.blend("Efficiency", "&6", "&e", "&l");
    }

    @Override
    public Material getDisplayMaterial() {
        return Material.SUGAR;
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                "Decreases your production speed by",
                "0.75 seconds per level."
        );
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public double getInitialCost() {
        return 15;
    }

    @Override
    public double getCostPerLevel() {
        return 15;
    }

}
