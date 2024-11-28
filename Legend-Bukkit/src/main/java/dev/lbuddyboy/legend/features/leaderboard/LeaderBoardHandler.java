package dev.lbuddyboy.legend.features.leaderboard;

import dev.lbuddyboy.commons.api.cache.UUIDCache;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.api.util.MojangUser;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.commons.util.LocationUtils;
import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.leaderboard.impl.*;
import dev.lbuddyboy.legend.features.leaderboard.thread.LeaderBoardUpdateThread;
import dev.lbuddyboy.legend.user.model.LegendUser;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class LeaderBoardHandler implements IModule {

    private final List<ILeaderBoardStat> leaderBoardStats;
    private final Map<ILeaderBoardStat, Map<UUID, LeaderBoardUser>> leaderboards;
    private final Map<ILeaderBoardStat, LeaderBoardHologram> holograms;
    private RotatingHologram rotatingHologram;
    private long lastUpdated;
    private Config config;

    public LeaderBoardHandler() {
        this.leaderBoardStats = new ArrayList<>();
        this.leaderboards = new ConcurrentHashMap<>();
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
                new KDRLeaderBoardStat(),
                new KillStreakLeaderBoardStat(),
                new HighestKillStreakLeaderBoardStat(),
                new PlayTimeLeaderBoardStat()
        ));

        this.rotatingHologram = new RotatingHologram(LocationUtils.deserializeString(config.getString("rotating-hologram", "world;0;100;0;0;0;")));

        new LeaderBoardUpdateThread().start();
        Tasks.runAsyncTimer(() -> this.rotatingHologram.rotate(), 20 * 15, 20 * 15);

        for (ILeaderBoardStat stat : this.leaderBoardStats) {
            this.leaderboards.put(stat, new ConcurrentHashMap<>());
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
    }

    @Override
    public void unload() {

    }

    public void update() {
        this.leaderboards.clear();

        for (ILeaderBoardStat stat : this.leaderBoardStats) {
            this.leaderboards.put(stat, new ConcurrentHashMap<>());
        }

        long start = System.currentTimeMillis();

        LegendBukkit.getInstance().getUserHandler().loadUsersAsync(
                Arrays.stream(Bukkit.getOfflinePlayers())
                        .filter(player -> player.getName() != null && UUIDCache.getUuidToNames().containsKey(player.getUniqueId()))
                        .map(OfflinePlayer::getUniqueId)
                        .collect(Collectors.toSet())
        ).thenAcceptAsync(users -> {
            Map<ILeaderBoardStat, PriorityQueue<LeaderBoardUser>> leaderboardPriorityQueues = new HashMap<>();

            for (LegendUser user : users) {
                for (ILeaderBoardStat type : this.leaderBoardStats) {
                    leaderboardPriorityQueues.computeIfAbsent(type, k -> new PriorityQueue<>(Comparator.comparingDouble(LeaderBoardUser::getScore).reversed())).offer(new LeaderBoardUser(user.getUuid(), user.getName(), type.getValue(user.getUuid())));
                }
            }

            leaderboardPriorityQueues.forEach((type, queue) -> {
                List<LeaderBoardUser> topUsers = new ArrayList<>();
                int limit = Math.min(10, queue.size());

                for (int i = 0; i < limit; i++) {
                    LeaderBoardUser user = queue.poll();
                    if (user != null) {
                        user.setPlace(i + 1);
                        topUsers.add(user);
                    }
                }

                this.leaderboards.put(type, topUsers.stream().collect(Collectors.toMap(LeaderBoardUser::getUuid, Function.identity())));
            });

            this.holograms.values().forEach(LeaderBoardHologram::update);
            this.leaderboards.values().forEach(e -> e.forEach((uuid, user) -> MojangUser.fetchTexture(uuid).thenAcceptAsync(texture -> user.setTexture(texture[0]))));

            long duration = System.currentTimeMillis() - start;
            Bukkit.broadcast("Leaderboard update completed in " + duration + "ms.", "op");
            Bukkit.broadcastMessage(CC.translate("<blend:&6;&4>&lLEADERBOARDS</> &7» &aLeaderboards have been updated! Next update in 15 minutes."));
        });


    }

    public Map<UUID, LeaderBoardUser> getLeaderBoard(ILeaderBoardStat stat) {
        return this.leaderboards.get(stat);
    }

    public Map<UUID, LeaderBoardUser> getLeaderBoard(Class<? extends ILeaderBoardStat> clazz) {
        return getLeaderBoard(getStatByClass(clazz));
    }

    public <T extends ILeaderBoardStat> T getStatByClass(Class<T> clazz) {
        return this.leaderBoardStats.stream().filter(stat -> stat.getClass().equals(clazz)).map(clazz::cast).findFirst().orElse(null);
    }

    public Optional<ILeaderBoardStat> getStatById(String id) {
        return this.leaderBoardStats.stream().filter(stat -> stat.getId().equalsIgnoreCase(id)).findFirst();
    }

}
