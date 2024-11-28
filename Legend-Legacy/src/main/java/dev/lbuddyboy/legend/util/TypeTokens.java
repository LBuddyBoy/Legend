package dev.lbuddyboy.legend.util;

import com.google.gson.reflect.TypeToken;
import dev.lbuddyboy.legend.team.model.TeamMember;
import dev.lbuddyboy.legend.team.model.claim.Claim;

import java.util.List;

public class TypeTokens {

    public static final TypeToken<List<TeamMember>> TEAM_MEMBERS = new TypeToken<>() {};
    public static final TypeToken<List<Claim>> CLAIMS = new TypeToken<>() {};

}
