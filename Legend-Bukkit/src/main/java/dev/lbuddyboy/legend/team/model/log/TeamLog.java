package dev.lbuddyboy.legend.team.model.log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.util.CC;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public abstract class TeamLog {

    private final UUID id;
    private final UUID teamId;
    private final String action;
    private final TeamLogType type;
    private final long loggedAt;

    public TeamLog(UUID teamId, String action, TeamLogType type) {
        this.teamId = teamId;
        this.action = action;
        this.id = UUID.randomUUID();
        this.type = type;
        this.loggedAt = System.currentTimeMillis();
    }

    public TeamLog(Document document) {
        this.action = document.getString("action");
        this.id = UUID.fromString(document.getString("id"));
        this.teamId = UUID.fromString(document.getString("teamId"));
        this.type = TeamLogType.valueOf(document.getString("type"));
        this.loggedAt = document.getLong("loggedAt");
    }

    public Document toDocument() {
        Document document = new Document();

        document.put("id", this.id.toString());
        document.put("teamId", this.teamId.toString());
        document.put("action", this.action);
        document.put("type", this.type.name());
        document.put("loggedAt", this.loggedAt);

        return document;
    }

    public String getTitle() {
        return CC.translate("&8[&7TEAM LOG&8] " + this.type.getDisplayName());
    }

    public List<String> getLog() {
        return new ArrayList<>(Arrays.asList(
                " &dAction&7: &f" + this.action,
                " &dDate&7: &f" + APIConstants.SDF.format(new Date(this.loggedAt))
        ));
    }

}
