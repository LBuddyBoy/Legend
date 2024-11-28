package dev.lbuddyboy.legend.team.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

@AllArgsConstructor
@Getter
public enum TopType {

    POINTS("Points", Team::getPoints);

    private final String displayName;
    private Function<Team, Integer> getter;

    public void update(Team team, int place) {
        team.getPlaces().put(this, place);
    }

}
