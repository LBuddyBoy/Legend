package dev.lbuddyboy.legend.team.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@AllArgsConstructor
@Getter
public enum WaypointType {

    HOME("Home", Color.BLUE),
    RALLY("Rally", Color.MAGENTA),
    FOCUSED("Focused Team", Color.PINK);

    private final String waypointName;
    private final Color color;

}
