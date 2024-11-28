package dev.lbuddyboy.legend.features.leaderboard;

import dev.lbuddyboy.api.leaderboard.model.LeaderboardDataEntry;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.*;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.leaderboard.impl.*;
import dev.lbuddyboy.legend.features.leaderboard.model.LeaderBoardHologram;
import dev.lbuddyboy.legend.features.leaderboard.model.RotatingHologram;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class LeaderBoardHandler implements IModule {

    private final List<ILeaderBoardStat> leaderBoardStats;
    private final Map<ILeaderBoardStat, LeaderBoardHologram> holograms;
    private RotatingHologram rotatingHologram;
    private long lastUpdated;
    private Config config;

    public LeaderBoardHandler() {
        this.leaderBoardStats = new ArrayList<>();
        this.holograms = new ConcurrentHashMap<>();
    }

    @Override
    public void load() {
        this.lastUpdated = System.currentTimeMillis();
        this.config = new Config(LegendBukkit.getInstance(), "leaderboards");

        this.leaderBoardStats.addAll(Arrays.asList(
                new KillLeaderBoardStat(),
                new DeathLeaderBoardStat(),
                new MoneyLeaderBoardStat(),
                // new KDRLeaderBoardStat(),
                // new KillStreakLeaderBoardStat(),
                new HighestKillStreakLeaderBoardStat(),
                new PlayTimeLeaderBoardStat()
        ));

        this.rotatingHologram = new RotatingHologram(LocationUtils.deserializeString(config.getString("rotating-hologram", "world;0;100;0;0;0;")));

        Tasks.runAsyncTimer(() -> this.rotatingHologram.rotate(), 20 * 15, 20 * 15);

        for (ILeaderBoardStat stat : this.leaderBoardStats) {
            this.holograms.put(stat, new LeaderBoardHologram(stat));
        }

        if (this.config.getConfigurationSection("holograms") != null) {
            for (String key : this.config.getConfigurationSection("holograms").getKeys(false)) {
                Optional<ILeaderBoardStat> statOpt = getStatById(key);

                if (!statOpt.isPresent()) {
                    LegendBukkit.getInstance().getLogger().warning("[Leaderboards] Error loading " + key + " hologram. Couldn't find a statistic with that id.");
                    continue;
                }

                this.holograms.put(statOpt.get(), new LeaderBoardHologram(key, config));
            }
        }

        this.holograms.values().forEach(LeaderBoardHologram::spawn);

        /**
         * Preload player heads bc it lags so bad for some reason
         */

        for (ILeaderBoardStat stat : this.leaderBoardStats) {
            LeaderboardDataEntry leaderboardDataEntry = LegendBukkit.getInstance().getDataEntry(stat.getId());
            List<Map.Entry<UUID, Integer>> users = leaderboardDataEntry.getLeaderBoards().entrySet().stream()
                    .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                    .limit(10)
                    .toList();

            users.forEach(e -> new ItemFactory(e.getKey()).build());
        }
    }

    @Override
    public void unload() {

    }

    public <T extends ILeaderBoardStat> List<Map.Entry<UUID, Integer>> getLeaderBoard(Class<T> clazz) {
        return LegendBukkit.getInstance().getDataEntry(getStatByClass(clazz).getId()).getLeaderBoards().entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .limit(10)
                .toList();
    }

    public <T extends ILeaderBoardStat> T getStatByClass(Class<T> clazz) {
        return this.leaderBoardStats.stream().filter(stat -> stat.getClass().equals(clazz)).map(clazz::cast).findFirst().orElse(null);
    }

    public Optional<ILeaderBoardStat> getStatById(String id) {
        return this.leaderBoardStats.stream().filter(stat -> stat.getId().equalsIgnoreCase(id)).findFirst();
    }


    public <T extends ILeaderBoardStat> String getFancyPlace(UUID playerUUID, Class<T> clazz) {
        List<Map.Entry<UUID, Integer>> leaderboard = getLeaderBoard(clazz);
        if (leaderboard.stream().noneMatch(e -> e.getKey().equals(playerUUID))) return "";

        for (int i = 0; i < 3; i++) {
            if (i >= leaderboard.size()) continue;
            if (!leaderboard.get(i).getKey().equals(playerUUID)) continue;

            if (i == 0) {
                return CC.translate("&6❶ ");
            } else if (i == 1) {
                return CC.translate("&e❷ ");
            }

            return CC.translate("&b❸ ");
        }

        return "";
    }


}
