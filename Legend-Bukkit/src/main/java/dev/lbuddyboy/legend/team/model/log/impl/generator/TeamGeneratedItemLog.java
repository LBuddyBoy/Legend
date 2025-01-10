package dev.lbuddyboy.legend.team.model.log.impl.generator;

import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.team.model.log.TeamLog;
import dev.lbuddyboy.legend.team.model.log.TeamLogType;
import lombok.Getter;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

@Getter
public class TeamGeneratedItemLog extends TeamLog {

    private final String itemString;

    public TeamGeneratedItemLog(UUID teamId, String itemString) {
        super(teamId, "&6" + ItemUtils.getName(ItemUtils.itemStackFromBase64(itemString)) + " &ewas generated!", TeamLogType.GENERATED_ITEM);

        this.itemString = itemString;
    }

    public TeamGeneratedItemLog(Document document) {
        super(document);
        this.itemString = document.getString("itemString");
    }

    @Override
    public Document toDocument() {
        Document document = super.toDocument();

        document.put("itemString", this.itemString);

        return document;
    }

    @Override
    public List<String> getLog() {
        List<String> log = super.getLog();

        log.add(" &dItem&7: &f" + ItemUtils.getName(ItemUtils.itemStackFromBase64(itemString)));

        return log;
    }

}
