package dev.lbuddyboy.legend.features.chatgames.model.impl;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.chatgames.model.AbstractChatGame;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.checkerframework.common.value.qual.StringVal;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MathChatGame extends AbstractChatGame {

    private Equation currentEquation;
    private long startedAt;

    @Override
    public String getId() {
        return "math";
    }

    @Override
    public void loadDefaults() {
        this.config.addDefault("displayName", "Math Game");
        this.config.addDefault("primaryColor", "&6");
        this.config.addDefault("secondaryColor", "&e");
        this.config.addDefault("secondsToSolve", 30);

        this.config.addDefault("settings.equations.addition.min", 100);
        this.config.addDefault("settings.equations.addition.max", 5000);

        this.config.addDefault("settings.equations.subtraction.min", 100);
        this.config.addDefault("settings.equations.subtraction.max", 5000);

        this.config.addDefault("settings.equations.division.min", 10);
        this.config.addDefault("settings.equations.division.max", 50);

        this.config.addDefault("settings.equations.multiplication.min", 10);
        this.config.addDefault("settings.equations.multiplication.max", 50);

        this.config.addDefault("lang.started", Arrays.asList(
                " ",
                "<blend:&6;&e>&lMATH GAME</>",
                "&7First to solve the equation will receive",
                "&7a reward!",
                " ",
                "<blend:&6;&e>The equation to solve is:</>",
                "&7%equation%",
                " ",
                "&7&o(( Round to the 2nd decimal place for division ))",
                " "
        ));

        this.config.addDefault("lang.ended", Arrays.asList(
                " ",
                "<blend:&6;&e>&lMATH GAME</>",
                " ",
                "<blend:&6;&e>%winner% solved the equation</>",
                "&7%equation_answer%",
                " "
        ));
    }

    @Override
    public void start() {
        int equations = EquationType.values().length;
        EquationType type = EquationType.values()[ThreadLocalRandom.current().nextInt(equations)];

        int numberOne = ThreadLocalRandom.current().nextInt(
                this.config.getInt("settings.equations." + type.name().toLowerCase() + ".min"),
                this.config.getInt("settings.equations." + type.name().toLowerCase() + ".max")
        );

        int numberTwo = ThreadLocalRandom.current().nextInt(
                this.config.getInt("settings.equations." + type.name().toLowerCase() + ".min"),
                this.config.getInt("settings.equations." + type.name().toLowerCase() + ".max")
        );

        this.currentEquation = new Equation(
                numberOne,
                numberTwo,
                type
        );

        this.config.getStringList("lang.started").forEach(s -> {
            Bukkit.broadcastMessage(CC.translate(s
                    .replaceAll("%equation%", this.currentEquation.getFormat())
            ));
        });

        Tasks.runLater(() -> this.end(null), 20L * this.config.getInt("secondsToSolve"));
    }

    @Override
    public void end(Player winner) {
        if (this.currentEquation == null) return;

        this.config.getStringList("lang.ended").forEach(s -> {
            Bukkit.broadcastMessage(CC.translate(s
                    .replaceAll("%winner%", (winner == null ? "No One" : winner.getName()))
                    .replaceAll("%equation_answer%", this.currentEquation.getAnswer())
                    .replaceAll("%equation%", this.currentEquation.getFormat())
            ));
        });

        if (winner != null) {
            LegendBukkit.getInstance().getChatGameHandler().getLootTable().open(winner);
        }

        this.currentEquation = null;
        this.startedAt = -1L;
    }

    @Override
    public boolean isStarted() {
        return this.currentEquation != null;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (this.currentEquation == null) return;
        if (!message.equalsIgnoreCase(this.currentEquation.getAnswer())) return;

        this.end(player);
    }

    @AllArgsConstructor
    public class Equation {

        private final int numberOne;
        private final int numberTwo;
        private final EquationType equationType;

        public String getAnswer() {
            return this.equationType.function.apply(this.numberOne, this.numberTwo);
        }

        public String getFormat() {
            return this.numberOne + " " + this.equationType.getSymbol() + " " + this.numberTwo;
        }

    }

    @AllArgsConstructor
    @Getter
    public enum EquationType {

        ADDITION("+", (n1, n2) -> String.valueOf(n1 + n2)),
        SUBTRACTION("-", (n1, n2) -> String.valueOf(n1 - n2)),
        DIVISION("/", (n1, n2) -> {
            float value = (float) n1 / n2;

            return value % 1 == 0
                    ? String.format("%.0f", value) // No decimals for whole numbers
                    : String.format("%.2f", value);
        }),
        MULTIPLICATION("*", (n1, n2) -> String.valueOf((n1 * n2)));

        private final String symbol;
        private final BiFunction<Integer, Integer, String> function;

    }

}
