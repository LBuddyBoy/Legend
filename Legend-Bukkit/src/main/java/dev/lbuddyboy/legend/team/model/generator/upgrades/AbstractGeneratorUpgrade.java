package dev.lbuddyboy.legend.team.model.generator.upgrades;

import dev.lbuddyboy.legend.team.model.generator.GeneratorData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class AbstractGeneratorUpgrade {

    public abstract String getId();
    public abstract String getPrimaryColor();
    public abstract String getSecondaryColor();
    public abstract String getDisplayName();
    public abstract Material getDisplayMaterial();
    public abstract List<String> getDescription();
    public abstract int getMaxLevel();
    public abstract double getInitialCost();
    public abstract double getCostPerLevel();

}
