package dev.lbuddyboy.legend.team.model.brew;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import dev.lbuddyboy.commons.util.CC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.item.alchemy.Potion;
import org.bukkit.Material;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Getter
public enum BrewType {

    SPLASH_POTION_POISON_I(PotionType.POISON, 11, true, Arrays.asList(Material.NETHER_WART, Material.SPIDER_EYE, Material.GUNPOWDER), "Splash Potion of Poison I", "&2", "&a"),
    SPLASH_POTION_SLOWNESS_I(PotionType.SLOWNESS, 12, true, Arrays.asList(Material.NETHER_WART, Material.SUGAR, Material.FERMENTED_SPIDER_EYE), "Splash Potion of Slowness I", "&b", "&8"),
    SPLASH_POTION_HEALTH_II(PotionType.STRONG_HEALING, 20, true, Arrays.asList(Material.NETHER_WART, Material.GLISTERING_MELON_SLICE, Material.GLOWSTONE_DUST, Material.GUNPOWDER), "Splash Potion of Healing II", "&c", "&6"),
    DRINKABLE_SPEED_II(PotionType.STRONG_SWIFTNESS, 21, false, Arrays.asList(Material.NETHER_WART, Material.SUGAR, Material.GLOWSTONE_DUST), "Potion of Swiftness II", "&9", "&b"),
    DRINKABLE_INVISIBILITY_I(PotionType.LONG_INVISIBILITY, 29, false, Arrays.asList(Material.NETHER_WART, Material.GOLDEN_CARROT, Material.FERMENTED_SPIDER_EYE, Material.REDSTONE), "Potion of Invisibility I", "&8", "&7"),
    DRINKABLE_FIRE_RESISTANCE_I(PotionType.LONG_FIRE_RESISTANCE, 30, false, Arrays.asList(Material.NETHER_WART, Material.MAGMA_CREAM, Material.REDSTONE), "Potion of Fire Resistance I", "&4", "&c");

    private final PotionType type;
    private final int slot;
    private final boolean splash;
    private final List<Material> brewMaterials;
    private final String displayName, primaryColor, secondaryColor;

    public String getColoredName() {
        return CC.blend(this.displayName, this.primaryColor, this.secondaryColor);
    }

    public String getColoredNameBold() {
        return CC.blend("&l" + this.displayName, this.primaryColor, this.secondaryColor);
    }

    public static List<Material> MATERIALS;

    public static void init() {
        if (MATERIALS != null) return;

        Set<Material> materials = Sets.newHashSet();

        materials.add(Material.GLASS_BOTTLE);

        for (BrewType type : values()) {
            materials.addAll(type.getBrewMaterials());
        }

        MATERIALS = materials.stream().distinct().toList();
    }

}
