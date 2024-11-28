package dev.lbuddyboy.legend.team.model;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.util.UUIDUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TeamMember {

    private final UUID uuid;
    private long joinedAt;
    private TeamRole role;

    public TeamMember(UUID uuid) {
        this(uuid, TeamRole.MEMBER);
    }

    public TeamMember(UUID uuid, TeamRole role) {
        this(uuid, System.currentTimeMillis(), role);
    }

    public boolean isAtLeast(TeamRole role) {
        return role.isGreaterOrEqual(this, role);
    }

    public boolean isOnline() {
        return Bukkit.getPlayer(this.uuid) != null;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public String getName() {
        return UUIDUtils.name(this.uuid);
    }

    public ChatColor getColor() {
        ChatColor color = ChatColor.GRAY;

        if (LegendBukkit.getInstance().getUserHandler().getUser(this.uuid).isTimerActive("deathban")) color = ChatColor.RED;
        else if (isOnline()) color = ChatColor.GREEN;

        return color;
    }

    public String getDisplayName() {
        return getColor() + this.role.getAstrix() + getName();
    }

}
