package dev.lbuddyboy.legend.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Data
public class Coordinate {

    int x;
    int z;

    @Override
    public String toString() {
        return (x + ", " + z);
    }

}