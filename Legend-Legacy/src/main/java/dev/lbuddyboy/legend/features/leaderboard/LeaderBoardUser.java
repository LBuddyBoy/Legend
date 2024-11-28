package dev.lbuddyboy.samurai.map.leaderboard;

import dev.lbuddyboy.commons.util.CC;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Data
public class LeaderBoardUser {

    private final UUID uuid;
    private final String name;
    private final double score;
    private String texture;
    private int place;

    public String getFancyPlace() {
        if (this.place == 1) {
            return CC.translate("&6❶");
        } else if (this.place == 2) {
            return CC.translate("&e❷");
        } else if (this.place == 3) {
            return CC.translate("&b❸");
        }

        return "";
    }

}
