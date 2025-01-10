package dev.lbuddyboy.legend.scoreboard;

import dev.lbuddyboy.commons.scoreboard.ScoreboardImpl;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.events.Events;
import dev.lbuddyboy.events.IEvent;
import dev.lbuddyboy.events.citadel.Citadel;
import dev.lbuddyboy.events.conquest.Conquest;
import dev.lbuddyboy.events.ctp.CaptureThePoint;
import dev.lbuddyboy.events.dps.DPS;
import dev.lbuddyboy.events.dtc.DTC;
import dev.lbuddyboy.events.koth.KoTH;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.classes.impl.BardClass;
import dev.lbuddyboy.legend.features.settings.Setting;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.timer.PlayerTimer;
import dev.lbuddyboy.legend.timer.ServerTimer;
import dev.lbuddyboy.legend.timer.server.SOTWTimer;
import dev.lbuddyboy.legend.user.model.LegendUser;
import dev.lbuddyboy.legend.util.PlaceholderUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LegendScoreboard implements ScoreboardImpl {

    @Override
    public boolean qualifies(Player player) {
        return true;
    }

    @Override
    public int getWeight() {
        return 5000;
    }

    @Override
    public String getTitle(Player player) {
        return CC.translate(LegendBukkit.getInstance().getScoreboard().getString("title"));
    }

    @Override
    public String getLegacyTitle(Player player) {
        return "";
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>(), toReturn = new ArrayList<>();
        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());
        List<KoTH> koths = Events.getInstance().getEvents().values().stream().filter(e -> e instanceof KoTH).map(e -> ((KoTH) e)).filter(IEvent::isActive).collect(Collectors.toList());
        List<CaptureThePoint> ctps = Events.getInstance().getEvents().values().stream().filter(e -> e instanceof CaptureThePoint).map(e -> ((CaptureThePoint) e)).filter(IEvent::isActive).collect(Collectors.toList());
        List<DTC> dtcs = Events.getInstance().getEvents().values().stream().filter(e -> e instanceof DTC).map(e -> ((DTC) e)).filter(IEvent::isActive).collect(Collectors.toList());
        List<Citadel> citadels = Events.getInstance().getEvents().values().stream().filter(e -> e instanceof Citadel).map(e -> ((Citadel) e)).filter(IEvent::isActive).collect(Collectors.toList());
        Team team = LegendBukkit.getInstance().getTeamHandler().getTeam(player.getUniqueId()).orElse(null);

        if (user.isDeathBanned()) {
            lines.addAll(LegendBukkit.getInstance().getScoreboard().getStringList("deathban"));
        } else {
            if (Setting.SCOREBOARD_CLAIM_AT.isToggled(player)) {
                if (LegendBukkit.getInstance().getScoreboard().getBoolean("claim.enabled")) {
                    lines.addAll(LegendBukkit.getInstance().getScoreboard().getStringList("claim.lines"));
                }
            }

            for (PlayerTimer timer : LegendBukkit.getInstance().getTimerHandler().getScoreboardTimers()) {
                if (!timer.isActive(player.getUniqueId())) continue;

                lines.add(timer.format(player.getUniqueId(), LegendBukkit.getInstance().getScoreboard().getString("timer-format")));
            }

            for (ServerTimer timer : LegendBukkit.getInstance().getTimerHandler().getScoreboardServerTimers()) {
                if (!timer.isActive()) continue;

                if (timer instanceof SOTWTimer) {
                    if (((SOTWTimer) timer).isEnabled(player)) {
                        lines.add(timer.format(LegendBukkit.getInstance().getScoreboard().getString("sotw-timer-format.enabled")));
                        continue;
                    }
                    lines.add(timer.format(LegendBukkit.getInstance().getScoreboard().getString("sotw-timer-format.normal")));
                    continue;
                }

                lines.add(timer.format(LegendBukkit.getInstance().getScoreboard().getString("server-timer-format")));
            }
        }

        if (LegendBukkit.getInstance().getClassHandler().isClassApplied(player)) {
            lines.addAll(LegendBukkit.getInstance().getClassHandler().getClassApplied(player).getScoreboardLines(player));
        }

        if (!koths.isEmpty()) {
            KoTH koTH = koths.get(0);

            for (String s : LegendBukkit.getInstance().getScoreboard().getStringList("koth")) {
                lines.add(koTH.applyPlaceHolders(s
                        .replaceAll("%name%", koTH.getDisplayName())
                ));
            }
        }

        if (!ctps.isEmpty()) {
            CaptureThePoint ctp = ctps.getFirst();
            String tickets = team == null ? "" : "&9[" + ctp.getTickets().getOrDefault(team.getId(), 0) + "]";

            if (ctp.getTickets().isEmpty()) {
                for (String s : LegendBukkit.getInstance().getScoreboard().getStringList("ctp_no_capper")) {
                    lines.add(ctp.applyPlaceHolders(s
                            .replaceAll("%name%", ctp.getDisplayName())
                            .replaceAll("%display_tickets%", tickets)
                            .replaceAll("%tickets%", String.valueOf((team == null ? 0 : ctp.getTickets().getOrDefault(team.getId(), 0))))
                    ));
                }
            } else if (ctp.isContested()) {
                for (String s : LegendBukkit.getInstance().getScoreboard().getStringList("ctp_contested")) {
                    lines.add(ctp.applyPlaceHolders(s
                            .replaceAll("%name%", ctp.getDisplayName())
                            .replaceAll("%display_tickets%", tickets)
                            .replaceAll("%tickets%", String.valueOf((team == null ? 0 : ctp.getTickets().getOrDefault(team.getId(), 0))))
                    ));
                }
            } else {
                for (String s : LegendBukkit.getInstance().getScoreboard().getStringList("ctp")) {
                    lines.add(ctp.applyPlaceHolders(s
                            .replaceAll("%name%", ctp.getDisplayName())
                            .replaceAll("%display_tickets%", tickets)
                            .replaceAll("%tickets%", String.valueOf((team == null ? 0 : ctp.getTickets().getOrDefault(team.getId(), 0))))
                    ));
                }
            }
        }

        if (!dtcs.isEmpty()) {
            DTC dtc = dtcs.getFirst();

            for (String s : LegendBukkit.getInstance().getScoreboard().getStringList("dtc")) {
                lines.add(dtc.applyPlaceHolders(s
                        .replaceAll("%name%", dtc.getDisplayName())
                ));
            }
        }

        if (!citadels.isEmpty()) {
            Citadel citadel = citadels.get(0);

            for (String s : LegendBukkit.getInstance().getScoreboard().getStringList("citadel")) {
                lines.add(citadel.applyPlaceHolders(s
                        .replaceAll("%name%", citadel.getName())
                ));
            }
        }

        Conquest conquest = Events.getInstance().getConquest();

        if (conquest.isActive()) {
            lines.addAll(LegendBukkit.getInstance().getScoreboard().getStringList("conquest").stream().map(conquest::applyPlaceHolders).toList());
        }

        DPS dps = Events.getInstance().getDps();

        if (dps.isActive()) {
            lines.addAll(LegendBukkit.getInstance().getScoreboard().getStringList("dps").stream().map(dps::applyPlaceHolders).toList());
        }

        if (team != null && team.getFocusedTeam() != null) {
            if (!lines.isEmpty()) lines.add(" ");

            if (team.getFocusedTeam().getTeamType() == TeamType.PLAYER) {
                lines.addAll(LegendBukkit.getInstance().getScoreboard().getStringList("player_focus").stream().map(s -> team.getFocusedTeam().applyPlaceholders(s, player)).toList());
            } else {
                lines.addAll(LegendBukkit.getInstance().getScoreboard().getStringList("system_focus").stream().map(s -> team.getFocusedTeam().applyPlaceholders(s, player)).toList());
            }
        }

        if (!lines.isEmpty()) {
            if (LegendBukkit.getInstance().getScoreboard().getBoolean("separator.enabled")) {
                toReturn.add(LegendBukkit.getInstance().getScoreboard().getString("separator.text"));
            } else {
                toReturn.add(" ");
            }

            toReturn.addAll(lines);

            if (LegendBukkit.getInstance().getScoreboard().getBoolean("footer.enabled")) {
                toReturn.addAll(LegendBukkit.getInstance().getScoreboard().getStringList("footer.lines"));
            }

            if (LegendBukkit.getInstance().getScoreboard().getBoolean("separator.enabled")) {
                toReturn.add(LegendBukkit.getInstance().getScoreboard().getString("separator.text"));
            }
        }

        return CC.translate(toReturn.stream().map(s -> PlaceholderUtil.applyAllPlaceholders(s, player)).collect(Collectors.toList()));
    }

    @Override
    public List<String> getLegacyLines(Player player) {
        return List.of();
    }
}
