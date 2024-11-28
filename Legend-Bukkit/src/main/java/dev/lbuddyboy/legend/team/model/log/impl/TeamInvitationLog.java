package dev.lbuddyboy.legend.team.model.log.impl;

import dev.lbuddyboy.legend.team.model.log.TeamLog;
import dev.lbuddyboy.legend.team.model.log.TeamLogType;
import lombok.Getter;
import org.bson.Document;

import javax.print.Doc;
import java.util.UUID;

@Getter
public class TeamCreationLog extends TeamLog {

    private final UUID sender;

    public TeamCreationLog(String action, TeamLogType type, UUID sender) {
        super(action, type);

        this.sender = sender;
    }

    public TeamCreationLog(Document document) {
        super(document);
        this.sender = UUID.fromString(document.getString("sender"));
    }

    @Override
    public Document toDocument() {
        Document document = super.toDocument();

        document.put("sender", this.sender.toString());

        return document;
    }
}
