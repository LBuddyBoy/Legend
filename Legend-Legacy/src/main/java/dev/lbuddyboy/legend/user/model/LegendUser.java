package dev.lbuddyboy.legend.user.model;

import com.mojang.authlib.properties.Property;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.arrow.Arrow;
import dev.lbuddyboy.arrow.staffmode.StaffModeConstants;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.util.skin.Skin;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.LegendConstants;
import dev.lbuddyboy.legend.settings.Setting;
import dev.lbuddyboy.legend.team.model.TeamType;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Data
public class LegendUser {

    private final UUID uuid;
    private String name;
    private int kills, deaths, lives, credits, currentKillStreak, highestKillStreak;
    private ChatMode chatMode = ChatMode.PUBLIC;
    private long playTime, lastOnline;
    private boolean playedBefore = false, deathBanned = false;
    private double balance;

    private final Map<Setting, Boolean> settings = new HashMap<>();
    private final Map<String, PersistentTimer> timers = new HashMap<>();

    private transient long joinedAt, lastBrokenAt;
    private transient Location lastBrokenLocation, lastBrokenBlock;
    private transient Skin skin;

    public LegendUser(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.joinedAt = System.currentTimeMillis();
    }

    public LegendUser(Document document) {
        this.uuid = UUID.fromString(document.getString("uuid"));
        this.name = document.getString("name");
        this.kills = document.getInteger("kills");
        this.deaths = document.getInteger("deaths");
        this.balance = document.get("balance", 0D);
        this.lastOnline = document.get("lastOnline", 0L);
        this.currentKillStreak = document.getInteger("currentKillStreak", 0);
        this.highestKillStreak = document.getInteger("highestKillStreak", 0);
        this.credits = document.getInteger("credits", 0);
        this.lives = document.getInteger("lives");
        this.playTime = document.getLong("playTime");
        this.deathBanned = document.getBoolean("deathBanned", false);
        this.playedBefore = document.getBoolean("playedBefore", false);
        this.chatMode = ChatMode.valueOf(document.getString("chatMode"));

        Document settingsDocument = Document.parse(document.getString("settings")), timersDocument = Document.parse(document.getString("timers"));

        settingsDocument.forEach((key, value) -> settings.put(Setting.valueOf(key), (Boolean) value));
        timersDocument.forEach((key, value) -> timers.put(key, new PersistentTimer(Document.parse((String) value))));
    }

    public Document toDocument() {
        Document document = new Document(), settingsDocument = new Document(), timersDocument = new Document();

        this.settings.forEach((key, value) -> settingsDocument.put(key.name(), value));
        this.timers.forEach((key, value) -> timersDocument.put(key, value.toDocument().toJson()));

        document.put("uuid", this.uuid.toString());
        document.put("name", this.name);
        document.put("lastOnline", this.lastOnline);
        document.put("kills", this.kills);
        document.put("balance", this.balance);
        document.put("deaths", this.deaths);
        document.put("credits", this.credits);
        document.put("lives", this.lives);
        document.put("currentKillStreak", this.currentKillStreak);
        document.put("highestKillStreak", this.highestKillStreak);
        document.put("deathBanned", this.deathBanned);
        document.put("playTime", this.playTime);
        document.put("playedBefore", this.playedBefore);
        document.put("chatMode", this.chatMode.name());
        document.put("settings", settingsDocument.toJson());
        document.put("timers", timersDocument.toJson());

        return document;
    }

    public boolean isOnline() {
        return LegendBukkit.getInstance().getServer().getPlayer(this.uuid) != null;
    }

    public long getCurrentPlayTime() {
        if (isOnline()) return this.playTime + getActivePlayTime();

        return this.playTime;
    }

    public long getTotalPlayTime() {
        long playTime = this.getCurrentPlayTime();
        Player player = Bukkit.getPlayer(this.uuid);

        if (player != null && !player.hasMetadata(StaffModeConstants.VANISH_META_DATA)) {
            playTime -= this.getActivePlayTime();
        }

        return playTime;
    }

    public long getActivePlayTime() {
        return System.currentTimeMillis() - this.joinedAt;
    }

    public boolean isTimerActive(String id) {
        PersistentTimer timer = this.timers.getOrDefault(id, null);

        return timer != null && timer.getRemaining() > 0L;
    }

    public long getRemainingCooldown(String id) {
        PersistentTimer timer = this.timers.getOrDefault(id, null);

        return timer == null ? 0L: timer.getRemaining();
    }

