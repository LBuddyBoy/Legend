package dev.lbuddyboy.legend.features.kitmap.kit;

import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.model.LegendUser;
import lombok.Data;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Data
public class Kit {

    private final Config config;
    private final String name;

    private String displayName;
    private Material displayMaterial;
    private ItemStack[] armor = new ItemStack[4], contents = new ItemStack[36], extras = new ItemStack[0], editorItems = new ItemStack[36];

    public Kit(String name) {
        this.name = name.toLowerCase();
        this.config = new Config(LegendBukkit.getInstance(), this.name, LegendBukkit.getInstance().getKitMapHandler().getKitsDirectory());
        this.displayName = WordUtils.capitalize(this.name);
        this.displayMaterial = Material.GRASS_BLOCK;
    }

    public Kit(Config config) {
        this.config = config;
        this.name = config.getFileName().replaceAll(".yml", "").toLowerCase();
        this.displayName = config.getString("displayName");
        this.displayMaterial = Material.getMaterial(config.getString("displayMaterial"));
        this.armor = ItemUtils.itemStackArrayFromBase64(config.getString("armor"));
        this.contents = ItemUtils.itemStackArrayFromBase64(config.getString("contents"));
        this.extras = ItemUtils.itemStackArrayFromBase64(config.getString("extras"));
        this.editorItems = ItemUtils.itemStackArrayFromBase64(config.getString("editorItems"));
    }

    public void delete() {
        if (this.config.getFile().exists()) this.config.getFile().delete();
        LegendBukkit.getInstance().getKitMapHandler().getKits().remove(this.name);
    }

    public void save() {
        this.config.set("displayName", this.displayName);
        this.config.set("displayMaterial", this.displayMaterial.name());
        this.config.set("armor", ItemUtils.itemStackArrayToBase64(this.armor));
        this.config.set("contents", ItemUtils.itemStackArrayToBase64(this.contents));
        this.config.set("extras", ItemUtils.itemStackArrayToBase64(this.extras));
        this.config.set("editorItems", ItemUtils.itemStackArrayToBase64(this.editorItems));
        this.config.save();
    }

    public void apply(Player player) {
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());

        player.getInventory().clear();

        if (!user.getEditedKits(this).isEmpty()) {
            EditedKit kit = user.getEditedKits(this).getFirst();

            player.getInventory().setStorageContents(kit.getInventoryContents());
        } else {
            player.getInventory().setStorageContents(this.contents);
        }

        player.getInventory().setArmorContents(this.armor);
        player.getInventory().setExtraContents(this.extras);
    }

}
