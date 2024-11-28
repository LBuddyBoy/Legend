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
public class TeamInvitationRevokedLog extends TeamLog {

    private final UUID sender, target;

    public TeamInvitationRevokedLog(UUID sender, UUID target) {
        super("&6" + UUIDUtils.name(sender) + " &erevoked &6" + UUIDUtils.name(target) + "&e's invitation!", TeamLogType.INVITATION_REVOKED);

        this.sender = sender;
        this.target = target;
    }

    public TeamInvitationRevokedLog(Document document) {
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

    @Override
    public List<String> getLog() {
        List<String> log = super.getLog();

        log.add(" &dSender&7: &f" + UUIDUtils.name(this.sender));
        log.add(" &dTarget&7: &f" + UUIDUtils.name(this.target));

        return log;
    }

}
