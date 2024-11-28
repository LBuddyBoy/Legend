package dev.lbuddyboy.legend.user.model;

import dev.lbuddyboy.api.ArrowAPI;
import dev.lbuddyboy.api.user.User;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.settings.Setting;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamMember;
import dev.lbuddyboy.legend.team.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum ChatMode {

    PUBLIC("Public", '!',
            (player -> Bukkit.getOnlinePlayers().stream().filter(Setting.GLOBAL_CHAT::isToggled).collect(Collectors.toUnmodifiableList())),
            ((event, receiver) -> {
                Player player = event.getPlayer();
                Optional<Team> teamOpt = LegendBukkit.getInstance().getTeamHandler().getTeam(player);
                User user = ArrowAPI.getInstance().getUserHandler().getOrCreateUser(player.getUniqueId());
                ChatColor chatColor = ChatColor.valueOf(user.getChatColor());
                String prefix = "";

                prefix += teamOpt.map(team -> CC.translate("&6[" + team.getName(receiver) + "] ")).orElse("");
                prefix += CC.translate(user.getDisplayName());

                return prefix + ChatColor.WHITE + ": " + chatColor + event.getMessage();
            })
    ),
    OFFICER("Captain", '#', (player -> {
        Optional<Team> teamOpt = LegendBukkit.getInstance().getTeamHandler().getTeam(player);

        return teamOpt.map(team -> team.getOnlineMembers().stream().filter(m -> m.isAtLeast(TeamRole.CAPTAIN)).map(TeamMember::getPlayer).toList()).orElse(Collections.emptyList());
    }), ((event, receiver) -> CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.chat.officer-format"))
            .replaceAll("%player%", event.getPlayer().getName())
            .replaceAll("%message%", CC.stripColor(event.getMessage())))
    ),
    TEAM("Team", '@', (player -> {
        Optional<Team> teamOpt = LegendBukkit.getInstance().getTeamHandler().getTeam(player);

        return teamOpt.map(Team::getOnlinePlayers).orElse(Collections.emptyList());
    }), ((event, receiver) -> CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.chat.team-format"))
            .replaceAll("%player%", event.getPlayer().getName())
            .replaceAll("%message%", CC.stripColor(event.getMessage())))
    ),
    ALLY("Ally", '$', (player -> {
        Optional<Team> teamOpt = LegendBukkit.getInstance().getTeamHandler().getTeam(player);

        // TODO: IMPLEMENT

        return teamOpt.map(team -> team.getOnlineMembers().stream().filter(m -> m.isAtLeast(TeamRole.CAPTAIN)).map(TeamMember::getPlayer).toList()).orElse(Collections.emptyList());
    }), ((event, receiver) -> CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.chat.ally-format"))
            .replaceAll("%player%", event.getPlayer().getName())
            .replaceAll("%message%", CC.stripColor(event.getMessage())))

    );

    private final String name;
    private final char identifier;
    private final Function<Player, List<Player>> recipientFunction;
    private final BiFunction<AsyncPlayerChatEvent, Player, String> formatFunction;

}
