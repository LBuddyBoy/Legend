package dev.lbuddyboy.legend.team.model.log;

import dev.lbuddyboy.legend.team.model.log.impl.*;
import dev.lbuddyboy.legend.team.model.log.impl.generator.TeamGeneratedItemClaimLog;
import dev.lbuddyboy.legend.team.model.log.impl.generator.TeamGeneratedItemExpireLog;
import dev.lbuddyboy.legend.team.model.log.impl.generator.TeamGeneratedItemLog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.Document;

import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum TeamLogType {

    GENERATED_ITEM("&a&lITEM GENERATED", TeamGeneratedItemLog::new),
    GENERATED_ITEM_CLAIMED("&a&lITEM CLAIMED", TeamGeneratedItemClaimLog::new),
    GENERATOR_ITEM_EXPIRED("&c&lITEM EXPIRED", TeamGeneratedItemExpireLog::new),
    CREATED("&a&lCREATED", TeamCreationLog::new),
    MEMBER_ADDED("&a&lMEMBER ADDED", TeamMemberAddedLog::new),
    MEMBER_REMOVED("&a&lMEMBER REMOVED", TeamMemberRemovedLog::new),
    DTR_CHANGED("&e&lDTR CHANGED", TeamDTRChangeLog::new),
    NOW_RAIDABLE("&4&lNOW RAIDABLE", TeamNowRaidableLog::new),
    NO_LONGER_RAIDABLE("&c&lNO LONGER RAIDABLE", TeamNoLongerRaidableLog::new),
    POINTS_CHANGED("&6&lPOINTS CHANGED", TeamPointsChangeLog::new),
    MEMBER_DEATH("&c&lMEMBER DEATH", TeamMemberDeathLog::new),
    INVITATION("&a&lINVITATION", TeamInvitationLog::new),
    INVITATION_REVOKED("&c&lINVITATION REVOKED", TeamInvitationRevokedLog::new);

    private final String displayName;
    private final Function<Document, TeamLog> creationConsumer;

}
