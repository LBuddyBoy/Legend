package dev.lbuddyboy.legend.features.kitmap.kit;

import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project lPractice
 * @file dev.lbuddyboy.practice.kit.editor
 * @since 5/3/2024
 */

@Data
public class EditedKit {

    private String kitName, name;
    private boolean defaultKit = false;
    private ItemStack[] inventoryContents;

    public EditedKit(Kit kit, String name, ItemStack[] inventoryContents) {
        this.kitName = kit.getName();
        this.name = name;
        this.inventoryContents = inventoryContents;
    }

    public EditedKit(Kit kit, String name) {
        this(kit, name, kit.getContents());
    }

    public EditedKit(Document document) {
        this.kitName = document.getString("kitName");
        this.name = document.getString("name");
        this.defaultKit = document.getBoolean("defaultKit");
        this.inventoryContents = ItemUtils.itemStackArrayFromBase64(document.getString("inventoryContents"));
    }

    public Kit getKit() {
        return LegendBukkit.getInstance().getKitMapHandler().getKits().get(this.kitName);
    }

    public void save(Player player) {
        this.inventoryContents = player.getInventory().getStorageContents().clone();
    }

    public Document toDocument() {
        return new Document()
                .append("kitName", this.kitName)
                .append("name", this.name)
                .append("defaultKit", this.defaultKit)
                .append("inventoryContents", ItemUtils.itemStackArrayToBase64(this.inventoryContents))
                ;
    }

}
