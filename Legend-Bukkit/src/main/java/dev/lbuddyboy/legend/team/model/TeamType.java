package dev.lbuddyboy.legend.team.model;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.claim.Claim;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;

import java.util.Comparator;

@AllArgsConstructor
@Getter
public enum TeamType {

    PLAYER(1000),
    SPAWN(1),
    ROAD(200),
    ENDPORTAL(150),
    KOTH(150),
    CTP(150),
    DTC(150),
    CITADEL(150),
    DEATHBAN_ARENA(1250),
    ORE_MOUNTAIN(500),
    GLOWSTONE_MOUNTAIN(500),
    LOOTHILL(500);

    private final int weight;

    public boolean appliesAt(Location location) {
        Team team = LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getTeam(location).orElse(null);
        if (team == null) return false;

        return team.getTeamType() == this;
    }

    public static class TeamTypeComparator implements Comparator<Team> {

        @Override
        public int compare(Team o1, Team o2) {
            return Integer.compare(o1.getTeamType().getWeight(), o2.getTeamType().getWeight());
        }
    }

    public static class TeamTypeClaimComparator implements Comparator<Claim> {


        @Override
        public int compare(Claim o1, Claim o2) {
            Team t1 = o1.getTeam().orElse(null), t2 = o2.getTeam().orElse(null);
            int w1 = t1 == null ? 10000 : t1.getTeamType().getWeight();
            int w2 = t2 == null ? 10000 : t2.getTeamType().getWeight();

            return Integer.compare(w1, w2);
        }
    }

}
