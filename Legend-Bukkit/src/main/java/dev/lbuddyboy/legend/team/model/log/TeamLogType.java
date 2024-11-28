package dev.lbuddyboy.legend.team.model.log;

import dev.lbuddyboy.legend.team.model.log.impl.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.Document;

import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum TeamLogType {

    CREATED("&a&lCREATED", TeamCreationLog::new),
    MEMBER_ADDED("&a&lMEMBER ADDED", TeamMemberAddedLog::new),
    MEMBER_REMOVED("&a&lMEMBER REMOVED", TeamMemberRemovedLog::new),
    DTR_CHANGED("&e&lDTR CHANGED", TeamDTRChangeLog::new),
    POINTS_CHANGED("&6&lPOINTS CHANGED", TeamPointsChangeLog::new),
    MEMBER_DEATH("&c&lMEMBER DEATH", TeamMemberDeathLog::new),
    INVITATION("&a&lINVITATION", TeamInvitationLog::new),
    INVITATION_REVOKED("&c&lINVITATION REVOKED", TeamInvitationRevokedLog::new);

    private final String displayName;
    private final Function<Document, TeamLog> creationConsumer;

}
