package dev.lbuddyboy.legend.team.model.log.impl;

import dev.lbuddyboy.legend.team.model.log.TeamLog;
import dev.lbuddyboy.legend.team.model.log.TeamLogType;
import dev.lbuddyboy.legend.util.UUIDUtils;
import lombok.Getter;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

@Getter
public class TeamMemberRemovedLog extends TeamLog {

    private final UUID playerUUID;
    private final LeftCause cause;

    public TeamMemberRemovedLog(UUID playerUUID, String title, LeftCause cause) {
        super(title, TeamLogType.MEMBER_REMOVED);

        this.playerUUID = playerUUID;
        this.cause = cause;
    }

    public TeamMemberRemovedLog(Document document) {
        super(document);
        this.playerUUID = UUID.fromString(document.getString("playerUUID"));
        this.cause = LeftCause.valueOf(document.getString("leftFrom"));
    }

    @Override
    public Document toDocument() {
        Document document = super.toDocument();

        document.put("playerUUID", this.playerUUID.toString());
        document.put("leftFrom", this.cause.name());

        return document;
    }

    @Override
    public List<String> getLog() {
        List<String> log = super.getLog();

        log.add(" &dPlayer&7: &f" + UUIDUtils.name(this.playerUUID));
        log.add(" &dCause&7: &f" + this.cause.name());

        return log;
    }

    public enum LeftCause {

        KICKED, LEFT, FORCE_LEFT

    }

}
