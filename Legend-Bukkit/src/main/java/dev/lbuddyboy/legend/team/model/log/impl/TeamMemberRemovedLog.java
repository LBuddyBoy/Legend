package dev.lbuddyboy.legend.team.model.log.impl;

import dev.lbuddyboy.legend.team.model.log.TeamLog;
import dev.lbuddyboy.legend.team.model.log.TeamLogType;
import dev.lbuddyboy.legend.util.UUIDUtils;
import lombok.Getter;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

@Getter
public class TeamRemoveLog extends TeamLog {

    private final UUID playerUUID;
    private final LeftFrom leftFrom;

    public TeamRemoveLog(UUID playerUUID, String title, LeftFrom leftFrom) {
        super(title, TeamLogType.INVITATION);

        this.playerUUID = playerUUID;
        this.leftFrom = leftFrom;
    }

    public TeamRemoveLog(Document document) {
        super(document);
        this.playerUUID = UUID.fromString(document.getString("playerUUID"));
        this.leftFrom = LeftFrom.valueOf(document.getString("leftFrom"));
    }

    @Override
    public Document toDocument() {
        Document document = super.toDocument();

        document.put("playerUUID", this.playerUUID.toString());
        document.put("leftFrom", this.leftFrom.name());

        return document;
    }

    @Override
    public List<String> getLog() {
        List<String> log = super.getLog();

        log.add(" &dPlayer&7: &f" + UUIDUtils.name(this.playerUUID));
        log.add(" &dFrom&7: &f" + this.leftFrom.name());

        return log;
    }

    public enum LeftFrom {

        KICKED, LEFT, FORCE_LEFT

    }

}
