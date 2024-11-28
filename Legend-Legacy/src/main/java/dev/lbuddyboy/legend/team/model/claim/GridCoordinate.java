package dev.lbuddyboy.legend.team.model.claim;

import lombok.Data;

@Data
public class GridCoordinate {

    public static int BITS = 6;

    private final int x, z;

    public GridCoordinate(int x, int z) {
        this.x = x >> BITS;
        this.z = z >> BITS;
    }

}
