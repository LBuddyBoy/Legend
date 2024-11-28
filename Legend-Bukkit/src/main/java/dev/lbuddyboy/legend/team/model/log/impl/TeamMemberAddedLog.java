package dev.lbuddyboy.legend.team.model.log.impl;

import dev.lbuddyboy.legend.team.model.log.TeamLog;
import dev.lbuddyboy.legend.team.model.log.TeamLogType;
import dev.lbuddyboy.legend.util.UUIDUtils;
import lombok.Getter;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

@Getter
public class TeamMemberAddedLog extends TeamLog {

    private final UUID playerUUID;
    private final JoinedFrom joinedFrom;

    public TeamMemberAddedLog(UUID playerUUID, String title, JoinedFrom joinedFrom) {
        super(title, TeamLogType.MEMBER_ADDED);

        this.playerUUID = playerUUID;
        this.joinedFrom = joinedFrom;
    }

    public TeamMemberAddedLog(Document document) {
        super(document);
        this.playerUUID = UUID.fromString(document.getString("playerUUID"));
        this.joinedFrom = JoinedFrom.valueOf(document.getString("joinedFrom"));
    }

    @Override
    public Document toDocument() {
        Document document = super.toDocument();

        document.put("playerUUID", this.playerUUID.toString());
        document.put("joinedFrom", this.joinedFrom.name());

        return document;
    }

    @Override
    public List<String> getLog() {
        List<String> log = super.getLog();

        log.add(" &dPlayer&7: &f" + UUIDUtils.name(this.playerUUID));
        log.add(" &dFrom&7: &f" + this.joinedFrom.name());

        return log;
    }

    public enum JoinedFrom {

        INVITE, ROSTER, FORCE_JOIN

    }

}
