package dev.lbuddyboy.legend.team.model;

import dev.lbuddyboy.legend.LegendBukkit;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

@AllArgsConstructor
@Getter
public enum TeamRole {

    MEMBER("Member", "-", 100),
    CAPTAIN("Captain", "*", 200),
    CO_LEADER("Co Leader", "**", 300),
    LEADER("Leader", "***", 400);

    private final String displayName;
    private final String astrix;
    private final int weight;

    public boolean isGreaterOrEqual(TeamMember member, TeamRole role) {
        return member.getRole().getWeight() >= role.getWeight();
    }

    public boolean isHigher(TeamRole role) {
        return this.weight > role.getWeight();
    }

    public boolean isLesser(TeamRole role) {
        return this.weight < role.getWeight();
    }

    public boolean isLesserOrEqual(TeamRole role) {
        return this.weight <= role.getWeight();
    }

    public boolean isHigherOrEqual(TeamRole role) {
        return this.weight >= role.getWeight();
    }

    public TeamRole next() {
        if (this == LEADER) return LEADER;

        return TeamRole.values()[ordinal() + 1];
    }

    public TeamRole previous() {
        if (this == MEMBER) return MEMBER;

        return TeamRole.values()[ordinal() - 1];
    }

}
