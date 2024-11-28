package dev.lbuddyboy.practice.kit.editor;

import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.practice.kit.Kit;
import dev.lbuddyboy.practice.lPractice;
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
        this(kit, name, kit.getInventoryContents());
    }

    public EditedKit(Document document) {
        this.kitName = document.getString("kitName");
        this.name = document.getString("name");
        this.defaultKit = document.getBoolean("defaultKit");
        this.inventoryContents = ItemUtils.itemStackArrayFromBase64(document.getString("inventoryContents"));
    }

    public Kit getKit() {
        return lPractice.getInstance().getKitHandler().getKits().get(this.kitName);
    }

    public void save(Player player) {
        this.inventoryContents = player.getInventory().getStorageContents().clone();
    }

    public ItemStack createKitItem() {
        NBTItem item = new NBTItem(new ItemFactory(Material.ENCHANTED_BOOK)
                .displayName(CC.blend("Edited Kit: " + this.name, "&6", "&e") + " &7(Click to Apply)")
                .build());

        item.setString("edited-kit", this.name);
        item.setString("kitName", this.kitName);

        return item.getItem();
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
