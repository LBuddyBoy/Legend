package dev.lbuddyboy.legend.team.model.log.impl;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.legend.LegendConstants;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.log.TeamLog;
import dev.lbuddyboy.legend.team.model.log.TeamLogType;
import dev.lbuddyboy.legend.util.UUIDUtils;
import lombok.Getter;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

@Getter
public class TeamDTRLog extends TeamLog {

    private final double previousDTR, afterDTR;
    private final UUID playerUUID;
    private final ChangeCause cause;

    public TeamDTRLog(double previousDTR, double afterDTR, UUID playerUUID, ChangeCause cause) {
        super((previousDTR > afterDTR ? "&a" : "&c") + APIConstants.formatNumber(previousDTR) + " -> " + APIConstants.formatNumber(afterDTR) + (playerUUID == null ? "" : " &7(Caused by " + UUIDUtils.name(playerUUID) + " )"), TeamLogType.DTR_CHANGED);


        this.previousDTR = previousDTR;
        this.afterDTR = afterDTR;
        this.playerUUID = playerUUID;
        this.cause = cause;
    }

    public TeamDTRLog(Document document) {
        super(document);
        this.previousDTR = document.getDouble("previousDTR");
        this.afterDTR = document.getDouble("afterDTR");
        this.playerUUID = UUID.fromString(document.getString("playerUUID"));
        this.cause = ChangeCause.valueOf(document.getString("cause"));
    }

    @Override
    public Document toDocument() {
        Document document = super.toDocument();

        document.put("previousDTR", this.previousDTR);
        document.put("afterDTR", this.afterDTR);
        document.put("playerUUID", this.playerUUID.toString());
        document.put("cause", this.cause.name());

        return document;
    }

    @Override
    public List<String> getLog() {
        List<String> log = super.getLog();

        log.add(" &dPrevious DTR&7: &f" + APIConstants.formatNumber(this.previousDTR));
        log.add(" &dAfter DTR&7: &f" + APIConstants.formatNumber(this.afterDTR));

        if (this.playerUUID != null) {
            log.add(" &dChanged By&7: &f" + UUIDUtils.name(this.playerUUID));
        }

        log.add(" &dCause&7: &f" + this.cause.name());

        return log;
    }

    public enum ChangeCause {

        NATURAL_REGEN, FORCED, MEMBER_DEATH, LOGGER_DEATH, MEMBER_LEFT

    }

}
