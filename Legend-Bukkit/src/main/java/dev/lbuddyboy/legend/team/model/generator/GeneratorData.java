package dev.lbuddyboy.legend.team.model.generator;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.loottable.impl.LootTable;
import dev.lbuddyboy.commons.loottable.impl.LootTableItem;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.features.settings.Setting;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.log.impl.generator.TeamGeneratedItemExpireLog;
import dev.lbuddyboy.legend.team.model.log.impl.generator.TeamGeneratedItemLog;
import dev.lbuddyboy.legend.util.LevelUtil;
import dev.lbuddyboy.legend.util.TypeTokens;
import dev.lbuddyboy.legend.util.model.DocumentedItemStack;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Data
public class GeneratorData {

    private static long PRODUCTION_INTERVAL = 120_000L;

    private int tier;
    private double experience;
    private long lastProduced;
    private List<DocumentedItemStack> generatedItems = new ArrayList<>();
    private Map<String, Integer> generatorUpgrades = new HashMap<>();

    public GeneratorData() {
        this.tier = 1;
        this.experience = 0.0D;
        this.lastProduced = System.currentTimeMillis();
    }

    public GeneratorData(Document document) {
        this.tier = document.getInteger("tier");
        this.experience = document.getDouble("experience");
        this.lastProduced = document.getLong("lastProduced");
        this.generatedItems.addAll(APIConstants.GSON.fromJson(document.getString("generatedItems"), TypeTokens.DOCUMENTED_ITEMS.getType()));
        this.generatorUpgrades.putAll(APIConstants.GSON.fromJson(document.getString("generatorUpgrades"), TypeTokens.STRING_INT_MAP.getType()));
    }

    public double getExperienceRequired() {
        return LevelUtil.getExperience(this.tier + 1, SettingsConfig.TEAM_GENERATORS_INITIAL_EXPERIENCE_LEVEL_UP.getDouble(), SettingsConfig.TEAM_GENERATORS_EXPERIENCE_LEVEL_UP_FACTOR.getDouble());
    }

    public boolean shouldProduce() {
        return this.getNextProduction() <= 0;
    }

    public long getNextProduction() {
        long modifier = (750L * this.generatorUpgrades.getOrDefault("efficiency", 0));

        return (this.lastProduced + (PRODUCTION_INTERVAL - modifier)) - System.currentTimeMillis();
    }

    public Document toDocument() {
        return new Document()
                .append("generatedItems", APIConstants.GSON.toJson(this.generatedItems, TypeTokens.DOCUMENTED_ITEMS.getType()))
                .append("generatorUpgrades", APIConstants.GSON.toJson(this.generatorUpgrades, TypeTokens.STRING_INT_MAP.getType()))
                .append("lastProduced", this.lastProduced)
                .append("experience", this.experience)
                .append("tier", this.tier);
    }

    public void cleanUpGeneratedItems(Team team) {
        this.generatedItems.removeIf(item -> {
            if (item.isExpired()) {
                team.sendMessage(CC.translate("<blend:&6;&e>&lGENERATORS</> &7» &cA generated item '" + ItemUtils.getName(item.getItemStack()) + "&c' expired!"));
//                team.createTeamLog(new TeamGeneratedItemExpireLog(team.getId(), item.getItemString()));
                return true;
            }

            return false;
        });
    }

    public void tick(Team team) {
        if (!shouldProduce()) return;

        LootTable lootTable = getGeneratorTier().getLootTable();
        int minimumLoot = 1;
        int maximumLoot = 2;
        int fortuneLevel = this.generatorUpgrades.getOrDefault("fortune", 0);

        if (fortuneLevel > 0 && ThreadLocalRandom.current().nextInt(100) <= (2.5 * fortuneLevel)) {
            maximumLoot += ThreadLocalRandom.current().nextInt(0, fortuneLevel + 1);
        }

        for (LootTableItem item : lootTable.roll(minimumLoot, maximumLoot)) {
            this.generatedItems.add(new DocumentedItemStack(item.getItem()));
//            team.createTeamLog(new TeamGeneratedItemLog(team.getId(), ItemUtils.itemStackToBase64(item.getItem())));

            for (Player player : team.getOnlinePlayers()) {
                if (!Setting.GENERATOR_MESSAGES.isToggled(player.getUniqueId())) continue;

                player.sendMessage(CC.translate("<blend:&6;&e>&lGENERATORS</> &7» &aYour generator has generated a '" + ItemUtils.getName(item.getItem()) + "&a'!"));
            }
        }

        this.lastProduced = System.currentTimeMillis();

        if (this.tier >= SettingsConfig.TEAM_GENERATORS_MAX_TIER.getInt()) {
            return;
        }

        double experience = ThreadLocalRandom.current().nextDouble(SettingsConfig.TEAM_GENERATORS_EXPERIENCE_PER_GENERATION.getDouble());

        int xpFinderLevel = this.generatorUpgrades.getOrDefault("xp-finder", 0);

        if (xpFinderLevel > 0 && ThreadLocalRandom.current().nextInt(100) <= (3.35 * xpFinderLevel)) {
            experience *= (xpFinderLevel + 1);
        }

        this.setExperience(this.experience + experience);

        if (this.experience >= this.getExperienceRequired()) {
            this.experience = 0;
            this.tier++;

            team.sendMessage("<blend:&6;&e>&lGENERATORS</> &7» &aYour generator has tiered up to tier " + this.tier + "!");
            team.playSound(Sound.ENTITY_PLAYER_LEVELUP, 1.0f);
        }

    }

    public GeneratorTier getGeneratorTier() {
        GeneratorTier tier = null;

        for (GeneratorTier other : LegendBukkit.getInstance().getTeamHandler().getSortedTiers()) {
            if (this.tier >= other.getTierRequired()) tier = other;
        }

        return tier;
    }

}
