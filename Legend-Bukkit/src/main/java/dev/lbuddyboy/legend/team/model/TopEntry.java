package dev.lbuddyboy.legend.team.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Data
public class TopEntry {

    private TopType type;
    private UUID teamUUID;
    private int value;
    private int place;

}
