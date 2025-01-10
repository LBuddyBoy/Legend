package dev.lbuddyboy.legend.util.model;

import dev.lbuddyboy.commons.util.ItemUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class DocumentedItemStack {

    private String itemString;
    private long addedAt;

    private transient ItemStack itemStack;

    public DocumentedItemStack(ItemStack stack) {
        this.itemString = ItemUtils.itemStackToBase64(stack);
        this.itemStack = stack.clone();
        this.addedAt = System.currentTimeMillis();
    }

    public ItemStack getItemStack() {
        if (this.itemStack == null) {
            this.itemStack = ItemUtils.itemStackFromBase64(this.itemString);
        }

        return this.itemStack.clone();
    }

    public boolean isExpired() {
        return this.getExpiry() <= 0;
    }

    public long getExpiry() {
        return this.addedAt + (60_000 * 60 * 24) - System.currentTimeMillis();
    }

}
