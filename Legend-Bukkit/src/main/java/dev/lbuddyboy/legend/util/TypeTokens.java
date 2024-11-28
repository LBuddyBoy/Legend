package dev.lbuddyboy.legend.util;

import com.google.gson.reflect.TypeToken;
import dev.lbuddyboy.legend.team.model.TeamMember;
import dev.lbuddyboy.legend.team.model.claim.Claim;

import java.util.List;
import java.util.UUID;

public class TypeTokens {

    public static final TypeToken<List<TeamMember>> TEAM_MEMBERS = new TypeToken<List<TeamMember>>() {};
    public static final TypeToken<List<Claim>> CLAIMS = new TypeToken<List<Claim>>() {};
    public static final TypeToken<List<UUID>> UUIDS = new TypeToken<List<UUID>>() {};

}
