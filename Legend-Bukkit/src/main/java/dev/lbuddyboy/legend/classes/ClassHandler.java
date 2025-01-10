package dev.lbuddyboy.legend.classes;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.SettingsConfig;
import dev.lbuddyboy.legend.classes.impl.*;
import dev.lbuddyboy.legend.classes.listener.PvPClassListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ClassHandler implements IModule {

    private List<PvPClass> classes;
    private Map<UUID, PvPClass> activeClasses;
    private File directory;

    @Override
    public void load() {
        this.activeClasses = new ConcurrentHashMap<>();
        this.classes = new ArrayList<>();

        this.directory = new File(LegendBukkit.getInstance().getDataFolder(), "classes");
        if (!this.directory.exists()) this.directory.mkdir();

        reload();

        Tasks.runTimer(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!this.activeClasses.containsKey(player.getUniqueId())) {
                    if (player.getInventory().getArmorContents() == null) continue;
                    if (player.getInventory().getArmorContents().length < 4) continue;

                    for (PvPClass pvpClass : this.classes) {
                        if (!pvpClass.hasSetOn(player)) continue;
                        if (!pvpClass.shouldApply(player)) continue;

                        pvpClass.apply(player);
                    }

                    continue;
                }
                PvPClass active = this.activeClasses.get(player.getUniqueId());
                if (active.hasSetOn(player)) {
                    if (active.isTickable()) active.tick(player);
                    continue;
                }

                active.remove(player);
                this.activeClasses.remove(player.getUniqueId());
            }
        }, 5, 5);

        LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(new PvPClassListener(), LegendBukkit.getInstance());
    }

    @Override
    public void reload() {
        this.classes.forEach(HandlerList::unregisterAll);
        this.classes.clear();

        new MinerClass();
        new BardClass();
        new ArcherClass();
        new RogueClass();
        new HunterClass();
        new DiverClass();

        this.classes.forEach(c -> LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(c, LegendBukkit.getInstance()));
    }

    @Override
    public void unload() {
        this.activeClasses.entrySet().stream().filter(e -> Bukkit.getPlayer(e.getKey()) != null).forEach(e -> e.getValue().remove(Bukkit.getPlayer(e.getKey())));
    }

    public <T extends PvPClass> T getClass(Class<T> t) {
        return this.classes.stream().filter(c -> c.getClass().equals(t)).findFirst().map(t::cast).orElse(null);
    }

    public PvPClass getClassApplied(Player player) {
        return this.activeClasses.getOrDefault(player.getUniqueId(), null);
    }

    public boolean isClassApplied(Player player) {
        return this.activeClasses.containsKey(player.getUniqueId());
    }

    public <T extends PvPClass> boolean isClassApplied(Player player, Class<T> clazz) {
        if (!this.activeClasses.containsKey(player.getUniqueId())) return false;

        return clazz.equals(this.activeClasses.get(player.getUniqueId()).getClass());
    }

}
