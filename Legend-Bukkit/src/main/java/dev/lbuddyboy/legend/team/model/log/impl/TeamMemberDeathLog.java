package dev.lbuddyboy.legend.team.model.log.impl;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.team.model.log.TeamLog;
import dev.lbuddyboy.legend.team.model.log.TeamLogType;
import dev.lbuddyboy.legend.util.UUIDUtils;
import lombok.Getter;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

@Getter
public class TeamMemberDeathLog extends TeamLog {

    private final UUID victimUUID;

    public TeamMemberDeathLog(UUID victimUUID, String action) {
        super(action, TeamLogType.MEMBER_DEATH);

        this.victimUUID = victimUUID;
    }

    public TeamMemberDeathLog(Document document) {
        super(document);
        this.victimUUID = UUID.fromString(document.getString("victimUUID"));
    }

    @Override
    public Document toDocument() {
        Document document = super.toDocument();

        document.put("victimUUID", this.victimUUID.toString());

        return document;
    }

    @Override
    public List<String> getLog() {
        List<String> log = super.getLog();

        log.add(" &dVictim&7: &f" + UUIDUtils.name(this.victimUUID));

        return log;
    }

}
