package dev.lbuddyboy.legend.team.model;

import dev.lbuddyboy.legend.team.model.claim.Claim;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Comparator;

@AllArgsConstructor
@Getter
public enum TeamType {

    PLAYER(1000),
    SPAWN(1),
    ROAD(200),
    KOTH(150),
    DTC(150),
    CITADEL(150),
    DEATHBAN_ARENA(1250),
    GLOWSTONE_MOUNTAIN(500);

    private final int weight;

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
