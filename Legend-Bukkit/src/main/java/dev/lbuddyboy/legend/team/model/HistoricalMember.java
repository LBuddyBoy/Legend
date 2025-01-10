package dev.lbuddyboy.legend.team.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class HistoricalMember {

    private final UUID playerUUID;
    private long leftAt;

}
