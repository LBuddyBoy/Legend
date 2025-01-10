package dev.lbuddyboy.legend.user.model.nametag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
@AllArgsConstructor
public enum ScoreboardEntryType {

    DEFAULT("default", ChatColor.RED, false, false, false),
    FRIENDLY("friendly", ChatColor.DARK_GREEN, true, true, false),
    ALLY("ally", ChatColor.BLUE, false, false, false),
    FOCUS("focus",  ChatColor.LIGHT_PURPLE, false, false, false),
    ARCHER_MARK("archer",  ChatColor.YELLOW, true, true, false),
    OBFUSCATED("obfuscated", ChatColor.WHITE, false, true, true);

    public final String scoreboardTeamName;
    public final ChatColor color;
    public final boolean canAlwaysSeeNametag;
    public final boolean canSeeInvisibles;
    public final boolean obfuscated;
}