    public String getRemaining(String id) {
        long l = this.timers.get(id).getRemaining();


        return TimeUtils.formatIntoMMSS((int) (l / 1000));
    }

    public String getRemainingSeconds(String id) {
        long l = this.timers.get(id).getRemaining();

        return (l / 1000) + "s";
    }

    public PersistentTimer getTimer(String id) {
        return this.timers.get(id);
    }

    public void increaseTimer(String id, long time) {
        PersistentTimer timer = this.timers.getOrDefault(id, null);
        if (timer == null) return;

        timer.setDuration(timer.getDuration() + time);
    }

    public void pauseTimer(String id) {
        PersistentTimer timer = this.timers.getOrDefault(id, null);
        if (timer == null) return;

        timer.pause();
    }

    public void resumeTimer(String id) {
        PersistentTimer timer = this.timers.getOrDefault(id, null);
        if (timer == null) return;

        timer.unpause();
    }

    public void removeTimer(String id) {
        this.timers.remove(id);
    }

    public void decreaseTimer(String id, long time) {
        increaseTimer(id, -time);
    }

    public void applyTimer(String id, long time) {
        PersistentTimer timer = new PersistentTimer(id, time);

        this.timers.put(id, timer);

        if (!id.equalsIgnoreCase("pvp_timer")) return;

        Player player = Bukkit.getPlayer(this.uuid);
        if (player == null) return;

        LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getTeam(player.getLocation()).ifPresent(team -> {
            if (team.getTeamType() != TeamType.SPAWN) return;

            timer.pause();
        });
    }

    public double getKDR() {
        if (getDeaths() == 0) return getKills();

        return (double) getKills() / getDeaths();
    }

    public String getKDRFormatted() {
        ChatColor color = ChatColor.GREEN;
        if (getKDR() < 1) {
            color = ChatColor.RED;
        }
        return color + LegendConstants.KDR_FORMAT.format(getKDR());
    }

    public void setKillStreak(int killstreak) {
        if (killstreak > getHighestKillStreak()) setHighestKillStreak(killstreak);

        this.currentKillStreak = killstreak;
    }

    public void delete(boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> delete(false));
            return;
        }

        LegendBukkit.getInstance().getUserHandler().getCollection().deleteOne(Filters.eq("uuid", this.uuid.toString()));
    }

    public void save(boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> save(false));
            return;
        }

        LegendBukkit.getInstance().getUserHandler().getCollection().replaceOne(Filters.eq("uuid", this.uuid.toString()), toDocument(), new ReplaceOptions().upsert(true));
    }

    public String applyPlaceholders(String s) {
        return s
                .replaceAll("%player%", this.name)
                .replaceAll("%player_chat_mode%", this.chatMode.getName())
                .replaceAll("%player_credits%", APIConstants.formatNumber(this.credits))
                .replaceAll("%player_balance%", APIConstants.formatNumber(this.balance))
                .replaceAll("%player_playtime%", TimeUtils.formatIntoDetailedString(getTotalPlayTime()))
                .replaceAll("%player_playtime_short%", TimeUtils.formatIntoDetailedStringShort((int) (playTime / 1000)))
                .replaceAll("%player_playtime_shorter%", TimeUtils.formatIntoDetailedStringShorter((int) (playTime / 1000)))
                .replaceAll("%player_killstreak%", APIConstants.formatNumber(this.currentKillStreak))
                .replaceAll("%player_highest_killstreak%", APIConstants.formatNumber(this.highestKillStreak))
                .replaceAll("%player_kills%", APIConstants.formatNumber(this.kills))
                .replaceAll("%player_kdr%", LegendConstants.KDR_FORMAT.format(getKDR()))
                .replaceAll("%player_kdr_formatted%", getKDRFormatted())
                .replaceAll("%player_deaths%", APIConstants.formatNumber(this.deaths))
                .replaceAll("%player_lives%", APIConstants.formatNumber(this.lives));
    }

    public void setKills(int kills) {
        this.kills = kills;
        Arrow.getInstance().
    }

    public Skin getSkin() {
        if (skin != null) return skin;
        Player player = Bukkit.getPlayer(this.uuid);
        if (player == null) return Skin.DEFAULT_SKIN;

        Optional<Property> onlineTexturesOpt = ((CraftPlayer) player).getProfile().getProperties().get("textures").stream().findFirst();

        this.skin = onlineTexturesOpt.map(property -> new Skin(property.getSignature(), property.getValue())).orElse(Skin.DEFAULT_SKIN);
        return skin;
    }

}
