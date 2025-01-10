package dev.lbuddyboy.legend.team.model.log.impl.generator;

import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.team.model.log.TeamLog;
import dev.lbuddyboy.legend.team.model.log.TeamLogType;
import dev.lbuddyboy.legend.util.UUIDUtils;
import lombok.Getter;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

@Getter
public class TeamGeneratedItemClaimLog extends TeamLog {

    private final String itemString;
    private final UUID claimedBy;

    public TeamGeneratedItemClaimLog(UUID teamId, UUID claimedBy, String itemString) {
        super(teamId, "&6" + ItemUtils.getName(ItemUtils.itemStackFromBase64(itemString)) + "&e was &a&lCLAIMED&e by " + UUIDUtils.name(claimedBy) + "!", TeamLogType.GENERATED_ITEM_CLAIMED);

        this.itemString = itemString;
        this.claimedBy = claimedBy;
    }

    public TeamGeneratedItemClaimLog(Document document) {
        super(document);
        this.itemString = document.getString("itemString");
        this.claimedBy = UUID.fromString(document.getString("claimedBy"));
    }

    @Override
    public Document toDocument() {
        Document document = super.toDocument();

        document.put("itemString", this.itemString);
        document.put("claimedBy", this.claimedBy.toString());

        return document;
    }

    @Override
    public List<String> getLog() {
        List<String> log = super.getLog();

        log.add(" &dItem&7: &f" + ItemUtils.getName(ItemUtils.itemStackFromBase64(this.itemString)));
        log.add(" &dClaimed By&7: &f" + UUIDUtils.name(this.claimedBy));

        return log;
    }

}
