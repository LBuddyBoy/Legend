package dev.lbuddyboy.legend.util;

import com.google.gson.reflect.TypeToken;
import dev.lbuddyboy.legend.team.model.HistoricalMember;
import dev.lbuddyboy.legend.team.model.TeamMember;
import dev.lbuddyboy.legend.team.model.brew.BrewData;
import dev.lbuddyboy.legend.team.model.claim.Claim;
import dev.lbuddyboy.legend.util.model.DocumentedItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TypeTokens {

    public static final TypeToken<List<TeamMember>> TEAM_MEMBERS = new TypeToken<List<TeamMember>>() {};
    public static final TypeToken<List<DocumentedItemStack>> DOCUMENTED_ITEMS = new TypeToken<List<DocumentedItemStack>>() {};
    public static final TypeToken<Map<String, Integer>> STRING_INT_MAP = new TypeToken<Map<String, Integer>>() {};
    public static final TypeToken<List<HistoricalMember>> HISTORICAL_MEMBERS = new TypeToken<List<HistoricalMember>>() {};
    public static final TypeToken<BrewData> BREW_DATA = new TypeToken<BrewData>() {};
    public static final TypeToken<List<Claim>> CLAIMS = new TypeToken<List<Claim>>() {};
    public static final TypeToken<List<UUID>> UUIDS = new TypeToken<List<UUID>>() {};

}
