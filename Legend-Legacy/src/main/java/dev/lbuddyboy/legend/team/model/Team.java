package dev.lbuddyboy.legend.team.model;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.LocationUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.claim.Claim;
import dev.lbuddyboy.legend.util.PlaceholderUtil;
import dev.lbuddyboy.legend.util.TypeTokens;
import lombok.Data;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

@Data
public class Team {

    private final UUID id;

    private String name;
    private List<TeamMember> members = new ArrayList<>();
    private List<Claim> claims = new ArrayList<>();
    private TeamType teamType = TeamType.PLAYER;

    private long dtrRegen, dtrRegenDuration;
    private double deathsUntilRaidable = 1.0D;
    private Location home = null;
    private Map<UUID, TeamRole> roster = new HashMap<>();
    private int points;

    private transient List<UUID> invitations = new ArrayList<>();
    private transient boolean edited = false;
    private transient long lastRegenerated = 0L;
    private final transient Map<TopType, Integer> places = new HashMap<>();

    public Team(UUID id, String name) {
        this.id = id;
        this.name = name;

        flagSave();
    }

    public Team(Document document) {
        this.id = UUID.fromString(document.getString("id"));
        this.name = document.getString("name");
        this.members.addAll(APIConstants.GSON.fromJson(document.getString("members"), TypeTokens.TEAM_MEMBERS.getType()));
        this.claims.addAll(APIConstants.GSON.fromJson(document.getString("claims"), TypeTokens.CLAIMS.getType()));
        this.dtrRegen = document.getLong("dtrRegen");
        this.deathsUntilRaidable = document.getDouble("deathsUntilRaidable");
        this.teamType = TeamType.valueOf(document.getString("teamType"));
        this.points = document.getInteger("points", 0);
        if (document.containsKey("home")) this.home = LocationUtils.deserializeString(document.getString("home"));
        if (document.containsKey("roster")) Document.parse(document.getString("roster")).forEach((k, v) -> this.roster.put(UUID.fromString(k), TeamRole.valueOf((String) v)));
    }

    public Document toDocument() {
        Document document = new Document(), rosterDocument = new Document();

        this.roster.forEach((k, v) -> rosterDocument.put(k.toString(), v.name()));

        document.put("id", this.id.toString());
        document.put("name", this.name);
        document.put("members", APIConstants.GSON.toJson(this.members, TypeTokens.TEAM_MEMBERS.getType()));
        document.put("claims", APIConstants.GSON.toJson(this.claims, TypeTokens.CLAIMS.getType()));
        document.put("dtrRegen", this.dtrRegen);
        document.put("deathsUntilRaidable", this.deathsUntilRaidable);
        document.put("points", this.points);
        document.put("teamType", this.teamType.name());
        document.put("roster", rosterDocument.toJson());

        if (home != null) document.put("home", LocationUtils.serializeString(this.home));

        return document;
    }

    public Optional<TeamMember> getMember(UUID playerUUID) {
        return this.members.stream().filter(member -> member.getUuid().equals(playerUUID)).findFirst();
    }

    public boolean isFullyRegenerated() {
        return this.deathsUntilRaidable >= getMaxDTR();
    }

    public boolean isDTRFrozen() {
        return getDTRFreezeRemaining() > 0;
    }

    public long getDTRFreezeRemaining() {
        return (this.dtrRegen + this.dtrRegenDuration) - System.currentTimeMillis();
    }

    public void applyDTRFreeze(long duration) {
        this.dtrRegen = System.currentTimeMillis();
        this.dtrRegenDuration = duration;
        flagSave();
    }

    public Optional<TeamMember> getLeader() {
        return this.members.stream().filter(member -> member.getRole() == TeamRole.LEADER).findFirst();
    }

    public List<TeamMember> getOnlineMembers() {
        return this.members.stream().filter(TeamMember::isOnline).toList();
    }

    public List<TeamMember> getOfflineMembers() {
        return this.members.stream().filter(m -> !m.isOnline()).toList();
    }

    public List<Player> getOnlinePlayers() {
        return getOnlineMembers().stream().map(TeamMember::getPlayer).toList();
    }

    public boolean isMember(UUID check) {
        return this.getMembersByUUID().contains(check);
    }

    public boolean isLeader(UUID check) {
        return getMember(check).map(m -> m.getRole() == TeamRole.LEADER).orElse(false);
    }

    public void addMember(TeamMember member) {
        this.invitations.remove(member.getUuid());
        removeMember(member.getUuid());
        this.members.add(member);
        flagSave();
    }

