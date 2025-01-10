package dev.lbuddyboy.legend.features.chatgames.model.impl;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.chatgames.model.AbstractChatGame;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ScrambleGame extends AbstractChatGame {

    private Scramble currentScramble;
    private long startedAt;

    @Override
    public String getId() {
        return "scramble";
    }

    @Override
    public void loadDefaults() {
        this.config.addDefault("displayName", "Scramble Game");
        this.config.addDefault("primaryColor", "&6");
        this.config.addDefault("secondaryColor", "&e");
        this.config.addDefault("secondsToSolve", 30);

        this.config.addDefault("lang.started", Arrays.asList(
                " ",
                "<blend:&6;&e>&lSCRAMBLE GAME</>",
                "&7First to unscramble the word",
                "&7correctly will receive a reward!",
                " ",
                "<blend:&6;&e>The word to unscramble is:</>",
                "&7%word%",
                " "
        ));

        this.config.addDefault("lang.ended", Arrays.asList(
                " ",
                "<blend:&6;&e>&lSCRAMBLE GAME</>",
                " ",
                "<blend:&6;&e>%winner% unscrambled the word</>",
                "&7%answer%",
                " "
        ));

        this.config.addDefault("words", Arrays.asList(
                "Minecraft",
                "Legend",
                "HCF",
                "Friend",
                "Ponder",
                "Kill",
                "Death",
                "Leaderboard",
                "Notch",
                "Jeb",
                "Invisible",
                "Golden Apple"
        ));
    }

    @Override
    public void start() {
        List<Scramble> scrambles = this.config.getStringList("words").stream().map(Scramble::new).toList();

        this.currentScramble = scrambles.get(ThreadLocalRandom.current().nextInt(scrambles.size()));

        this.config.getStringList("lang.started").forEach(s -> {
            Bukkit.broadcastMessage(CC.translate(s
                    .replaceAll("%word%", this.currentScramble.getScrambledWord())
            ));
        });

        Tasks.runLater(() -> this.end(null), 20L * this.config.getInt("secondsToSolve"));
    }

    @Override
    public void end(Player winner) {
        if (this.currentScramble == null) return;

        this.config.getStringList("lang.ended").forEach(s -> {
            Bukkit.broadcastMessage(CC.translate(s
                    .replaceAll("%winner%", (winner == null ? "No One" : winner.getName()))
                    .replaceAll("%answer%", this.currentScramble.getWord())
                    .replaceAll("%question%", this.currentScramble.getScrambledWord())
            ));
        });

        if (winner != null) {
            LegendBukkit.getInstance().getChatGameHandler().getLootTable().open(winner);
        }

        this.currentScramble = null;
        this.startedAt = -1L;
    }

    @Override
    public boolean isStarted() {
        return this.currentScramble != null;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (this.currentScramble == null) return;
        if (!message.equalsIgnoreCase(this.currentScramble.getWord())) return;

        this.end(player);
    }

    @Getter
    public class Scramble {

        private final String word;
        private final String scrambledWord;

        public Scramble(String word) {
            this.word = word;
            this.scrambledWord = scrambleWord(word); // Initialize scrambledWord by scrambling the word
        }

        private String scrambleWord(String word) {
            List<Character> characters = new ArrayList<>();
            for (char c : word.toCharArray()) {
                characters.add(c);
            }
            Collections.shuffle(characters); // Shuffle the characters
            StringBuilder scrambled = new StringBuilder();
            for (char c : characters) {
                scrambled.append(c);
            }
            return scrambled.toString();
        }
    }

}
