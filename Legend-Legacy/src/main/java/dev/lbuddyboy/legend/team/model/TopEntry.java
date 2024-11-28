package dev.lbuddyboy.legend.team.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

public record TopEntry(TopType type, UUID teamUUID, int value, int place) {

}
