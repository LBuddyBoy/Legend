package dev.lbuddyboy.legend.scoreboard;

import dev.lbuddyboy.commons.scoreboard.ScoreboardImpl;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.util.PlaceholderUtil;
import dev.lbuddyboy.minigames.MiniGames;
import dev.lbuddyboy.minigames.type.MiniGame;
import org.bukkit.entity.Player;

import java.util.List;

public class MiniGameScoreboard implements ScoreboardImpl {

    @Override
    public boolean qualifies(Player player) {
        return MiniGames.getInstance().getMiniGame(player).isPresent();
    }

    @Override
    public int getWeight() {
        return 1500;
    }

    @Override
    public String getTitle(Player player) {
        MiniGame miniGame = MiniGames.getInstance().getMiniGame(player).orElse(null);
        return miniGame == null ? "" : this.applyPlaceHolders(miniGame.getScoreboardTitle(player), player);
    }

    @Override
    public String getLegacyTitle(Player player) {
        MiniGame miniGame = MiniGames.getInstance().getMiniGame(player).orElse(null);
        return miniGame == null ? "" : this.applyPlaceHolders(miniGame.getLegacyScoreboardTitle(player), player);
    }

    @Override
    public List<String> getLines(Player player) {
        MiniGame miniGame = MiniGames.getInstance().getMiniGame(player).orElse(null);
        return miniGame == null ? List.of() : CC.translate(miniGame.getScoreboardLines(player).stream().map(s -> applyPlaceHolders(s, player)).toList());
    }

    @Override
    public List<String> getLegacyLines(Player player) {
        MiniGame miniGame = MiniGames.getInstance().getMiniGame(player).orElse(null);
        return miniGame == null ? List.of() : CC.translate(miniGame.getLegacyScoreboardLines(player).stream().map(s -> applyPlaceHolders(s, player)).toList());
    }

    public String applyPlaceHolders(String s, Player player) {
        return PlaceholderUtil.applyAllPlaceholders(s, player);
    }

}
