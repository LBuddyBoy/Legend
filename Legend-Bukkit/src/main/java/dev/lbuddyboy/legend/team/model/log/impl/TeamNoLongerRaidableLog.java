package dev.lbuddyboy.legend.team.model.log.impl;

import dev.lbuddyboy.legend.team.model.log.TeamLog;
import dev.lbuddyboy.legend.team.model.log.TeamLogType;
import dev.lbuddyboy.legend.util.UUIDUtils;
import lombok.Getter;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

@Getter
public class TeamNoLongerRaidableLog extends TeamLog {

    private final UUID sender;
    private final RaidableCause cause;

    public TeamNoLongerRaidableLog(UUID teamId, UUID sender, RaidableCause cause) {
        super(teamId, "&6" + UUIDUtils.name(sender) + " &emade team &cnot raidable!", TeamLogType.NO_LONGER_RAIDABLE);

        this.sender = sender;
        this.cause = cause;
    }

    public TeamNoLongerRaidableLog(Document document) {
        super(document);

        this.sender = UUID.fromString(document.getString("sender"));
        this.cause = RaidableCause.valueOf(document.getString("cause"));
    }

    @Override
    public Document toDocument() {
        Document document = super.toDocument();

        if (this.sender != null) {
            document.put("sender", this.sender.toString());
        }
        document.put("cause", this.cause.name());

        return document;
    }

    @Override
    public List<String> getLog() {
        List<String> log = super.getLog();

        log.add(" &dSender&7: &f" + (this.sender == null ? " &4&lCONSOLE" : UUIDUtils.name(this.sender)));
        log.add(" &dCaused By&7: &f" + this.cause.name());

        return log;
    }

    public enum RaidableCause {

        REGEN, SET

    }

}
