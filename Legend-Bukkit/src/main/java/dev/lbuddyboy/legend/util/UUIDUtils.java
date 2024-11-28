package dev.lbuddyboy.legend.util;

import dev.lbuddyboy.commons.api.CommonsAPI;

import java.util.UUID;

public class UUIDUtils {

    public static String name(UUID uuid) {
        return CommonsAPI.getInstance().getUUIDCache().getName(uuid);
    }

    public static UUID uuid(String name) {
        return CommonsAPI.getInstance().getUUIDCache().getUUID(name);
    }

}
