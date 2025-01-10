package dev.lbuddyboy.legend.team.model.log.impl;

import dev.lbuddyboy.legend.team.model.log.TeamLog;
import dev.lbuddyboy.legend.team.model.log.TeamLogType;
import dev.lbuddyboy.legend.util.UUIDUtils;
import lombok.Getter;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

@Getter
public class TeamNowRaidableLog extends TeamLog {

    private final UUID sender;
    private UUID killer;
    private final RaidableCause cause;

    public TeamNowRaidableLog(UUID teamId, UUID sender, UUID killer, RaidableCause cause) {
        super(teamId, "&6" + UUIDUtils.name(sender) + " &emade team &craidable!" + (killer == null ? "" : "Killed by " + UUIDUtils.name(killer) + ")"), TeamLogType.NOW_RAIDABLE);

        this.sender = sender;
        this.killer = killer;
        this.cause = cause;
    }

    public TeamNowRaidableLog(Document document) {
        super(document);
        this.sender = UUID.fromString(document.getString("sender"));
        this.cause = RaidableCause.valueOf(document.getString("cause"));
        if (document.containsKey("killer")) this.killer = UUID.fromString(document.getString("killer"));
    }

    @Override
    public Document toDocument() {
        Document document = super.toDocument();

        document.put("sender", this.sender.toString());
        document.put("cause", this.cause.name());
        if (this.killer != null) document.put("killer", this.killer.toString());

        return document;
    }

    @Override
    public List<String> getLog() {
        List<String> log = super.getLog();

        log.add(" &dSender&7: &f" + UUIDUtils.name(this.sender));
        log.add(" &dCaused By&7: &f" + this.cause.name());
        if (killer != null) log.add(" &dKiller&7: &f" + UUIDUtils.name(this.killer));

        return log;
    }

    public enum RaidableCause {

        PLAYER, SET

    }

}
