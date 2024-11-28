package dev.lbuddyboy.legend.features.kitmap.streak;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class KillStreak {

    private int neededKills;
    private String name;
    private List<String> commands;

}
