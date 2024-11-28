package dev.lbuddyboy.legend.team.model.log.impl;

import de.tr7zw.nbtapi.utils.UUIDUtil;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.team.model.log.TeamLog;
import dev.lbuddyboy.legend.team.model.log.TeamLogType;
import dev.lbuddyboy.legend.util.UUIDUtils;
import lombok.Getter;
import org.bson.Document;

import javax.print.Doc;
import java.util.List;
import java.util.UUID;

@Getter
public class TeamCreationLog extends TeamLog {

    private final UUID sender;

    public TeamCreationLog(UUID sender) {
        super("&6" + UUIDUtils.name(sender) + " &acreated &ethe team!", TeamLogType.CREATED);

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

    @Override
    public List<String> getLog() {
        List<String> log = super.getLog();

        log.add(" &dSender&7: &f" + UUIDUtils.name(this.sender));

        return CC.translate(log);
    }

}
