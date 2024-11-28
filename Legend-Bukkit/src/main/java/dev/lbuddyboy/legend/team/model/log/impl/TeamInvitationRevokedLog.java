package dev.lbuddyboy.legend.team.model.log.impl;

import dev.lbuddyboy.legend.team.model.log.TeamLog;
import dev.lbuddyboy.legend.team.model.log.TeamLogType;
import dev.lbuddyboy.legend.util.UUIDUtils;
import lombok.Getter;
import org.bson.Document;

import java.util.UUID;

@Getter
public class TeamInvitationLog extends TeamLog {

    private final UUID sender, target;

    public TeamInvitationLog(UUID sender, UUID target) {
        super("&6" + UUIDUtils.name(sender) + " &einvited &6" + UUIDUtils.name(target) + " &eto join the team!", TeamLogType.INVITATION);

        this.sender = sender;
        this.target = target;
    }

    public TeamInvitationLog(Document document) {
        super(document);
        this.sender = UUID.fromString(document.getString("sender"));
        this.target = UUID.fromString(document.getString("target"));
    }

    @Override
    public Document toDocument() {
        Document document = super.toDocument();

        document.put("sender", this.sender.toString());
        document.put("target", this.target.toString());

        return document;
    }
}
