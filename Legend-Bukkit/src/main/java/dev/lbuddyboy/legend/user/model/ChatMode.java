package dev.lbuddyboy.legend.user.model;

import dev.lbuddyboy.api.ArrowAPI;
import dev.lbuddyboy.api.user.model.User;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.leaderboard.impl.KillLeaderBoardStat;
import dev.lbuddyboy.legend.features.settings.Setting;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamMember;
import dev.lbuddyboy.legend.team.model.TeamRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum ChatMode {

    PUBLIC("Public", new String[]{"global", "g", "p"}, '!',
            (player -> Bukkit.getOnlinePlayers().stream().filter(Setting.GLOBAL_CHAT::isToggled).collect(Collectors.toList())),
            ((event, receiver) -> {
                Player player = event.getPlayer();
                Optional<Team> teamOpt = LegendBukkit.getInstance().getTeamHandler().getTeam(player);
                User user = ArrowAPI.getInstance().getUserHandler().getOrCreateUser(player.getUniqueId());
                ChatColor chatColor = ChatColor.valueOf(user.getChatColor());
                String fancyPlace = LegendBukkit.getInstance().getLeaderBoardHandler().getFancyPlace(player.getUniqueId(), KillLeaderBoardStat.class);

                String prefix = "";

                prefix += teamOpt.map(team -> CC.translate("&6[" + team.getName(receiver) + "&6] ")).orElse("");
                prefix += CC.translate(user.getDisplayName());

                return prefix + ChatColor.WHITE + ": " + chatColor + event.getMessage();
            })
    ),
    OFFICER("Officer", new String[]{"c", "o", "officer", "captain"}, '#', (player -> {
        Optional<Team> teamOpt = LegendBukkit.getInstance().getTeamHandler().getTeam(player);

        return teamOpt.map(team -> team.getOnlineMembers().stream().filter(m -> m.isAtLeast(TeamRole.CAPTAIN)).map(TeamMember::getPlayer).collect(Collectors.toList())).orElse(Collections.emptyList());
    }), ((event, receiver) -> CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.chat.officer-format")
            .replaceAll("%player%", event.getPlayer().getName())
            .replaceAll("%message%", CC.stripColor(event.getMessage()))
    ))
    ),
    TEAM("Team", new String[]{"f", "faction", "t"}, '@', (player -> {
        Optional<Team> teamOpt = LegendBukkit.getInstance().getTeamHandler().getTeam(player);

        return teamOpt.map(Team::getOnlinePlayers).orElse(Collections.emptyList());
    }), ((event, receiver) -> CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.chat.team-format")
            .replaceAll("%player%", LegendBukkit.getInstance().getTeamHandler().getTeam(event.getPlayer()).map(team -> team.getMember(event.getPlayer().getUniqueId()).get().getDisplayName()).orElse(event.getPlayer().getName()))
            .replaceAll("%message%", CC.stripColor(event.getMessage())
            )))
    ),
    ALLY("Ally", new String[]{"a", "alliance"}, '$', (player -> {
        Optional<Team> teamOpt = LegendBukkit.getInstance().getTeamHandler().getTeam(player);
        List<Player> players = new ArrayList<>(teamOpt.map(Team::getAllies).map(ally -> ally.stream().map(Team::getOnlinePlayers).flatMap(Collection::stream).collect(Collectors.toList())).orElse(Collections.emptyList()));

        teamOpt.ifPresent(team -> players.addAll(team.getOnlinePlayers()));

        return players;
    }), ((event, receiver) -> CC.translate(LegendBukkit.getInstance().getLanguage().getString("team.chat.ally-format")
            .replaceAll("%player%", event.getPlayer().getName())
            .replaceAll("%message%", CC.stripColor(event.getMessage()))
    )));

    private final String name;
    private final String[] aliases;
    private final char identifier;
    private final Function<Player, List<Player>> recipientFunction;
    private final BiFunction<AsyncPlayerChatEvent, Player, String> formatFunction;

    public List<String> getIdentifiers() {
        return Arrays.asList(this.name, String.valueOf(this.name.charAt(0)).toLowerCase());
    }

    public static ChatMode findMode(String identifier) {
        for (ChatMode mode : values()) {
            for (String alias : mode.getAliases()) {
                if (alias.equalsIgnoreCase(identifier)) return mode;
            }
            if (mode.getName().equalsIgnoreCase(identifier) || identifier.charAt(0) == mode.getName().charAt(0))
                return mode;
        }
        return valueOf(identifier.toUpperCase());
    }

}
