package dev.lbuddyboy.legend.tab;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TabColumn {

    LEFT(0), MIDDLE(20), RIGHT(40), EXTRA(60);

    private final int start;

}
