package dev.lbuddyboy.legend.team.model.generator;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.util.Informable;
import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.lbuddyboy.commons.loottable.AbstractItem;
import dev.lbuddyboy.commons.util.ItemUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class GeneratorItem extends AbstractItem implements Informable {

    private int requiredTier;

    public GeneratorItem(String parent, String id) {
        super(parent, id);
    }

    public GeneratorItem(String parent, String id, ItemStack item) {
        super(parent, id, item);
    }

    public GeneratorItem(ConfigurationSection section) {
        super(section);

        this.requiredTier = section.getInt("requiredTier", 1);
    }


    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("id", this.getId());
        map.put("displayName", getDisplayName());
        map.put("item", ItemUtils.itemStackToBase64(getItem()));
        map.put("chance", getChance());
        map.put("giveItem", isGiveItem());
        map.put("requiredTier", this.requiredTier);
        map.put("commands", getCommands());
        map.put("priority", this.getPriority());
        map.put("enabled", this.isEnabled());

        return map;
    }

    @Override
    public List<String> getBreakDown() {
        List<String> lore = super.getBreakDown();

        lore.add("&eRequired Tier&7: &f" + this.requiredTier);

        return lore;
    }

    public Object[] getPlaceHolders() {
        return new Object[] {
                "%item_material%", getItem().getType(),
                "%item_tier%", APIConstants.formatNumber(this.requiredTier),
                "%item_display_name%", getDisplayName(),
                "%item_chance%", getChance(),
                "%item_lore%", StringUtils.join(ItemUtils.getLore(getItem()), "\n")
        };
    }

}
