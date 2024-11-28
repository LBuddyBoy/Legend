package dev.lbuddyboy.legend.team.model.log.impl;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.legend.team.model.log.TeamLog;
import dev.lbuddyboy.legend.team.model.log.TeamLogType;
import dev.lbuddyboy.legend.util.UUIDUtils;
import lombok.Getter;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

@Getter
public class TeamPointsChangeLog extends TeamLog {

    private final int previousPoints, afterPoints;
    private UUID playerUUID;
    private final ChangeCause cause;

    public TeamPointsChangeLog(int previousPoints, int afterPoints, UUID playerUUID, ChangeCause cause) {
        super((previousPoints > afterPoints ? "&a" : "&c") + APIConstants.formatNumber(previousPoints) + " -> " + APIConstants.formatNumber(afterPoints) + (playerUUID == null ? "" : " &7(Caused by " + UUIDUtils.name(playerUUID) + " )"), TeamLogType.POINTS_CHANGED);

        this.previousPoints = previousPoints;
        this.afterPoints = afterPoints;
        this.playerUUID = playerUUID;
        this.cause = cause;
    }

    public TeamPointsChangeLog(Document document) {
        super(document);
        this.previousPoints = document.getInteger("previousPoints");
        this.afterPoints = document.getInteger("afterPoints");
        this.cause = ChangeCause.valueOf(document.getString("cause"));
        if (document.containsKey("playerUUID")) this.playerUUID = UUID.fromString(document.getString("playerUUID"));
    }

    @Override
    public Document toDocument() {
        Document document = super.toDocument();

        document.put("previousPoints", this.previousPoints);
        document.put("afterPoints", this.afterPoints);
        document.put("cause", this.cause.name());
        if (this.playerUUID != null) document.put("playerUUID", this.playerUUID.toString());

        return document;
    }

    @Override
    public List<String> getLog() {
        List<String> log = super.getLog();

        log.add(" &dPrevious Points&7: &f" + APIConstants.formatNumber(this.previousPoints));
        log.add(" &dAfter Points&7: &f" + APIConstants.formatNumber(this.afterPoints));

        if (this.playerUUID != null) {
            log.add(" &dChanged By&7: &f" + UUIDUtils.name(this.playerUUID));
        }

        log.add(" &dCause&7: &f" + this.cause.name());

        return log;
    }

    public enum ChangeCause {

        NATURAL, FORCED, KILL, DEATH, KOTH, CITADEL, CONQUEST

    }

}
