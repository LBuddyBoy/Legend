package dev.lbuddyboy.legend.features.crystals;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.features.crystals.listener.CrystalListener;
import dev.lbuddyboy.legend.features.crystals.model.CrystalShopItem;
import dev.lbuddyboy.legend.features.crystals.model.CrystalShopLootTable;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class CrystalHandler implements IModule {

    private CrystalShopLootTable lootTable;
    private long lastRotation;
    private Map<Material, Double> oreChances;
    private Map<EntityType, Double> monsterChances;

    @Override
    public void load() {
        this.lastRotation = System.currentTimeMillis();

        Tasks.runTimer(() -> {
            if (getNextRotation() <= 0) {
                rotate();
            }
        }, 20, 20);

        this.loadLootTables();

        LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(new CrystalListener(), LegendBukkit.getInstance());

        reload();
    }

    @Override
    public void unload() {

    }

    @Override
    public void reload() {
        this.oreChances = new HashMap<>() {{
            put(Material.COAL_ORE, 0.50);
            put(Material.DEEPSLATE_COAL_ORE, 0.50);
            put(Material.COPPER_ORE, 1.0);
            put(Material.DEEPSLATE_COPPER_ORE, 1.0);
            put(Material.LAPIS_ORE, 1.5);
            put(Material.DEEPSLATE_LAPIS_ORE, 1.5);
            put(Material.REDSTONE_ORE, 1.5);
            put(Material.DEEPSLATE_REDSTONE_ORE, 1.5);
            put(Material.DIAMOND_ORE, 2.0);
            put(Material.DEEPSLATE_DIAMOND_ORE, 2.0);
            put(Material.EMERALD_ORE, 3.0);
            put(Material.DEEPSLATE_EMERALD_ORE, 3.0);
        }};

        this.monsterChances = new HashMap<>() {{
            put(EntityType.ZOMBIE, 0.50);
            put(EntityType.SPIDER, 0.75);
            put(EntityType.CAVE_SPIDER, 1.0);
            put(EntityType.SKELETON, 1.25);
            put(EntityType.CREEPER, 3.50);
            put(EntityType.ENDERMAN, 5.0);
        }};

        this.oreChances.forEach((k, v) -> this.oreChances.put(k, v * SettingsConfig.MODIFIERS_CRYSTALS.getDouble()));
        this.monsterChances.forEach((k, v) -> this.monsterChances.put(k, v * SettingsConfig.MODIFIERS_CRYSTALS.getDouble()));

    }

    private void loadLootTables() {
        this.lootTable = new CrystalShopLootTable();

        this.lootTable.register();
        this.lootTable.registerCommandInfo(LegendBukkit.getInstance().getCommandHandler().getCommandManager());
    }

    public void rotate() {
        this.lastRotation = System.currentTimeMillis();

        for (CrystalShopItem item : this.lootTable.getItems().values()) {
            item.setActive(false);
        }

        List<CrystalShopItem> rotationItems = new ArrayList<>(this.lootTable.getItems().values().stream().filter(CrystalShopItem::isRotation).toList());

        if (rotationItems.isEmpty()) {
            LegendBukkit.getInstance().getLogger().info("Tried rotating crystal shop, but no rotation items were found.");
            lootTable.save();
            return;
        }

        Collections.shuffle(rotationItems);
        int amount = 3;

        for (CrystalShopItem item : rotationItems) {
            if (amount <= 0) break;
            if (item.isActive()) continue;

            item.setActive(true);
            amount--;
        }

        lootTable.save();
        Bukkit.broadcastMessage(CC.translate("<blend:&3;&b>&lCRYSTAL SHOP</> &7» &fThe &bcrystal shop&f items have rotated! &7(/crystalshop)"));
    }

    public long getNextRotation() {
        return (this.lastRotation + (60_000 * 30)) - System.currentTimeMillis();
    }

}
