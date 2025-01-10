package dev.lbuddyboy.legend.team.model.generator;

import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.loottable.impl.LootTable;
import dev.lbuddyboy.commons.util.CC;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;

@Data
public class GeneratorTier {

    private String name, displayName, primaryColor, secondaryColor, materialString;
    private int tierRequired, menuSlot, customModelData;

    public GeneratorTier(String key, ConfigurationSection section) {
        this.name = key;
        this.displayName = section.getString("displayName");
        this.primaryColor = section.getString("primaryColor");
        this.secondaryColor = section.getString("secondaryColor");
        this.tierRequired = section.getInt("tierRequired");
        this.materialString = section.getString("menu.material");
        this.customModelData = section.getInt("menu.customModelData");
        this.menuSlot = section.getInt("menu.slot");
    }

    public LootTable getLootTable() {
        if (CommonsPlugin.getInstance().getLootTableHandler().getLootTables().containsKey("generator_tier_" + this.name))
            return (LootTable) CommonsPlugin.getInstance().getLootTableHandler().getLootTables().get("generator_tier_" + this.name);

        LootTable lootTable = new LootTable("generator_tier_" + this.name);
        lootTable.register();
        return getLootTable();
    }

    public String getColoredName() {
        return CC.blend(this.displayName, this.primaryColor, this.secondaryColor, "&l");
    }

    public Object[] getPlaceHolders() {
        return new Object[] {
                "%tier_name%", this.name,
                "%tier_material%", this.materialString,
                "%tier_display_name%", this.displayName,
                "%tier_colored_name%", getColoredName(),
                "%tier_primary_color%", this.primaryColor,
                "%tier_secondary_color%", this.secondaryColor,
                "%tier_required_tier%", this.tierRequired
        };
    }

}