    public int getMaxDTR() {
        return Math.min(this.members.size() + 1, LegendBukkit.getInstance().getSettings().getInt("team.maximum-dtr"));
    }

    public String getDTRSymbol() {
        if (isRaidable()) return "-";
        if (!isDTRFrozen() && this.deathsUntilRaidable < getMaxDTR()) return "+";
        if (isDTRFrozen()) return "#";

        return "=";
    }

    public String getDTRColor() {
        if (this.deathsUntilRaidable < 0) return LegendBukkit.getInstance().getLanguage().getString("team.dtr.raidable-color");
        if (this.deathsUntilRaidable > 0 && this.deathsUntilRaidable <= 1) return LegendBukkit.getInstance().getLanguage().getString("team.dtr.one-dtr-color");

        return LegendBukkit.getInstance().getLanguage().getString("team.dtr.normal-color");
    }

    public void removeMember(UUID playerUUID) {
        this.members.removeIf(member -> member.getUuid().equals(playerUUID));
        flagSave();
    }

    public long getNextDTRRegen() {
        return (this.lastRegenerated + (LegendBukkit.getInstance().getSettings().getInt("team.regeneration.speed") * 1000L)) - System.currentTimeMillis();
    }

    public List<UUID> getMembersByUUID() {
        return this.members.stream().map(TeamMember::getUuid).toList();
    }

    public void playSound(Sound sound, float volume) {
        getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), sound, volume, volume));
    }

    public void sendMessage(String message) {
        getOnlinePlayers().forEach(p -> p.sendMessage(CC.translate(PlaceholderUtil.applyTeamPlaceholders(message, this))));
    }

    public void regenDTR(double amount) {
        this.deathsUntilRaidable += amount;
        this.lastRegenerated = System.currentTimeMillis();

        playSound(Sound.ENTITY_PLAYER_LEVELUP, 1.0f);

        if (isFullyRegenerated()) {
            sendMessage(LegendBukkit.getInstance().getLanguage().getString("team.regeneration.fully-regenerated")
                    .replaceAll("%dtr%", APIConstants.formatNumber(amount))
            );
            return;
        }

        sendMessage(LegendBukkit.getInstance().getLanguage().getString("team.regeneration.regenerated")
                .replaceAll("%dtr%", APIConstants.formatNumber(amount))
        );
    }

    public Optional<TeamMember> getRosterReplacement(UUID playerUUID) {
        if (!roster.containsKey(playerUUID)) return Optional.empty();

        TeamRole role = roster.get(playerUUID);

        return this.members.stream().filter(m -> !m.isOnline() && !m.getRole().isHigher(role)).findFirst();
    }

    public boolean isRaidable() {
        return this.deathsUntilRaidable < 0;
    }

    public void setHome(Location location) {
        this.home = location;
        flagSave();
    }

    public void setPoints(int amount) {
        this.points = amount;
        flagSave();
    }

    public String getName(Player player) {
        if (teamType == TeamType.SPAWN) return LegendBukkit.getInstance().getLanguage().getString("team.system.names.spawn");
        if (teamType == TeamType.ROAD) return LegendBukkit.getInstance().getLanguage().getString("team.system.names.road").replaceAll("%team%", this.name);
        if (getMember(player.getUniqueId()).isPresent()) return ChatColor.DARK_GREEN + this.name;

        return ChatColor.DARK_RED + this.name;
    }

    public void flagSave() {
        this.edited = true;
    }

    public String applyPlaceholders(String s, Player player) {
        return s
                .replaceAll("%dtr%", APIConstants.formatNumber(getDeathsUntilRaidable()))
                .replaceAll("%dtr-regeneration%", isDTRFrozen() ? " - " + TimeUtils.formatIntoDetailedString(getDTRFreezeRemaining()) : "")
                .replaceAll("%dtr-symbol%", getDTRSymbol())
                .replaceAll("%dtr-color%", getDTRColor())
                .replaceAll("%online%", String.valueOf(getOnlineMembers().size()))
                .replaceAll("%total%", String.valueOf(getMembers().size()))
                .replaceAll("%online-members%", StringUtils.join(getOnlineMembers().stream().map(TeamMember::getDisplayName).toList(), ", "))
                .replaceAll("%offline-members%", StringUtils.join(getOfflineMembers().stream().map(TeamMember::getDisplayName).toList(), ", "))
                .replaceAll("%members%", StringUtils.join(getMembers().stream().map(TeamMember::getDisplayName).toList(), ", "))
                .replaceAll("%home%", (getHome() == null ? "N/A" : LocationUtils.toString(getHome())))
                .replaceAll("%team%", getName(player));
    }
    
}
