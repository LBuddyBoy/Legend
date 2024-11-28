package dev.lbuddyboy.legend.user;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.listener.UserListener;
import dev.lbuddyboy.legend.user.model.LegendUser;
import dev.lbuddyboy.legend.util.UUIDUtils;
import lombok.Getter;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Getter
public class UserHandler implements IModule {

    private MongoCollection<Document> collection;

    private final LoadingCache<UUID, LegendUser> userCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES) // Entries expire after 10 minutes
            .maximumSize(1000)
            .removalListener((RemovalListener<UUID, LegendUser>) notification -> {
                if (notification.wasEvicted()) {
                    LegendUser user = notification.getValue();
                    if (user != null) {
                        try {
                            if (user.isOnline()) {
                                getUserCache().put(user.getUuid(), user);
                            }
                            user.save(true);
                            LegendBukkit.getInstance().getLogger().info("Saved user data for UUID " + notification.getKey() + " (" + UUIDUtils.name(notification.getKey()) + ") upon eviction.");
                        } catch (Exception e) {
                            LegendBukkit.getInstance().getLogger().info("Failed to save user data for UUID " + notification.getKey() + " (" + UUIDUtils.name(notification.getKey()) + ") upon eviction.");
                            e.printStackTrace();
                        }
                    }
                }
            })
            .build(new CacheLoader<>() {
                public @NotNull LegendUser load(@NotNull UUID uuid) {
                    Bson bson = Filters.eq("uuid", uuid.toString());
                    Document document = getCollection().find(bson).first();

                    if (document == null) {
                        String userName = UUIDUtils.name(uuid);

                        if (userName.equalsIgnoreCase("null")) {
                            throw new IllegalStateException("Username not found for UUID: " + uuid);
                        }

                        LegendUser newUser = new LegendUser(uuid, userName);
                        newUser.save(true);
                        return newUser;
                    }

                    return new LegendUser(document);
                }
            });


    @Override
    public void load() {
        this.collection = LegendBukkit.getInstance().getMongoHandler().getDatabase().getCollection("Users");

        for (Player player : Bukkit.getOnlinePlayers()) {
            LegendUser user = getUser(player.getUniqueId());

            user.setJoinedAt(System.currentTimeMillis());
        }

        LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(new UserListener(), LegendBukkit.getInstance());
    }

    @Override
    public void unload() {
        this.userCache.asMap().values().forEach(user -> {
            if (user.isOnline()) {
                user.setPlayTime(user.getPlayTime() + user.getActivePlayTime());
                user.setJoinedAt(-1L);
            }

            user.save(false);
        });
    }

    public LegendUser getUser(UUID uuid) {
        try {
            return this.userCache.get(uuid);
        } catch (ExecutionException e) {
            e.printStackTrace();
            LegendBukkit.getInstance().getLogger().info("Failed to load " + uuid + " (" + UUIDUtils.name(uuid) + ") UserHandler#getUser.");
            return null;
        }
    }

    public CompletableFuture<LegendUser> loadAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> getUser(uuid));
    }

}
