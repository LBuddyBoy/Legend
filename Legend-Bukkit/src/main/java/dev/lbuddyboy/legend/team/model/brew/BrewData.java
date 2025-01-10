package dev.lbuddyboy.legend.team.model.brew;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.team.model.Team;
import lombok.Getter;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

@Getter
public class BrewData {

    private final Map<BrewType, Long> brewedPotions = new HashMap<>();
    private final Map<BrewType, Long> lastBrewedTimes = new HashMap<>();
    private final Map<BrewType, Boolean> startedBrewing = new HashMap<>();
    private final Map<Material, Long> brewingMaterials = new HashMap<>();

    public long getBrewedPotions(BrewType type) {
        return this.brewedPotions.getOrDefault(type, 0L);
    }

    public long getBrewingMaterials(Material material) {
        return this.brewingMaterials.getOrDefault(material, 0L);
    }

    public boolean isBrewing(BrewType type) {
        return this.startedBrewing.getOrDefault(type, false);
    }

    public boolean canBrew(BrewType type) {
        return this.brewingMaterials.getOrDefault(Material.GLASS_BOTTLE, 0L) > 0 && type.getBrewMaterials().stream().allMatch(m -> this.getBrewingMaterials(m) > 0);
    }

    public long getBrewTimeLeft(BrewType type) {
        return ((this.lastBrewedTimes.getOrDefault(type, 0L) + this.getBrewDelay()) - System.currentTimeMillis()) / 1000L;
    }

    public BrewResult attemptBrew(Team team, BrewType type) {
        long bottles = this.brewingMaterials.getOrDefault(Material.GLASS_BOTTLE, 0L);

        if (!isBrewing(type)) {
            return BrewResult.NOT_ACTIVE;
        }

        if (bottles <= 0) {
            team.sendMessage(CC.translate("<blend:&6;&e>&lTEAM BREW</>&7 » &fCouldn't brew " + type.getColoredName() + "&f due to you not having any glass bottles."));
            return BrewResult.INVALID_MATERIALS;
        }

        for (Material material : type.getBrewMaterials()) {
            long amount = this.brewingMaterials.getOrDefault(material, 0L);
            boolean success = true;

            if (amount <= 0) {
                team.sendMessage(CC.translate("<blend:&6;&e>&lTEAM BREW</>&7 » &fCouldn't brew " + type.getColoredName() + "&f due to you not having any " + ItemUtils.getName(material) + "."));
                success = false;
            }

            if (!success) return BrewResult.INVALID_MATERIALS;
        }

        return this.getBrewTimeLeft(type) <= 0 ? BrewResult.SUCCESS : BrewResult.BREW_DELAY;
    }

    public void brewPotion(BrewType type) {
        long amount = this.getBrewingMaterials(Material.GLASS_BOTTLE) < 3 ? this.getBrewingMaterials(Material.GLASS_BOTTLE) : 3;

        this.brewedPotions.put(type, this.getBrewedPotions(type) + amount);
        this.brewingMaterials.put(Material.GLASS_BOTTLE, this.getBrewingMaterials(Material.GLASS_BOTTLE) - amount);

        for (Material material : type.getBrewMaterials()) {
            this.brewingMaterials.put(material, this.getBrewingMaterials(material) - 1);
        }

        this.lastBrewedTimes.put(type, System.currentTimeMillis());
    }

    public long getBrewDelay() {
        return 5_000L;
    }

}
