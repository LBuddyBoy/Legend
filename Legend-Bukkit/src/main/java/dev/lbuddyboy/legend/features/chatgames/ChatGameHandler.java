package dev.lbuddyboy.legend.features.chatgames;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.loottable.impl.LootTable;
import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.features.chatgames.model.AbstractChatGame;
import dev.lbuddyboy.legend.features.chatgames.model.impl.MathChatGame;
import dev.lbuddyboy.legend.features.chatgames.model.impl.QuestionGame;
import dev.lbuddyboy.legend.features.chatgames.model.impl.ScrambleGame;
import lombok.Getter;
import org.bukkit.event.HandlerList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class ChatGameHandler implements IModule {

    private File directory;
    private LootTable lootTable;
    private List<AbstractChatGame> chatGames;

    @Override
    public void load() {
        this.chatGames = new ArrayList<>();
        if (!SettingsConfig.SETTINGS_CHAT_GAMES.getBoolean()) return;

        this.directory = new File(LegendBukkit.getInstance().getDataFolder(), "chatgames");
        if (!this.directory.exists()) this.directory.mkdir();

        this.lootTable = new LootTable("chatgames");
        this.lootTable.register();

        Tasks.runTimer(this::startRandom, (20 * 60) * 5, (20 * 60) * 5);

        this.reload();
    }

    @Override
    public void unload() {

    }

    @Override
    public void reload() {
        if (!SettingsConfig.SETTINGS_CHAT_GAMES.getBoolean()) return;

        this.chatGames.forEach(chatGame -> chatGame.end(null));
        this.chatGames.forEach(HandlerList::unregisterAll);
        this.chatGames.clear();

        this.chatGames.add(new MathChatGame());
        this.chatGames.add(new QuestionGame());
        this.chatGames.add(new ScrambleGame());

        this.chatGames.forEach(chatGame -> LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(chatGame, LegendBukkit.getInstance()));
    }

    public void startRandom() {
        AbstractChatGame chatGame = this.chatGames.get(ThreadLocalRandom.current().nextInt(this.chatGames.size()));

        if (chatGame.isStarted()) {
            LegendBukkit.getInstance().getLogger().info("Tried starting " + chatGame.getId() + " chat game, but it's already started.");
            return;
        }

        chatGame.start();
    }

}
