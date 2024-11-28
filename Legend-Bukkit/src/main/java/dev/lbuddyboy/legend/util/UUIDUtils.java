package dev.lbuddyboy.legend.util;

import co.aikar.commands.InvalidCommandArgument;
import dev.lbuddyboy.arrow.ArrowPlugin;
import dev.lbuddyboy.commons.api.CommonsAPI;
import dev.lbuddyboy.commons.api.util.MojangUser;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UUIDUtils {

    public static String name(UUID uuid) {
        return CommonsAPI.getInstance().getUUIDCache().getName(uuid);
    }

    public static UUID uuid(String name) {
        return CommonsAPI.getInstance().getUUIDCache().getUUID(name);
    }

    public static UUID fetchUUID(String name) {
        UUID uuid = ArrowPlugin.getInstance().getArrowAPI().getUUIDCache().getUUID(name.toLowerCase());
        if (uuid != null) {
            return uuid;
        } else {
            CompletableFuture<MojangUser> uuidFuture = MojangUser.fetchAsync(name);
            MojangUser user = (MojangUser)uuidFuture.thenApply((futureUser) -> {
                ArrowPlugin.getInstance().getArrowAPI().getUUIDCache().cache(futureUser.getUuid(), futureUser.getName(), true);
                return futureUser;
            }).exceptionally((e) -> null).join();
            if (user != null) {
                return user.getUuid();
            } else {
                throw new InvalidCommandArgument("No player with the name " + name + " could be found.");
            }
        }
    }

}
