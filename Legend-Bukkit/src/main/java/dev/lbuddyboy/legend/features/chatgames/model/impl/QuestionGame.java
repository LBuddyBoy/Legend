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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;

public class QuestionGame extends AbstractChatGame {

    private Question currentQuestion;
    private long startedAt;

    @Override
    public String getId() {
        return "question";
    }

    @Override
    public void loadDefaults() {
        this.config.addDefault("displayName", "Question Game");
        this.config.addDefault("primaryColor", "&6");
        this.config.addDefault("secondaryColor", "&e");
        this.config.addDefault("secondsToSolve", 30);

        this.config.addDefault("lang.started", Arrays.asList(
                " ",
                "<blend:&6;&e>&lQUESTION GAME</>",
                "&7First to answer the question",
                "&7correctly will receive a reward!",
                " ",
                "<blend:&6;&e>The question to answer is:</>",
                "&7%question%",
                " "
        ));

        this.config.addDefault("lang.ended", Arrays.asList(
                " ",
                "<blend:&6;&e>&lQUESTION GAME</>",
                " ",
                "<blend:&6;&e>%winner% answered the question</>",
                "&7%answer%",
                " "
        ));

        this.config.addDefault("questions", Arrays.asList(
                "What item is required to make an Enchantment Table?;Book",
                "What is the maximum level of Fortune enchantment?;3",
                "Which hostile mob drops Blaze Rods?;Blaze",
                "What is the name of the dimension where Endermen spawn most frequently?;The End",
                "What tool is primarily used to mine Obsidian?;Diamond Pickaxe",
                "What is the rarest ore in the Overworld?;Emerald",
                "How many hearts does a player have in total?;10",
                "Which food item restores the most hunger points?;Cooked Porkchop",
                "What is the maximum number of items in a stack of Cobblestone?;64",
                "What is the name of the block that can push other blocks when powered by Redstone?;Piston",
                "What is the default height of a Nether Portal?;4",
                "Which material is needed to craft a Beacon?;Glass",
                "What is the name of the boss mob found in an Ocean Monument?;Elder Guardian",
                "What is the name of the effect you get from eating a Golden Apple?;Regeneration",
                "Which mob explodes when close to a player?;Creeper",
                "What do you need to craft a Shield?;Wood Planks",
                "Which block can prevent Endermen from teleporting?;Leaves",
                "How many blocks of Iron are needed to craft an Anvil?;3",
                "What is the name of the flower used to create Blue Dye?;Cornflower",
                "Which biome is home to Polar Bears?;Snowy Tundra",
                "What is the primary ingredient for crafting TNT?;Gunpowder",
                "How many Obsidian blocks are needed to make a Nether Portal frame?;10",
                "Which enchantment allows tools to repair themselves over time?;Mending",
                "What is the maximum build height in Minecraft (as of 1.20)?;320",
                "What is the name of the biome where you can find Striders?;Nether Wastes",
                "What is required to cure a Zombie Villager?;Golden Apple",
                "Which item allows you to breathe underwater?;Turtle Helmet",
                "What is the name of the block that can store experience?;Sculk Catalyst",
                "What is the main ingredient for crafting an Eye of Ender?;Ender Pearl",
                "What is the name of the potion that makes you invisible?;Invisibility Potion"
        ));
    }

    @Override
    public void start() {
        List<Question> questions = this.config.getStringList("questions").stream().map(s -> {
            String[] parts = s.split(";");

            return new Question(parts[0], parts[1]);
        }).toList();

        this.currentQuestion = questions.get(ThreadLocalRandom.current().nextInt(questions.size()));

        this.config.getStringList("lang.started").forEach(s -> {
            Bukkit.broadcastMessage(CC.translate(s
                    .replaceAll("%question%", this.currentQuestion.getQuestion())
            ));
        });

        Tasks.runLater(() -> this.end(null), 20L * this.config.getInt("secondsToSolve"));
    }

    @Override
    public void end(Player winner) {
        if (this.currentQuestion == null) return;

        this.config.getStringList("lang.ended").forEach(s -> {
            Bukkit.broadcastMessage(CC.translate(s
                    .replaceAll("%winner%", (winner == null ? "No One" : winner.getName()))
                    .replaceAll("%answer%", this.currentQuestion.getAnswer())
                    .replaceAll("%question%", this.currentQuestion.getQuestion())
            ));
        });

        if (winner != null) {
            LegendBukkit.getInstance().getChatGameHandler().getLootTable().open(winner);
        }

        this.currentQuestion = null;
        this.startedAt = -1L;
    }

    @Override
    public boolean isStarted() {
        return this.currentQuestion != null;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (this.currentQuestion == null) return;
        if (!message.equalsIgnoreCase(this.currentQuestion.getAnswer())) return;

        this.end(player);
    }

    @AllArgsConstructor
    @Getter
    public class Question {

        private final String question, answer;

    }

}
