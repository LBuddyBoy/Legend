package dev.lbuddyboy.legend.features.crystals.model;

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
public class CrystalShopItem extends AbstractItem implements Informable {

    private double price = 1;
    private int slot = 1;
    private boolean rotation = false, active = false;

    public CrystalShopItem(String parent, String id) {
        super(parent, id);
    }

    public CrystalShopItem(String parent, String id, ItemStack item) {
        super(parent, id, item);
    }

    public CrystalShopItem(ConfigurationSection section) {
        super(section);

        this.price = section.getInt("price", 1);
        this.slot = section.getInt("slot", 1);
        this.rotation = section.getBoolean("rotation", false);
        this.active = section.getBoolean("active", false);
    }


    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("id", this.getId());
        map.put("displayName", getDisplayName());
        map.put("item", ItemUtils.itemStackToBase64(getItem()));
        map.put("chance", getChance());
        map.put("giveItem", isGiveItem());
        map.put("price", this.price);
        map.put("slot", this.slot);
        map.put("rotation", this.rotation);
        map.put("active", this.active);
        map.put("commands", getCommands());
        map.put("priority", this.getPriority());
        map.put("enabled", this.isEnabled());

        return map;
    }

    @Override
    public List<String> getBreakDown() {
        List<String> lore = super.getBreakDown();

        lore.add("&ePrice&7: &f" + this.price + " shards");
        lore.add("&eSlot&7: &f" + this.slot);
        lore.add("&eRotates&7: &f" + (this.rotation ? "&aYes" : "&cNo"));
        lore.add("&eActive&7: &f" + (this.active ? "&aYes" : "&cNo"));

        return lore;
    }

    public Object[] getPlaceHolders() {
        return new Object[] {
                "%item_material%", getItem().getType(),
                "%item_price%", APIConstants.formatNumber(this.price),
                "%item_display_name%", getDisplayName(),
                "%item_chance%", getChance(),
                "%item_lore%", StringUtils.join(ItemUtils.getLore(getItem()), "\n")
        };
    }

}
